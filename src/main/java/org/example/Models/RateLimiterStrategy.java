package org.example.Models;

public enum RateLimiterStrategy {
    TOKEN_BUCKET,
    LEAKY_BUCKET,
    FIXED_WINDOW_COUNTER,
    SLIDING_WINDOW_COUNTER,
    SLIDING_WINDOW_LOG;
}
