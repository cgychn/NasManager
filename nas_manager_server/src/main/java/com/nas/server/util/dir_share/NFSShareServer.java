package com.nas.server.util.dir_share;

import com.nas.server.util.FileUtil;
import com.nas.server.util.process.ProcessResult;
import com.nas.server.util.process.ProcessRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class NFSShareServer implements ShareServer {
    private static final Logger logger = LoggerFactory.getLogger(NFSShareServer.class);

    @Override
    public Protocol getProtocolName() {
        return Protocol.NFS;
    }

    @Override
    public boolean checkProtocolEnabled() {
        String[] cmdStatus = {"sh", "-c", "service nfs-server status"};
        try {
            ProcessResult res = ProcessRunner.run(cmdStatus);
            return res.getOutPut().toString().contains("Active: active");
        } catch (Exception e) {
            logger.error("检查nfs服务失败", e);
            return false;
        }
    }

    @Override
    public String getConfigFileTemplate() {
        return "";
    }

    @Override
    public String getConfigFileContent() {
        File configFile = new File(getServerConfigPath());
        return FileUtil.readFile(configFile);
    }

    @Override
    public String getServerConfigPath() {
        return "/etc/exports";
    }

    @Override
    public boolean saveConfig(String configContent) {
        File configFile = new File(getServerConfigPath());
        try {
            FileUtil.writeStringToFile(configFile, configContent);
            String[] reloadNfsCmd = {"sh", "-c", "exportfs"};
            ProcessResult res = ProcessRunner.run(reloadNfsCmd);
            return res.getExitValue() == 0;
        } catch (Exception e) {
            logger.error("写入nfs配置文件失败", e);
            return false;
        }
    }

    @Override
    public boolean createShareUser(String userName, String password) {
        String[] cmdCreateSystemUser = {"sh", "-c", "useradd " + userName + " && echo \"" + password + "\" | passwd --stdin \"" + userName + "\""};
        String[] checkUser = {"id", userName};
        try {
            ProcessRunner.run(cmdCreateSystemUser);
            ProcessResult run = ProcessRunner.run(checkUser);
            return run.getExitValue() == 0;
        } catch (Exception e) {
            logger.error("", e);
            return false;
        }
    }

    @Override
    public boolean startServer() {
        String[] cmd = {"sh", "-c", "service nfs-server start"};
        try {
            ProcessResult runRes = ProcessRunner.run(cmd);
            return runRes.getExitValue() == 0;
        } catch (Exception e) {
            logger.error("启动nfs服务失败", e);
        }
        return false;
    }

    @Override
    public boolean restartServer() {
        String[] cmd = {"sh", "-c", "service nfs-server restart"};
        try {
            ProcessResult runRes = ProcessRunner.run(cmd);
            return runRes.getExitValue() == 0;
        } catch (Exception e) {
            logger.error("重新启动nfs服务失败", e);
        }
        return false;
    }

    @Override
    public boolean stopServer() {
        String[] cmd = {"sh", "-c", "service nfs-server stop"};
        try {
            ProcessResult runRes = ProcessRunner.run(cmd);
            return runRes.getExitValue() == 0;
        } catch (Exception e) {
            logger.error("关闭nfs服务失败", e);
        }
        return false;
    }


}
