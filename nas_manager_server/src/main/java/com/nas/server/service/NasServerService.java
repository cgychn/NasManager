package com.nas.server.service;

import com.nas.server.entity.*;
import com.nas.server.entity.db.AutoMount;
import com.nas.server.entity.db.NetMount;
import com.nas.server.enumrate.PartitionFormat;
import com.nas.server.mapper.AutoMountMapper;
import com.nas.server.mapper.NetMountMapper;
import com.nas.server.util.DriveAPI;
import com.nas.server.util.FileUtil;
import com.nas.server.util.StringUtil;
import com.nas.server.util.dir_share.DirShareFactory;
import com.nas.server.util.dir_share.Protocol;
import com.nas.server.util.dir_share.ShareServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class NasServerService {

    @Autowired
    private AutoMountMapper autoMountMapper;

    @Autowired
    private NetMountMapper netMountMapper;

    private Logger logger = LoggerFactory.getLogger(NasServerService.class);

    public List<Disk> getDiskList () throws Exception {
        List<Disk> diskList = DriveAPI.getDiskList();
        // 加载自动挂载信息
        for (Disk disk : diskList) {
            for (Partition partition : disk.getPartitions()) {
                if (!StringUtil.isEmpty(partition.getUuid())) {
                    partition.setAutoMount(getAutoMountByUuid(partition.getUuid()) != null);
                }
            }
        }
        return diskList;
    }

    public List<MountListItem> getMountList(boolean showNetMount, String partitionNum) throws Exception {
        return DriveAPI.getMountList(showNetMount, partitionNum, "");
    }

    public List<MountPointUsage> getMountPointUsage(String mountPoint) throws Exception {
        return DriveAPI.getMountPointUsage(mountPoint);
    }

    /**
     * 设置自动挂载
     * @param uuid
     * @param mountPoint
     */
    public void setAutoMount (String uuid, String mountPoint, String fstype, String options) {
        autoMountMapper.insertAutoMount(uuid, mountPoint, fstype, options);
    }

    /**
     * 通过分区uuid删除自动挂载
     * @param uuid
     */
    public void deleteAutoMountByUUID (String uuid) {
        autoMountMapper.deleteAutoMountByUuid(uuid);
    }

    /**
     * 通过挂载点删除自动挂载
     * @param mountPoint
     */
    public void deleteAutoMountByMountPoint (String mountPoint) {
        autoMountMapper.deleteAutoMountByMountPoint(mountPoint);
    }

    /**
     * 查询自动挂载
     * @param uuid
     * @return
     */
    public AutoMount getAutoMountByUuid(String uuid) {
        return autoMountMapper.getAutoMountByPartitionUuid(uuid);
    }

    public boolean umount(String mountOn) throws Exception {
        return DriveAPI.umountMountPoint(mountOn, true);
    }

    public boolean mount(String partitionNum, String mountPoint, String fstype, String options) throws Exception {
        return DriveAPI.mountPartition(partitionNum, mountPoint, fstype, options);
    }

    public List<NetMount> getNetMountList() {
        return netMountMapper.getNetMountList();
    }

    public List<AutoMount> getAutoMountList() {
        return autoMountMapper.getAutoMountList();
    }

    public void initDisk(String diskSeNo) throws Exception {
        List<Disk> diskList = getDiskList();
        for (Disk disk : diskList) {
            if (diskSeNo.equals(disk.getSENum())) {
                for (Partition partition : disk.getPartitions()) {
                    DriveAPI.removePartition(disk.getDevNum(), partition.getPartitionNumber());
                }
                break;
            }
        }
    }

    public void createNewPartition(String diskSeNo, String start, String end) throws Exception {
        List<Disk> diskList = getDiskList();
        for (Disk disk : diskList) {
            if (diskSeNo.equals(disk.getSENum())) {
                DriveAPI.createNewPartition(disk.getDevNum(), start, end);
                break;
            }
        }
    }

    public void removePartition(String diskSeNo, int partitionNum) throws Exception {
        List<Disk> diskList = getDiskList();
        for (Disk disk : diskList) {
            if (diskSeNo.equals(disk.getSENum())) {
                DriveAPI.removePartition(disk.getDevNum(), partitionNum);
                break;
            }
        }
    }

    public void formatPartition(String diskSeNo, int partitionNum, String format) throws Exception {
        List<Disk> diskList = getDiskList();
        for (Disk disk : diskList) {
            if (diskSeNo.equals(disk.getSENum())) {
                for (Partition partition : disk.getPartitions()) {
                    if (partition.getPartitionNumber() == partitionNum) {
                        DriveAPI.formatPartition(partition.getPartitionNum(), PartitionFormat.valueOf(format));
                        break;
                    }
                }
                break;
            }
        }
    }

    public boolean share(DirShareRequest request) throws Exception {
        ShareServer shareServer = DirShareFactory.generateShareUtil(request.getProtocol());
        if (shareServer != null) {
            boolean enable;
            if (!(enable = shareServer.checkProtocolEnabled())) {
                // 尝试重新启动服务
                shareServer.restartServer();
                Thread.sleep(2000);
                enable = shareServer.checkProtocolEnabled();
            }
            if (enable) {
                // 共享
                return shareServer.saveConfig(request.getCfgContent());
            }
        }
        return false;
    }

    public List<NasFile> listFiles(ListFileRequest listFileRequest) {
        if (StringUtil.isEmpty(listFileRequest.getParentFileAbsPath())) {
            listFileRequest.setParentFileAbsPath("/");
        }
        List<NasFile> res = new LinkedList<>();
        File[] listFiles = new File(listFileRequest.getParentFileAbsPath()).listFiles();
        if (listFiles != null) {
            for (File listFile : listFiles) {
                if (!StringUtil.isEmpty(listFileRequest.getFileNameFilter())) {
                    if (!Pattern.matches(listFileRequest.getFileNameFilter(), listFile.getName())) {
                        continue;
                    }
                }
                NasFile nasFile = new NasFile();
                nasFile.setAbsFilePath(listFile.getAbsolutePath());
                nasFile.setFileName(listFile.getName());
                if (listFile.isFile()) {
                    try {
                        nasFile.setFileSize(Files.size(listFile.toPath()));
                    } catch (Exception e) {
                        logger.error("获取文件大小出错", e);
                        nasFile.setFileSize(0);
                    }
                } else {
                    nasFile.setFileSize(0);
                }
                nasFile.setDir(listFile.isDirectory());
                nasFile.setDataTime(new Date(listFile.lastModified()));
                PosixFileAttributes fileAttributes = FileUtil.getFileAttributes(listFile);
                if (fileAttributes != null) {
                    nasFile.setOwner(fileAttributes.owner().getName());
                    nasFile.setGroup(fileAttributes.group().getName());
                    Set<PosixFilePermission> permissions = fileAttributes.permissions();
                    nasFile.setOwnerRead(permissions.contains(PosixFilePermission.OWNER_READ));
                    nasFile.setOwnerExecute(permissions.contains(PosixFilePermission.OWNER_EXECUTE));
                    nasFile.setOwnerWrite(permissions.contains(PosixFilePermission.OWNER_WRITE));
                    nasFile.setGroupRead(permissions.contains(PosixFilePermission.GROUP_READ));
                    nasFile.setGroupWrite(permissions.contains(PosixFilePermission.GROUP_WRITE));
                    nasFile.setGroupExecute(permissions.contains(PosixFilePermission.GROUP_EXECUTE));
                    nasFile.setOtherExecute(permissions.contains(PosixFilePermission.OTHERS_EXECUTE));
                    nasFile.setOtherRead(permissions.contains(PosixFilePermission.OTHERS_READ));
                    nasFile.setOtherWrite(permissions.contains(PosixFilePermission.OTHERS_WRITE));
                }
                res.add(nasFile);
            }
        }
        return res;
    }

    public ShareConfig getShareCfg(Protocol protocol) {
        try {
            ShareServer shareServer = DirShareFactory.generateShareUtil(protocol);
            return shareServer.toShareConfig();
        } catch (Exception e) {
            logger.error("获取共享配置失败", e);
            return null;
        }
    }
}
