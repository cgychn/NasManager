package com.nas.server.config;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ScheduleExecutorPool {

    public static ScheduledThreadPoolExecutor getPool() {
        return new ScheduledThreadPoolExecutor(2);
    }

}
