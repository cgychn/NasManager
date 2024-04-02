package com.nas.server.controller;

import com.nas.server.service.NasServerService;
import com.nas.server.util.RespRes;
import com.nas.server.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("nasServer")
public class NasServerController {

    private final Logger logger = LoggerFactory.getLogger(NasServerController.class);

    @Autowired
    private NasServerService serverService;

    @GetMapping("serverTime")
    public Map<String, Object> getServerTime () {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return RespRes.success(simpleDateFormat.format(new Date()), "");
    }

    @GetMapping("diskList")
    public Map<String, Object> diskList () {
        try {
            return RespRes.success(serverService.getDiskList(), "");
        } catch (Exception e) {
            logger.error("获取磁盘列表失败", e);
            return RespRes.error(null, "");
        }
    }

    @GetMapping("mountList")
    public Map<String, Object> mountList (@RequestParam(value = "showNetMount", required = false) Boolean showNetMount,
                                          @RequestParam(value = "partitionNum", required = false) String partitionNum) {
        if (showNetMount == null) {
            showNetMount = true;
        }
        try {
            return RespRes.success(serverService.getMountList(showNetMount, partitionNum), "");
        } catch (Exception e) {
            logger.error("获取挂载列表失败", e);
            return RespRes.error(null, "");
        }
    }

    @GetMapping("getMountPointUsage")
    public Map<String, Object> getMountPointUsage (@RequestParam("mountPoint") String mountPoint) {
        try {
            if (StringUtil.isEmpty(mountPoint, true)) {
                return RespRes.error(null, "");
            }
            return RespRes.success(serverService.getMountPointUsage(mountPoint), "");
        } catch (Exception e) {
            logger.error("获取挂载点占用失败", e);
            return RespRes.error(null, "");
        }
    }

}
