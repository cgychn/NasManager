package com.nas.server.entity;

import com.nas.server.util.dir_share.Protocol;

public class DirShareRequest {

    private String cfgContent;
    private Protocol protocol;

    public String getCfgContent() {
        return cfgContent;
    }

    public void setCfgContent(String cfgContent) {
        this.cfgContent = cfgContent;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }
}
