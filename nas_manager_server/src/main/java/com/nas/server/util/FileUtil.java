package com.nas.server.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFileAttributes;

public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    public static String readFile(File f) {
        if (!f.exists()) {
            return null;
        }
        BufferedReader br = null;
        FileReader reader;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            reader = new FileReader(f);
            br = new BufferedReader(reader);
            //按行读取
            char[] chs = new char[4096];
            int len;
            while ((len = br.read(chs)) > -1) {
                stringBuilder.append(chs, 0, len);
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public static void writeStringToFile(File file, String content) throws Exception {
        FileWriter fileWriter = null;
        try {
            if (!file.exists()){
                return;
            }
            fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.flush();
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        } finally {
            try {
                if (fileWriter != null)
                    fileWriter.close();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    public static PosixFileAttributes getFileAttributes (File file) {
        try {
            return Files.readAttributes(file.toPath(), PosixFileAttributes.class);
        } catch (Exception e) {
            logger.error("读取文件熟悉失败", e);
            return null;
        }
    }
}
