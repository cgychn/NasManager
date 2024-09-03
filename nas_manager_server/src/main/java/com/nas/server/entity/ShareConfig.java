package com.nas.server.entity;

import com.nas.server.util.dir_share.Protocol;

public class ShareConfig {

    private String configContent;
    private String configTemplate;
    private Protocol protocol;
    private boolean serverEnabled;

    public String getConfigContent() {
        return configContent;
    }

    public void setConfigContent(String configContent) {
        this.configContent = configContent;
    }

    public String getConfigTemplate() {
        return configTemplate;
    }

    public void setConfigTemplate(String configTemplate) {
        this.configTemplate = configTemplate;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public boolean isServerEnabled() {
        return serverEnabled;
    }

    public void setServerEnabled(boolean serverEnabled) {
        this.serverEnabled = serverEnabled;
    }
}
