package com.nas.server.config;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ScheduleExecutorPool {

    public static ScheduledThreadPoolExecutor getPool() {
        // DiskMonitor x1
        // NetMountMonitor x1
        return new ScheduledThreadPoolExecutor(2);
    }

}
