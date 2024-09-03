package com.nas.server.util.dir_share;

import com.nas.server.entity.ShareConfig;

public abstract class ShareServer {

    public abstract Protocol getProtocolName();
    public abstract boolean checkProtocolEnabled();
    public abstract String getConfigFileTemplate();
    public abstract String getConfigFileContent();
    public abstract String getServerConfigPath();
    public abstract boolean saveConfig(String configContent);
    public abstract boolean createShareUser(String userName, String password);
    public abstract boolean startServer();
    public abstract boolean restartServer();
    public abstract boolean stopServer();
    public ShareConfig toShareConfig() {
        ShareConfig shareConfig = new ShareConfig();
        shareConfig.setProtocol(getProtocolName());
        shareConfig.setConfigContent(getConfigFileContent());
        shareConfig.setConfigTemplate(getConfigFileTemplate());
        shareConfig.setServerEnabled(checkProtocolEnabled());
        return shareConfig;
    }

}
