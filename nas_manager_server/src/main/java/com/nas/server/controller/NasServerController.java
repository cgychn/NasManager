package com.nas.server.controller;

import com.nas.server.entity.DirShareRequest;
import com.nas.server.entity.ListFileRequest;
import com.nas.server.service.NasServerService;
import com.nas.server.util.RespRes;
import com.nas.server.util.StringUtil;
import com.nas.server.util.dir_share.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("setPartitionAutoMount")
    public Map<String, Object> setPartitionAutoMount (@RequestParam("partitionUUID") String partitionUUID,
                                                      @RequestParam("mountPoint") String mountPoint,
                                                      @RequestParam(value = "fsType", defaultValue = "", required = false) String fsType,
                                                      @RequestParam(value = "options", defaultValue = "", required = false) String options) {
        try {
            if (StringUtil.isEmpty(partitionUUID) || StringUtil.isEmpty(mountPoint)) {
                return RespRes.error(null, "");
            }
            serverService.setAutoMount(partitionUUID, mountPoint, fsType, options);
            return RespRes.success(null, "");
        } catch (Exception e) {
            logger.error("设置分区 " + partitionUUID + " 自动挂载失败", e);
            return RespRes.error(null, "");
        }
    }

    @GetMapping("removePartitionAutoMount")
    public Map<String, Object> removePartitionAutoMount (@RequestParam("partitionUUID") String partitionUUID) {
        try {
            if (StringUtil.isEmpty(partitionUUID)) {
                return RespRes.error(null, "");
            }
            serverService.deleteAutoMountByUUID(partitionUUID);
            return RespRes.success(null, "");
        } catch (Exception e) {
            logger.error("移除分区 " + partitionUUID + " 自动挂载失败", e);
            return RespRes.error(null, "");
        }
    }

    @GetMapping("getLocalDeviceAutoMountList")
    public Map<String, Object> getLocalDeviceAutoMountList() {
        try {
            return RespRes.success(serverService.getAutoMountList(), "");
        } catch (Exception e) {
            logger.error("", e);
            return RespRes.error(null, "");
        }
    }

    @GetMapping("getNetMountList")
    public Map<String, Object> getNetMountList() {
        try {
            return RespRes.success(serverService.getNetMountList(), "");
        } catch (Exception e) {
            logger.error("", e);
            return RespRes.error(null, "");
        }
    }

    @GetMapping("initDisk")
    public Map<String, Object> initDisk(@RequestParam("diskSeNo") String diskSeNo) {
        try {
            serverService.initDisk(diskSeNo);
            return RespRes.success(null, "");
        } catch (Exception e) {
            logger.error("", e);
            return RespRes.error(null, "");
        }
    }

    @GetMapping("createNewPartition")
    public Map<String, Object> createNewPartition(@RequestParam("diskSeNo") String diskSeNo,
                                                  @RequestParam("start") String start,
                                                  @RequestParam("end") String end) {
        try {
            serverService.createNewPartition(diskSeNo, start, end);
            return RespRes.success(null, "");
        } catch (Exception e) {
            logger.error("", e);
            return RespRes.error(null, e.getMessage());
        }
    }

    @GetMapping("removePartition")
    public Map<String, Object> removePartition (@RequestParam("diskSeNo") String diskSeNo, @RequestParam("partitionNum") int partitionNum) {
        try {
            serverService.removePartition(diskSeNo, partitionNum);
            return RespRes.success(null, "");
        } catch (Exception e) {
            logger.error("", e);
            return RespRes.error(null, e.getMessage());
        }
    }

    @GetMapping("formatPartition")
    public Map<String, Object> formatPartition (@RequestParam("diskSeNo") String diskSeNo,
                                                @RequestParam("partitionNum") int partitionNum,
                                                @RequestParam("format") String format) {
        try {
            serverService.formatPartition(diskSeNo, partitionNum, format);
            return RespRes.success(null, "");
        } catch (Exception e) {
            logger.error("", e);
            return RespRes.error(null, e.getMessage());
        }
    }

    @PostMapping("share")
    public Map<String, Object> share (@RequestBody DirShareRequest request) {
        try {
            return RespRes.success(serverService.share(request), "");
        } catch (Exception e) {
            logger.error("", e);
            return RespRes.error(null, e.getMessage());
        }
    }

    @GetMapping
    public Map<String, Object> getShareCfg (@RequestParam Protocol protocol) {
        try {
            return RespRes.success(serverService.getShareCfg(protocol), "");
        } catch (Exception e) {
            logger.error("", e);
            return RespRes.error(null, e.getMessage());
        }
    }

    @PostMapping("listFiles")
    public Map<String, Object> listFiles(@RequestBody ListFileRequest listFileRequest) {
        try {
            return RespRes.success(serverService.listFiles(listFileRequest), "");
        } catch (Exception e) {
            logger.error("", e);
            return RespRes.error(null, e.getMessage());
        }
    }
}
