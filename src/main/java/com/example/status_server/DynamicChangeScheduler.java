package com.example.status_server;

import com.example.status_server.controller.MetricDataController;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DynamicChangeScheduler {

    private ThreadPoolTaskScheduler scheduler;
    private String cron = "0 0/5 * * * ?";

    @Autowired
    private MetricDataController metricDataController;

    public void startScheduler(){
        scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();
        scheduler.schedule(getRunnable(), getTrigger());
    }

    public void stopScheduler(){
        scheduler.shutdown();
    }

    private Runnable getRunnable(){
        return () -> {
            metricDataController.getMetricData();
        };
    }

    private Trigger getTrigger(){
        return new CronTrigger(cron);
    }

    @PostConstruct
    public void init(){
        startScheduler();
    }

    @PreDestroy
    public void destory(){
        stopScheduler();
    }
}
