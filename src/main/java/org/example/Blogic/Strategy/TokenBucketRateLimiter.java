package org.example.Blogic.Strategy;

import org.example.Models.RateLimiter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TokenBucketRateLimiter implements RateLimiter {

    private final int capacity;
    private final int tokensAddedPerSecond;
    private final Object lock = new Object();
    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
    private int tokensAvailable;


    public TokenBucketRateLimiter(int capacity, int tokensAddedPerSecond) {//tokensAddedPerSecond or refillRate
        this.capacity = capacity;
        this.tokensAddedPerSecond = tokensAddedPerSecond;
        this.tokensAvailable = capacity;
        scheduledExecutorService.scheduleAtFixedRate(this::refillTokens, 0, 1, TimeUnit.SECONDS);
    }

    private void refillTokens() {
        tokensAvailable += tokensAddedPerSecond;
        tokensAvailable = Math.min(tokensAvailable, capacity);
    }

    @Override
    public boolean allowRequest(Runnable request) {
        synchronized (lock) {
            if (tokensAvailable > 0) {
                tokensAvailable = tokensAvailable - 1;
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
