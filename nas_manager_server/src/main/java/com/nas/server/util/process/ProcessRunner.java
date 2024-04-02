package com.nas.server.util.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class ProcessRunner {

    static Logger logger = LoggerFactory.getLogger(ProcessRunner.class);

    private static final List<Process> runningProcess = new LinkedList<>();

    static {
        synchronized (runningProcess) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("回调执行，结束" + runningProcess.size() + "个子进程");
                runningProcess.forEach(Process::destroyForcibly);
            }));
        }
    }

    /**
     * 命令行程序输出触发器
     */
    public interface ProcessOutputTrigger {
        // 如果需要根据命令行程序输出实现功能，需要实现该方法
        void toDo(String line);
    }

    /**
     * 进程创建时回调
     */
    public interface ProcessCreatedTrigger {
        // 回调进程对象
        void toDo (Process process);
    }


    /**
     * 命令行运行
     * @param cmd
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static ProcessResult run (String[] cmd) throws IOException, InterruptedException {
        return run(cmd, null, null,"UTF-8", null, null, true);
    }


    /**
     * 命令行运行
     * @param cmd
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static ProcessResult run (String cmd) throws IOException, InterruptedException {
        return run(cmd, null, null,"UTF-8", null, null);
    }

    /**
     * 运行命令，并指定输出编码格式
     * @param cmd
     * @param outputEncode
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static ProcessResult run (String cmd, String outputEncode) throws IOException, InterruptedException {
        return run(cmd, null, null, outputEncode, null, null);
    }

    /**
     * 运行命令，并指定输出编码格式
     * @param cmd
     * @param outputEncode
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static ProcessResult run (String[] cmd, String outputEncode) throws IOException, InterruptedException {
        return run(cmd, null, null, outputEncode, null, null, true);
    }

    /**
     * 在wd（指定的工作目录）中运行cmd
     * @param cmd
     * @param envp
     * @param wd
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static ProcessResult run (String cmd, String[] envp, File wd) throws IOException, InterruptedException {
        return run(cmd, envp, wd,"UTF-8", null, null);
    }

    /**
     * 在wd（指定的工作目录）中运行cmd
     * @param cmd
     * @param envp
     * @param wd
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static ProcessResult run (String[] cmd, String[] envp, File wd) throws IOException, InterruptedException {
        return run(cmd, envp, wd,"UTF-8", null, null, true);
    }


    // ============================================带输出触发器==============================================================
    /**
     * 命令行运行
     * @param cmd
     * @param trigger
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static ProcessResult run (String cmd, ProcessOutputTrigger trigger) throws IOException, InterruptedException {
        return run(cmd, null, null,"UTF-8", trigger, null);
    }

    /**
     * 命令行运行
     * @param cmd
     * @param trigger
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static ProcessResult run (String[] cmd, ProcessOutputTrigger trigger) throws IOException, InterruptedException {
        return run(cmd, null, null,"UTF-8", trigger, null, true);
    }

    /**
     * 运行命令，并指定输出编码格式
     * @param cmd
     * @param outputEncode
     * @param trigger
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static ProcessResult run (String cmd,
                                     String outputEncode,
                                     ProcessOutputTrigger trigger) throws IOException, InterruptedException {
        return run(cmd, null, null, outputEncode, trigger, null);
    }

    /**
     * 运行命令，并指定输出编码格式
     * @param cmd
     * @param outputEncode
     * @param trigger
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static ProcessResult run (String[] cmd,
                                     String outputEncode,
                                     ProcessOutputTrigger trigger) throws IOException, InterruptedException {
        return run(cmd, null, null, outputEncode, trigger, null, true);
    }

    /**
     * 在wd（指定的工作目录）中运行cmd
     * @param cmd
     * @param envp
     * @param wd
     * @param trigger
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static ProcessResult run (String cmd,
                                     String[] envp,
                                     File wd,
                                     ProcessOutputTrigger trigger) throws IOException, InterruptedException {
        return run(cmd, envp, wd,"UTF-8", trigger, null, true);
    }

    /**
     * 在wd（指定的工作目录）中运行cmd
     * @param cmd
     * @param envp
     * @param wd
     * @param trigger
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static ProcessResult run (String[] cmd,
                                     String[] envp,
                                     File wd,
                                     ProcessOutputTrigger trigger) throws IOException, InterruptedException {
        return run(cmd, envp, wd,"UTF-8", trigger, null, true);
    }

    /**
     * @param cmd
     * @param envp
     * @param wd
     * @param outputEncode
     * @param trigger
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static ProcessResult run (String cmd,
                                     String[] envp,
                                     File wd,
                                     String outputEncode,
                                     ProcessOutputTrigger trigger,
                                     ProcessCreatedTrigger createdTrigger) throws IOException, InterruptedException {
        return run(cmd, envp, wd, outputEncode, trigger, createdTrigger, true);
    }

    /**
     * @param cmd
     * @param envp
     * @param wd
     * @param outputEncode
     * @param trigger
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static ProcessResult run (String cmd,
                                     String[] envp,
                                     File wd,
                                     String outputEncode,
                                     ProcessOutputTrigger trigger,
                                     ProcessCreatedTrigger createdTrigger, boolean showLog) throws IOException, InterruptedException {

        StringTokenizer st = new StringTokenizer(cmd);
        String[] cmdarray = new String[st.countTokens()];
        for (int i = 0; st.hasMoreTokens(); i++)
            cmdarray[i] = st.nextToken();
        return run(cmdarray, envp, wd, outputEncode, trigger, createdTrigger, showLog);
    }


    /**
     * @param cmd
     * @param envp
     * @param wd
     * @param outputEncode
     * @param trigger
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static ProcessResult run (String[] cmd,
                                     String[] envp,
                                     File wd,
                                     String outputEncode,
                                     ProcessOutputTrigger trigger,
                                     ProcessCreatedTrigger createdTrigger, boolean showLog) throws IOException, InterruptedException {
        if (showLog) {
            logger.info("\n待执行命令：" + String.join(" ", cmd));
        }
        ProcessBuilder pb = new ProcessBuilder(cmd).directory(wd);
        Map<String, String> envMap = pb.environment();
        // 加载环境变量
        genEnv(envMap, envp);
        Process process = pb.start();
        ProcessResult processResult;
        try {
            // 加入运行队列
            synchronized (runningProcess) {
                runningProcess.add(process);
            }
            if (createdTrigger != null) {
                createdTrigger.toDo(process);
            }
            processResult = getResult(process, outputEncode, trigger, showLog);
        } finally {
            // 移出运行队列
            synchronized (runningProcess) {
                runningProcess.remove(process);
            }
        }
        return processResult;
    }

    /**
     * 生成环境变量
     * @param env
     * @return
     */
    private static void genEnv (Map<String, String> environment, String[] env) {
        if (env != null) {
            for (String envstring : env) {
                if (envstring.indexOf((int) '\u0000') != -1)
                    envstring = envstring.replaceFirst("\u0000.*", "");

                int eqlsign = envstring.indexOf('=', 1);
                if (eqlsign != -1)
                    environment.put(envstring.substring(0,eqlsign),
                            envstring.substring(eqlsign+1));
            }
        }
    }

    /**
     * 运行cmd（对runtime.exec的封装）
     * @param process
     * @param outputEncode
     * @param trigger
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    private static ProcessResult getResult (Process process,
                                            String outputEncode,
                                            ProcessOutputTrigger trigger, boolean showLog) throws IOException, InterruptedException {
        ProcessResult processResult = new ProcessResult();
        StringBuilder outPut = new StringBuilder();
        StringBuilder errOutPut = new StringBuilder();
        BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(process.getInputStream(), outputEncode));
        BufferedReader errInputStreamReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), outputEncode));
        String inputStr;
        while ((inputStr = inputStreamReader.readLine()) != null) {
            if (showLog) {
                logger.info(inputStr);
            }
            outPut.append(inputStr).append("\n");
            if (trigger != null) {
                trigger.toDo(inputStr);
            }
        }
        while ((inputStr = errInputStreamReader.readLine()) != null) {
            if (showLog) {
                logger.error(inputStr);
            }
            errOutPut.append(inputStr).append("\n");
            if (trigger != null) {
                trigger.toDo(inputStr);
            }
        }
        inputStreamReader.close();
        int exitVal = process.waitFor();
        if (showLog) {
            logger.info("\n程序返回值：" + exitVal);
        }
        processResult.setExitValue(exitVal);
        processResult.setOutPut(outPut);
        processResult.setErrOutPut(errOutPut);
        return processResult;
    }

    /**
     * 杀进程
     * @param process
     */
    public static void killProcess (Process process) {
        killProcess(process, false);
    }

    /**
     * 杀进程
     * @param process
     * @param force
     */
    public static void killProcess (Process process, boolean force) {
        if (force) {
            process.destroyForcibly();
        } else {
            process.destroy();
        }
    }

}
