package com.nas.server.entity;

public class LsscsiListItem {

    private String scsiNum;
    private String type;
    private String protocol;
    private String devNum;

    public String getScsiNum() {
        return scsiNum;
    }

    public void setScsiNum(String scsiNum) {
        this.scsiNum = scsiNum;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getDevNum() {
        return devNum;
    }

    public void setDevNum(String devNum) {
        this.devNum = devNum;
    }
}
