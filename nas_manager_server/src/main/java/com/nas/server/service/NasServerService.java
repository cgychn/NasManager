package com.nas.server.service;

import com.nas.server.entity.Disk;
import com.nas.server.entity.MountListItem;
import com.nas.server.entity.MountPointUsage;
import com.nas.server.entity.Partition;
import com.nas.server.entity.db.AutoMount;
import com.nas.server.entity.db.NetMount;
import com.nas.server.mapper.AutoMountMapper;
import com.nas.server.mapper.NetMountMapper;
import com.nas.server.util.DriveAPI;
import com.nas.server.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NasServerService {

    @Autowired
    private AutoMountMapper autoMountMapper;

    @Autowired
    private NetMountMapper netMountMapper;

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
}
