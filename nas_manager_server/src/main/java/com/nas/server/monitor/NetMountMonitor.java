package com.nas.server.monitor;

import com.nas.server.config.ScheduleExecutorPool;
import com.nas.server.entity.MountListItem;
import com.nas.server.entity.db.NetMount;
import com.nas.server.service.NasServerService;
import com.nas.server.util.DriveAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class NetMountMonitor implements ApplicationRunner {

    private final Logger logger = LoggerFactory.getLogger(NetMountMonitor.class);

    @Autowired
    private NasServerService nasServerService;

    @Override
    public void run(ApplicationArguments args) {
        ScheduleExecutorPool.getPool().scheduleWithFixedDelay(this::processNetMount, 0, 1, TimeUnit.SECONDS);
    }

    private void processNetMount() {
        try {
            List<NetMount> netMounts = nasServerService.getNetMountList();
            for (NetMount netMount : netMounts) {
                try {
                    if (!checkMount(netMount.getMountPoint())) {
                        // 挂载
                        nasServerService.mount(netMount.getNetPath(), netMount.getMountPoint(), netMount.getFstype(), netMount.getOptions());
                    }
                } catch (Exception e) {
                    logger.error("操作网络挂载失败", e);
                }
            }
        } catch (Exception e) {
            logger.error("获取网络挂载列表失败", e);
        }
    }

    private boolean checkMount(String mountPoint) throws Exception {
        List<MountListItem> mountList = DriveAPI.getMountList(true, "", mountPoint, () -> {
            // 解除挂载
            try {
                DriveAPI.umountMountPoint(mountPoint, true);
            } catch (Exception e) {
                logger.error("超时解除挂载失败", e);
            }
        });
        return mountList.size() > 0;
    }

}
