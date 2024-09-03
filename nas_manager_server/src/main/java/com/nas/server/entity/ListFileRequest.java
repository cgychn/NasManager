package com.nas.server.entity;

public class ListFileRequest {

    private String parentFileAbsPath;

    private String fileNameFilter;

    public String getParentFileAbsPath() {
        return parentFileAbsPath;
    }

    public void setParentFileAbsPath(String parentFileAbsPath) {
        this.parentFileAbsPath = parentFileAbsPath;
    }

    public String getFileNameFilter() {
        return fileNameFilter;
    }

    public void setFileNameFilter(String fileNameFilter) {
        this.fileNameFilter = fileNameFilter;
    }
}
