package org.example.Blogic.Strategy;

import org.example.Models.RateLimiter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SlidingWindowCounterRateLimiter implements RateLimiter {

    private final Object lock = new Object();
    private final int maxRequestLimitForaWindow;
    private final long windowDurationInMillis;
    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
    private int counter;
    private long[] timeStamps;
    private int currentIndex;
    private long currTimeInMillis;


    public SlidingWindowCounterRateLimiter(int maxRequestLimitForAWindow, int windowDurationInSecs) { //windowSize here is the rate for sliding the window
        this.maxRequestLimitForaWindow = maxRequestLimitForAWindow;
        this.windowDurationInMillis = 1000L * windowDurationInSecs;
        this.counter = 0;
        this.currentIndex = 0;
        this.currTimeInMillis = System.currentTimeMillis();
        this.timeStamps = new long[maxRequestLimitForAWindow];
        scheduledExecutorService.scheduleAtFixedRate(this::slideWindowAtFixedRate, 0, windowDurationInSecs, TimeUnit.SECONDS);
    }

    private void slideWindowAtFixedRate() {
        //slide the counter at fixed intervals
        currTimeInMillis = System.currentTimeMillis();

        // Remove timestamps that are outside the current window
        for (int i = 0; i < timeStamps.length; i++) {
            if (currTimeInMillis - timeStamps[i] >= windowDurationInMillis) {
                timeStamps[i] = 0;
            }
        }
        // Check if the request limit is exceeded
        int count = 0;
        for (long timeStamp : timeStamps) {
            if (timeStamp != 0) {
                count++;
            }
        }
        this.counter = count;
    }

    @Override
    public boolean allowRequest(Runnable request) {
        synchronized (lock) {
            if (counter < maxRequestLimitForaWindow) {
                timeStamps[currentIndex] = System.currentTimeMillis();
                currentIndex = (currentIndex + 1) % maxRequestLimitForaWindow;
                return true; // Allow the request
            } else {
                System.out.println("Request " + request.toString() + "is not allowed");
                return false; // Reject the request
            }
        }
    }

    @Override
    public void acceptRequest(Runnable request) {
        if (allowRequest(request)) {
            scheduledExecutorService.execute(request);
        }

    }
}
