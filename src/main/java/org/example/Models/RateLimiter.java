package org.example.Models;

public interface RateLimiter {
    boolean allowRequest(Runnable request);

    void acceptRequest(Runnable request);
}
