package com.nas.server.util;

import com.nas.server.entity.Disk;
import com.nas.server.entity.MountListItem;
import com.nas.server.entity.MountPointUsage;
import com.nas.server.entity.Partition;
import com.nas.server.enumrate.DiskLabel;
import com.nas.server.enumrate.PartitionFormat;
import com.nas.server.util.process.ProcessResult;
import com.nas.server.util.process.ProcessRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class DriveAPI {

    private static final Logger logger = LoggerFactory.getLogger(DriveAPI.class);

    public interface DFTimeoutCallback {
        void callback();
    }

    public static List<MountListItem> getMountList(boolean showNetMount, String partitionNum, String mountPoint) throws Exception {
        return getMountList(showNetMount, partitionNum, mountPoint, null);
    }

    /**
     * 获取所有挂载列表
     * @param showNetMount 是否显示网络挂载路径
     * @param partitionNum 根据分区名、
     * @param mountPoint 挂载点
     * @param dfTimeoutCallback 超时回调
     * @return
     * @throws Exception
     */
    public static List<MountListItem> getMountList(boolean showNetMount, String partitionNum, String mountPoint, DFTimeoutCallback dfTimeoutCallback) throws Exception{
        if (partitionNum == null) {
            partitionNum = "";
        }
        // get head indexes
        String[] cmd = {"sh", "-c", "df -hl | awk 'NR == 1'"};
        ProcessResult result = ProcessRunner.run(cmd, null, null,"UTF-8", null, null, false);
        String resString = result.getOutPut().toString();
        String[] lines = resString.split("\n");
        int sizeEndIndex = 0, usedEndIndex = 0, availEndIndex = 0, usedPercentageEndIndex = 0, mountOnStartIndex = 0;
        for (String line : lines) {
            if (line.startsWith("Filesystem ")) {
                sizeEndIndex = line.indexOf("Size") + 4;
                usedEndIndex = line.indexOf("Used") + 4;
                availEndIndex = line.indexOf("Avail") + 5;
                usedPercentageEndIndex = line.indexOf("Use%") + 4;
                mountOnStartIndex = line.indexOf("Mounted on");
            }
        }
        String dfPath = StringUtil.isEmpty(mountPoint) ? partitionNum : mountPoint;
        List<MountListItem> res = new LinkedList<>();
        AtomicReference<ProcessResult> resultDf = new AtomicReference<>();
        AtomicReference<Process> processDf = new AtomicReference<>();
        Thread t = new Thread(() -> {
            try {
                String[] cmdDf = new String[]{"sh", "-c", "df -h" + (showNetMount ? "" : "l") + " " + dfPath + " | awk 'NR > 1'"};
                // match contents by head indexes
                resultDf.set(ProcessRunner.run(cmdDf, null, null, "UTF-8", null, processDf::set, false));
            } catch (Exception e) {
                logger.error("执行df命令失败", e);
            }
        });
        t.start();
        t.join(10000);
        if (resultDf.get() == null && dfTimeoutCallback != null) {
            try {
                ProcessRunner.killProcess(processDf.get(), true);
            } catch (Exception e) {
                logger.error("杀死进程失败", e);
            }
            dfTimeoutCallback.callback();
            return res;
        }
        resString = result.getOutPut().toString();
        lines = resString.split("\n");
        for (String line : lines) {
            if (
                    StringUtil.isEmpty(dfPath) ?
                            line.trim().startsWith("/") :
                            (line.trim().startsWith(partitionNum) && line.trim().endsWith(mountPoint))
            ) {
                // get size first
                int spaceInFrontOfSizeEnd = StringUtil.getFirstSpaceIndexInFrontOfGivenIndexInString(line, sizeEndIndex - 1);
                MountListItem mountListItem = new MountListItem();
                mountListItem.setSize(StringUtil.substring(line, spaceInFrontOfSizeEnd, sizeEndIndex).trim());
                mountListItem.setFileSystem(StringUtil.substring(line, 0, spaceInFrontOfSizeEnd).trim());
                mountListItem.setUsed(StringUtil.substring(line, sizeEndIndex, usedEndIndex).trim());
                mountListItem.setAvail(StringUtil.substring(line, usedEndIndex, availEndIndex).trim());
                mountListItem.setUsePercent(
                        Float.parseFloat(
                                (StringUtil.substring(line, availEndIndex, usedPercentageEndIndex).trim()).replace("%", "")
                        ) / 100
                );
                mountListItem.setMountOn(StringUtil.substring(line, mountOnStartIndex, line.length()).trim());
                res.add(mountListItem);
            }
        }
        return res;
    }

    /**
     * 获取设备序列号
     * @param devNum 设备号
     * @return
     */
    public static String getDiskSENum (String devNum) throws Exception {
        String[] cmd = {"sh", "-c", "smartctl -i " + devNum + " | grep 'Serial Number:'"};
        ProcessResult result = ProcessRunner.run(cmd, null, null,"UTF-8", null, null, false);
        String resString = result.getOutPut().toString();
        String[] lines = resString.split("\n");
        for (String line : lines) {
            if (StringUtil.isEmpty(line, true)) {
                continue;
            }
            line = line.trim().replaceAll(" +", " ");
            return line.split(":")[1].trim();
        }
        return "";
    }

    /**
     * 获取分区UUID
     * @param partitionNum 分区号
     * @return
     */
    public static String getPartitionUUID (String partitionNum) throws Exception {
        String[] getUUIDCmd = {"sh", "-c", "ls -l /dev/disk/by-uuid"};
        ProcessResult result = ProcessRunner.run(getUUIDCmd, null, null,"UTF-8", null, null, false);
        String[] lines = result.getOutPut().toString().split("\n");
        for (String line : lines) {
            if (line.endsWith("../.." + partitionNum.replace("/dev", ""))) {
                String paramLine = line.split("->")[0];
                String[] params = paramLine.split(" ");
                return params[params.length - 1];
            }
        }
        return "";
    }

    /**
     * 获取磁盘列表
     * @return
     * @throws Exception
     */
    public static List<Disk> getDiskList () throws Exception {
        String[] cmd = {"sh", "-c", "parted -l"};
        ProcessResult result = ProcessRunner.run(cmd, null, null,"UTF-8", null, null, false);
        String resString = result.getOutPut().toString();
        String[] diskParts = resString.split("\n\n\n");
        List<Disk> disks = new ArrayList<>();
        for (String diskPart : diskParts) {
            Disk disk = new Disk();
            String[] lines = diskPart.split("\n");
            String devNum = "";
            String table = "";
            String model = "";
            String size = "";
            for (String line : lines) {
                if (line.startsWith("Disk /")) {
                    // 指定磁盘的信息
                    devNum = line.trim().split(":")[0].split(" ")[1];
                    size = line.trim().split(":")[1].trim();
                }
                if (line.startsWith("Partition Table:")) {
                    table = line.trim().split(":")[1].trim();
                }
                if (line.startsWith("Model:")) {
                    model = line.trim().split(":")[1].trim();
                }
            }
            disk.setDevNum(devNum);
            disk.setPartitionTable(table);
            disk.setDiskName(model);
            disk.setDiskSize(size);
            disk.setSENum(getDiskSENum(devNum));
            int i = 0;
            int startStartIndex = 0;
            int endStartIndex = 0;
            int sizeStartIndex = 0;
            int fileSystemStartIndex = 0;
            int nameStartIndex = 0;
            int flagsStartIndex = 0;
            for (; i < lines.length; i ++) {
                if (lines[i].trim().startsWith("Number ")) {
                    String line = lines[i];
                    startStartIndex = line.indexOf("Start");
                    endStartIndex = line.indexOf("End");
                    sizeStartIndex = line.indexOf("Size");
                    fileSystemStartIndex = line.indexOf("File system");
                    nameStartIndex = line.indexOf("Name");
                    flagsStartIndex = line.indexOf("Flags");
                    break;
                }
            }
            i ++;
            for (; i < lines.length; i ++) {
                Partition partition = new Partition();
                if (StringUtil.isEmpty(lines[i].trim())) {
                    continue;
                }
                String partitionNum = lines[i].trim().split(" ")[0];
                partition.setPartitionNumber(Integer.parseInt(partitionNum));
                if (devNum.startsWith("/dev/nvme")) {
                    partitionNum = devNum + "p" + partitionNum;
                } else {
                    partitionNum = devNum + partitionNum;
                }
                partition.setPartitionNum(partitionNum);
                partition.setStart(StringUtil.substring(lines[i], startStartIndex, endStartIndex).trim());
                partition.setEnd(StringUtil.substring(lines[i], endStartIndex, sizeStartIndex).trim());
                partition.setSize(StringUtil.substring(lines[i], sizeStartIndex, fileSystemStartIndex).trim());
                partition.setFileSystem(StringUtil.substring(lines[i], fileSystemStartIndex, nameStartIndex).trim());
                partition.setName(StringUtil.substring(lines[i], nameStartIndex, flagsStartIndex).trim());
                partition.setFlags(StringUtil.substring(lines[i], flagsStartIndex, lines[i].length()).trim());
                partition.setUuid(getPartitionUUID(partitionNum));
                List<MountListItem> mountList = getMountList(false, partitionNum, "");
                if (mountList != null && mountList.size() > 0) {
                    partition.setMountOn(mountList.get(0).getMountOn());
                }
                disk.addPartition(partition);
            }
            disks.add(disk);
        }
        return disks;
    }

    /**
     * 初始化磁盘分区表格式
     * @param devNum
     * @param labelName
     * @return
     * @throws Exception
     */
    public static boolean makeLabel (String devNum, DiskLabel labelName) throws Exception {
        String[] cmd = {"sh", "-c", "parted -s " + devNum + " mklabel " + labelName.name()};
        ProcessResult result = ProcessRunner.run(cmd);
        int returnVal = result.getExitValue();
        if (returnVal != 0) {
            throw new Exception(result.getErrOutPut().toString());
        }
        return true;
    }

    /**
     * 创建分区
     * @param devNum 设备名
     * @param start 开始位置
     * @param end 结束位置
     * @return
     * @throws Exception
     */
    public static boolean createNewPartition (String devNum, String start, String end) throws Exception {
        String[] cmdCreatNewPartition = {"sh", "-c", "parted -s " + devNum + " mkpart primary " + start + " " + end};
        ProcessResult result = ProcessRunner.run(cmdCreatNewPartition);
        int returnVal = result.getExitValue();
        if (returnVal != 0) {
            throw new Exception(result.getErrOutPut().toString());
        }
        return true;
    }

    /**
     * 删除分区
     * @param devNum 设备名称
     * @param partitionNumber 分区号（integer index）
     * @return
     */
    public static boolean removePartition (String devNum, int partitionNumber) throws Exception {
        // 删除分区
        String[] cmd = {"sh", "-c", "parted -s " + devNum + " rm " + partitionNumber};
        ProcessResult result = ProcessRunner.run(cmd);
        int returnVal = result.getExitValue();
        if (returnVal != 0) {
            throw new Exception(result.getErrOutPut().toString());
        }
        return true;
    }

    /**
     * 格式化分区
     * @param partitionNum 分区名称
     * @param partitionFormat 格式
     * @return
     */
    public static boolean formatPartition (String partitionNum, PartitionFormat partitionFormat) throws Exception {
        String[] cmd = {"sh", "-c", "mkfs -t " + partitionFormat.name() + " -f " + partitionNum};
        ProcessResult result = ProcessRunner.run(cmd);
        int returnVal = result.getExitValue();
        if (returnVal != 0) {
            throw new Exception(result.getErrOutPut().toString());
        }
        return true;
    }

    /**
     * 挂载分区
     * @param partitionNum
     * @param fsType
     * @param extraOptions
     * @return
     */
    public static boolean mountPartition (String partitionNum, String mountPoint, String fsType, String extraOptions) throws Exception {
        String fsTypeOption = "";
        String options = "";
        if (!StringUtil.isEmpty(fsType, true)) {
            fsTypeOption = "-t " + fsType;
        }
        if (!StringUtil.isEmpty(extraOptions, true)) {
            options = "-o" + extraOptions;
        }
        String[] cmd = {"sh", "-c", "mount " + fsTypeOption + " '" + partitionNum + "' '" + mountPoint + "' " + options};
        ProcessResult result = ProcessRunner.run(cmd);
        int returnVal = result.getExitValue();
        if (returnVal != 0) {
            throw new Exception(result.getErrOutPut().toString());
        }
        return true;
    }


    /**
     * 卸载分区
     * @param partitionNum
     * @param latter
     * @return
     */
    public static boolean umountPartition (String partitionNum, boolean latter) throws Exception {
        return umount(partitionNum, latter);
    }

    /**
     * 卸载挂载点
     * @param mountPoint
     * @param latter
     * @return
     */
    public static boolean umountMountPoint (String mountPoint, boolean latter) throws Exception {
        return umount(mountPoint, latter);
    }

    /**
     * 卸载
     * @param mount
     * @param latter
     * @return
     * @throws Exception
     */
    private static boolean umount (String mount, boolean latter) throws Exception {
        String[] cmd = {"sh", "-c", "umount " + (latter ? "-l" : "") + "'" + mount + "'"};
        ProcessResult result = ProcessRunner.run(cmd);
        int returnVal = result.getExitValue();
        if (returnVal != 0) {
            throw new Exception(result.getErrOutPut().toString());
        }
        return true;
    }

    /**
     * 获取正在使用挂载点的
     * @param mountPoint
     * @return
     */
    public static List<MountPointUsage> getMountPointUsage (String mountPoint) throws Exception {
        String[] cmd = {"sh", "-c", "lsof '" + mountPoint + "' | awk '{print $2}' | awk 'NR > 1'"};
        List<MountPointUsage> res = new ArrayList<>();
        ProcessResult result = ProcessRunner.run(cmd);
        String[] lines = result.getOutPut().toString().split("\n");
        Set<String> pids = new HashSet<>();
        for (String line : lines) {
            if (!StringUtil.isEmpty(line, true)) {
                pids.add(line.trim());
            }
        }
        for (String pid : pids) {
            String[] cmdOfPs = {"sh", "-c", "ps -ef | grep '" + pid + "' | grep -v 'grep'"};
            ProcessResult resultOfPs = ProcessRunner.run(cmdOfPs);
            String[] linesOfPs = resultOfPs.getOutPut().toString().split("\n");
            for (String lineOfPs : linesOfPs) {
                if (!StringUtil.isEmpty(lineOfPs, true)) {
                    String[] parts = lineOfPs.replaceAll(" +", " ").split(" ");
                    if (parts[1].trim().equals(pid)) {
                        MountPointUsage mountPointUsage = new MountPointUsage();
                        mountPointUsage.setPid(pid);
                        mountPointUsage.setUser(parts[0]);
                        mountPointUsage.setRunningTime(parts[6]);
                        mountPointUsage.setCmd(StringUtil.substring(lineOfPs, lineOfPs.indexOf(parts[6]) + 8, lineOfPs.length()));
                        res.add(mountPointUsage);
                    }
                }
            }
        }
        return res;
    }


}
