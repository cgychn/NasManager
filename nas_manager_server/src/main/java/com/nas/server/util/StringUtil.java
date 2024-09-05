package com.nas.server.util;

import org.apache.commons.codec.digest.DigestUtils;

public class StringUtil {

    public static String substring(String str, int startIndex, int endIndex) {
        if (str.length() < endIndex || startIndex >= endIndex || startIndex < 0) {
            return "";
        }
        return str.substring(startIndex, endIndex);
    }

    public static boolean isEmpty(String string) {
        return isEmpty(string, false);
    }

    public static boolean isEmpty(String string, boolean trim) {
        if (string == null) {
            return true;
        }
        if (trim) {
            string = string.trim();
        }
        return string.isEmpty();
    }

    public static int getFirstSpaceIndexInFrontOfGivenIndexInString (String string, int givenIndex) {
        if (isEmpty(string)) {
            return -1;
        } else if (givenIndex >= string.length() || givenIndex <= 0) {
            return -1;
        } else {
            for (int i = givenIndex; i >= 0; i --) {
                if (string.charAt(i) == ' ') {
                    return i;
                }
            }
        }
        return -1;
    }

    public static String getStringMD5 (String str) {
        return DigestUtils.md5Hex(str);
    }

    public static void main (String[] args) {
        String s = "/dev/nvme0n1p4                                 433G  305G  128G  71% /";
        System.out.println(getFirstSpaceIndexInFrontOfGivenIndexInString(s, 50));
    }

}
