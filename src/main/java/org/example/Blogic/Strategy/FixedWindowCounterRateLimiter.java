package org.example.Blogic.Strategy;

import org.example.Models.RateLimiter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FixedWindowCounterRateLimiter implements RateLimiter {
    private final Object lock = new Object();
    private final int maxRequestLimitForaWindow;
    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
    private int counter;

    public FixedWindowCounterRateLimiter(int maxRequestLimitForaWindow, int windowDurationInSecs) { //indowSize here is the rate for fixed window
        this.maxRequestLimitForaWindow = maxRequestLimitForaWindow;
        this.counter = 0;
        executorService.scheduleAtFixedRate(this::resetCounterAtFixedIntervals, 0, windowDurationInSecs, TimeUnit.SECONDS);
    }

    private void resetCounterAtFixedIntervals() {
        //reset the counter at fixed Intervals
        counter = 0;
    }

    @Override
    public boolean allowRequest(Runnable request) {
        synchronized (lock) {
            if (counter < maxRequestLimitForaWindow) { //if current window counter is less than limit of that window allow request
                counter = counter + 1;
                return true;
            }
            System.out.println("Request " + request.toString() + "is not allowed");
            return false;
        }

    }

    @Override
    public void acceptRequest(Runnable request) {
        if (allowRequest(request)) {
            executorService.execute(request);
        }
    }
}
