package com.nas.server.util.dir_share;

public interface ShareServer {

    Protocol getProtocolName();
    boolean checkProtocolEnabled();
    String getConfigFileTemplate();
    String getConfigFileContent();
    String getServerConfigPath();
    boolean saveConfig(String configContent);
    boolean createShareUser(String userName, String password);
    boolean startServer();
    boolean restartServer();
    boolean stopServer();
}
