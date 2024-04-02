package com.nas.server.entity;

public class MountListItem {

    private String fileSystem;
    private String size;
    private String used;
    private String avail;
    private float usePercent;
    private String mountOn;

    public String getFileSystem() {
        return fileSystem;
    }

    public void setFileSystem(String fileSystem) {
        this.fileSystem = fileSystem;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getUsed() {
        return used;
    }

    public void setUsed(String used) {
        this.used = used;
    }

    public String getAvail() {
        return avail;
    }

    public void setAvail(String avail) {
        this.avail = avail;
    }

    public float getUsePercent() {
        return usePercent;
    }

    public void setUsePercent(float usePercent) {
        this.usePercent = usePercent;
    }

    public String getMountOn() {
        return mountOn;
    }

    public void setMountOn(String mountOn) {
        this.mountOn = mountOn;
    }
}
