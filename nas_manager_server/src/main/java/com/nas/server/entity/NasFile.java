package com.nas.server.entity;

import java.util.Date;

public class NasFile {

    private String fileName;

    private String absFilePath;

    private Date dataTime;

    private boolean isDir;

    private String owner;

    private String group;

    private boolean ownerRead;

    private boolean ownerWrite;

    private boolean ownerExecute;

    private boolean groupRead;

    private boolean groupWrite;

    private boolean groupExecute;

    private boolean otherRead;

    private boolean otherWrite;

    private boolean otherExecute;

    private long fileSize;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getAbsFilePath() {
        return absFilePath;
    }

    public void setAbsFilePath(String absFilePath) {
        this.absFilePath = absFilePath;
    }

    public Date getDataTime() {
        return dataTime;
    }

    public void setDataTime(Date dataTime) {
        this.dataTime = dataTime;
    }

    public boolean isDir() {
        return isDir;
    }

    public void setDir(boolean dir) {
        isDir = dir;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public boolean isOwnerRead() {
        return ownerRead;
    }

    public void setOwnerRead(boolean ownerRead) {
        this.ownerRead = ownerRead;
    }

    public boolean isOwnerWrite() {
        return ownerWrite;
    }

    public void setOwnerWrite(boolean ownerWrite) {
        this.ownerWrite = ownerWrite;
    }

    public boolean isOwnerExecute() {
        return ownerExecute;
    }

    public void setOwnerExecute(boolean ownerExecute) {
        this.ownerExecute = ownerExecute;
    }

    public boolean isGroupRead() {
        return groupRead;
    }

    public void setGroupRead(boolean groupRead) {
        this.groupRead = groupRead;
    }

    public boolean isGroupWrite() {
        return groupWrite;
    }

    public void setGroupWrite(boolean groupWrite) {
        this.groupWrite = groupWrite;
    }

    public boolean isGroupExecute() {
        return groupExecute;
    }

    public void setGroupExecute(boolean groupExecute) {
        this.groupExecute = groupExecute;
    }

    public boolean isOtherRead() {
        return otherRead;
    }

    public void setOtherRead(boolean otherRead) {
        this.otherRead = otherRead;
    }

    public boolean isOtherWrite() {
        return otherWrite;
    }

    public void setOtherWrite(boolean otherWrite) {
        this.otherWrite = otherWrite;
    }

    public boolean isOtherExecute() {
        return otherExecute;
    }

    public void setOtherExecute(boolean otherExecute) {
        this.otherExecute = otherExecute;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
}
