package com.nas.server.monitor;

import com.nas.server.config.ScheduleExecutorPool;
import com.nas.server.entity.Disk;
import com.nas.server.entity.MountListItem;
import com.nas.server.entity.Partition;
import com.nas.server.entity.db.AutoMount;
import com.nas.server.service.NasServerService;
import com.nas.server.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class DiskMonitor implements ApplicationRunner {
    private final Logger logger = LoggerFactory.getLogger(DiskMonitor.class);

    @Autowired
    private NasServerService nasServerService;

    private List<Disk> lastTimeDiskList;

    @Override
    public void run(ApplicationArguments args) {
        ScheduleExecutorPool.getPool().scheduleWithFixedDelay(this::processHardDisk, 0, 1, TimeUnit.SECONDS);
    }

    private void processHardDisk() {
        try {
            List<Disk> diskList = nasServerService.getDiskList();
            if (lastTimeDiskList != null) {
                for (Disk oldDisk : lastTimeDiskList) {
                    if (diskList.stream().noneMatch(disk -> oldDisk.getSENum().equals(disk.getSENum()))) {
                        for (Partition partition : oldDisk.getPartitions()) {
                            try {
                                nasServerService.umount(partition.getMountOn());
                            } catch (Exception e) {
                                logger.error("卸载失败", e);
                            }
                        }
                    }
                }
            }
            lastTimeDiskList = diskList;
            for (Disk disk : diskList) {
                for (Partition partition : disk.getPartitions()) {
                    try {
                        if (!StringUtil.isEmpty(partition.getUuid(), true)) {
                            // 查询是否自动挂载
                            AutoMount autoMount = nasServerService.getAutoMountByUuid(partition.getUuid());
                            if (autoMount != null) {
                                // 自动挂载
                                List<MountListItem> mountList = nasServerService.getMountList(false, partition.getPartitionNum());
                                if (mountList.size() == 0) {
                                    // 挂载
                                    nasServerService.mount(partition.getPartitionNum(), autoMount.getMountPoint(), autoMount.getFstype(), autoMount.getOtherOptions());
                                } else if (!mountList.stream().allMatch(mountListItem -> autoMount.getMountPoint().equals(mountListItem.getMountOn()))) {
                                    // 卸载目录相关所有挂载点
                                    mountList.forEach(mountListItem -> {
                                        try {
                                            nasServerService.umount(mountListItem.getMountOn());
                                        } catch (Exception e) {
                                            logger.error("卸载失败", e);
                                        }
                                    });
                                    // 重新挂载
                                    nasServerService.mount(partition.getPartitionNum(), autoMount.getMountPoint(), autoMount.getFstype(), autoMount.getOtherOptions());
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.error("挂载分区失败", e);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("监控磁盘列表失败", e);
        }
    }
}
