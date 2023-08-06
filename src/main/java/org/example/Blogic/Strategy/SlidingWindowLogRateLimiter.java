package org.example.Blogic.Strategy;

import org.example.Models.RateLimiter;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SlidingWindowLogRateLimiter implements RateLimiter {
    private final Object lock = new Object();
    private final int maxRequestLimitForaWindow;
    private final long windowDurationInMillis;
    Queue<Long> requestsTimes; //log container
    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);


    public SlidingWindowLogRateLimiter(int maxRequestLimitForAWindow, int windowDurationInSeconds) { //windowSize here is the rate for sliding the window
        this.maxRequestLimitForaWindow = maxRequestLimitForAWindow;
        this.windowDurationInMillis = 1000L * windowDurationInSeconds;
        this.requestsTimes = new LinkedList<>();

        scheduledExecutorService.scheduleAtFixedRate(this::slideWindowAtFixedRate, 0, windowDurationInSeconds, TimeUnit.SECONDS);
    }

    private void slideWindowAtFixedRate() {
        //slide and remove the old  log timestamp at fixed intervals
        long currentTimeInMillis = System.currentTimeMillis();
        while (!requestsTimes.isEmpty() && requestsTimes.peek() < currentTimeInMillis - windowDurationInMillis) {
            requestsTimes.poll();
        }
    }

    @Override
    public boolean allowRequest(Runnable request) {
        synchronized (lock) {
            if (requestsTimes.size() < maxRequestLimitForaWindow) {
                requestsTimes.add(System.currentTimeMillis()); //add the log timestamp to the log queue
                return true;
            }
            System.out.println("Request " + request.toString() + "is not allowed");
            return false;
        }
    }

    @Override
    public void acceptRequest(Runnable request) {
        if (allowRequest(request)) {
            scheduledExecutorService.execute(request);
        }

    }
}
