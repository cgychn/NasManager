package com.nas.server.util.dir_share;

import com.nas.server.util.FileUtil;
import com.nas.server.util.process.ProcessResult;
import com.nas.server.util.process.ProcessRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


public class SMBShareServer extends ShareServer {
    private final Logger logger = LoggerFactory.getLogger(SMBShareServer.class);

    @Override
    public Protocol getProtocolName() {
        return Protocol.SMB;
    }

    @Override
    public boolean checkProtocolEnabled() {
        String[] cmd = {"sh", "-c", "service smb status"};
        try {
            // 检查smb服务是否在线
            ProcessResult runRes = ProcessRunner.run(cmd);
            return (runRes.getOutPut().toString().contains("Active: active (running)"));
        } catch (Exception e) {
            logger.error("检查smb服务失败", e);
            return false;
        }
    }

    @Override
    public String getConfigFileTemplate() {
        File exampleFile = new File("/etc/samba/smb.conf.example");
        if (exampleFile.exists()) {
            return FileUtil.readFile(exampleFile);
        }
        return "";
    }

    @Override
    public String getConfigFileContent() {
        return FileUtil.readFile(new File(getServerConfigPath()));
    }

    @Override
    public String getServerConfigPath() {
        return "/etc/samba/smb.conf";
    }

    @Override
    public boolean saveConfig(String configContent) {
        File configFile = new File(getServerConfigPath());
        try {
            FileUtil.writeStringToFile(configFile, configContent);
            // 应用配置
            String[] reloadCmd = {"sh", "-c", "smbcontrol smbd reload-config"};
            ProcessResult run = ProcessRunner.run(reloadCmd);
            return run.getExitValue() == 0;
        } catch (Exception e) {
            logger.error("写入smb配置文件失败", e);
        }
        return false;
    }

    @Override
    public boolean createShareUser(String userName, String password) {
        String[] cmdCreateSystemUser = {"sh", "-c", "useradd " + userName + " && echo \"" + password + "\" | passwd --stdin \"" + userName + "\""};
        String[] cmdCreateSmbUser = {"sh", "-c", "expect /opt/nas_server/bin/addSmbUser.sh " + userName + " " + password};
        try {
            ProcessRunner.run(cmdCreateSystemUser);
            ProcessResult run = ProcessRunner.run(cmdCreateSmbUser);
            return run.getExitValue() == 0;
        } catch (Exception e) {
            logger.error("", e);
            return false;
        }
    }

    @Override
    public boolean startServer() {
        String[] cmd = {"sh", "-c", "service smb start"};
        try {
            ProcessResult runRes = ProcessRunner.run(cmd);
            return runRes.getExitValue() == 0;
        } catch (Exception e) {
            logger.error("启动smb服务失败", e);
        }
        return false;
    }

    @Override
    public boolean restartServer() {
        String[] cmd = {"sh", "-c", "service smb restart"};
        try {
            ProcessResult runRes = ProcessRunner.run(cmd);
            return runRes.getExitValue() == 0;
        } catch (Exception e) {
            logger.error("重新启动smb服务失败", e);
        }
        return false;
    }

    @Override
    public boolean stopServer() {
        String[] cmd = {"sh", "-c", "service smb stop"};
        try {
            ProcessResult runRes = ProcessRunner.run(cmd);
            return runRes.getExitValue() == 0;
        } catch (Exception e) {
            logger.error("关闭smb服务失败", e);
        }
        return false;
    }


}
