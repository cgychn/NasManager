package com.nas.server.entity;

import java.util.ArrayList;
import java.util.List;

public class Disk {

    private String diskName;
    private String diskSize;
    private String devNum;
    private String partitionTable;
    private String SENum;
    private List<Partition> partitions = new ArrayList<>();

    public String getDiskName() {
        return diskName;
    }

    public String getSENum() {
        return SENum;
    }

    public void setSENum(String SENum) {
        this.SENum = SENum;
    }

    public void setDiskName(String diskName) {
        this.diskName = diskName;
    }

    public String getDiskSize() {
        return diskSize;
    }

    public void setDiskSize(String diskSize) {
        this.diskSize = diskSize;
    }

    public String getDevNum() {
        return devNum;
    }

    public void setDevNum(String devNum) {
        this.devNum = devNum;
    }

    public String getPartitionTable() {
        return partitionTable;
    }

    public void setPartitionTable(String partitionTable) {
        this.partitionTable = partitionTable;
    }

    public List<Partition> getPartitions() {
        return partitions;
    }

    public void addPartition(Partition partition) {
        this.partitions.add(partition);
    }
}
