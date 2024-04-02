package com.nas.server.entity.db;

public class AutoMount {

    private int id;
    private String partitionUuid;
    private String mountPoint;
    private String fstype;
    private String otherOptions;

    public String getFstype() {
        return fstype;
    }

    public void setFstype(String fstype) {
        this.fstype = fstype;
    }

    public String getOtherOptions() {
        return otherOptions;
    }

    public void setOtherOptions(String otherOptions) {
        this.otherOptions = otherOptions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPartitionUuid() {
        return partitionUuid;
    }

    public void setPartitionUuid(String partitionUuid) {
        this.partitionUuid = partitionUuid;
    }

    public String getMountPoint() {
        return mountPoint;
    }

    public void setMountPoint(String mountPoint) {
        this.mountPoint = mountPoint;
    }
}
