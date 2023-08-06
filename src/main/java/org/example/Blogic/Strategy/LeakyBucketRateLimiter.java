package org.example.Blogic.Strategy;

import org.example.Models.RateLimiter;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LeakyBucketRateLimiter implements RateLimiter {
    private final int capacity;
    private final Object lock = new Object();
    private final Queue<Runnable> proceessQueue;
    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
    private int tokensAvailable;


    public LeakyBucketRateLimiter(int capacity, int processRate) { //process Rate is the rate at which the queue is performing the job
        this.capacity = capacity;
        proceessQueue = new LinkedList<>();
        this.tokensAvailable = capacity;
        scheduledExecutorService.scheduleAtFixedRate(this::processQueueRequest, 0, processRate, TimeUnit.SECONDS);

    }

    private void processQueueRequest() {
        synchronized (lock) {
            if (!proceessQueue.isEmpty()) {
                Runnable task = proceessQueue.poll();
                scheduledExecutorService.execute(task);
                tokensAvailable = tokensAvailable + 1;
                tokensAvailable = Math.min(tokensAvailable, capacity);
            }
        }
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
            proceessQueue.add(request);
        }

    }
}
