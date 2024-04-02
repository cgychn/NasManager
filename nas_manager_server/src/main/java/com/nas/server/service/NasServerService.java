package com.nas.server.service;

import com.nas.server.entity.Disk;
import com.nas.server.entity.MountListItem;
import com.nas.server.entity.MountPointUsage;
import com.nas.server.entity.db.AutoMount;
import com.nas.server.entity.db.NetMount;
import com.nas.server.mapper.AutoMountMapper;
import com.nas.server.mapper.NetMountMapper;
import com.nas.server.util.DriveAPI;
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
        return DriveAPI.getDiskList();
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
     * 删除自动挂载
     * @param id
     */
    public void deleteAutoMount (int id) {
        autoMountMapper.deleteAutoMountById(id);
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
}
