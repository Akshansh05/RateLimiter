package org.example.Blogic.Factories;

import org.example.Blogic.Strategy.*;
import org.example.Models.RateLimiter;
import org.example.Models.RateLimiterStrategy;

public class RateLimiterFactory {


    public static RateLimiter createRateLimiter(RateLimiterStrategy rateLimiterStrategy, int param1, int param2) throws IllegalAccessException {

        switch (rateLimiterStrategy) {
            case TOKEN_BUCKET:
                return new TokenBucketRateLimiter(param1, param2);
            case LEAKY_BUCKET:
                return new LeakyBucketRateLimiter(param1, param2);
            case FIXED_WINDOW_COUNTER:
                return new FixedWindowCounterRateLimiter(param1, param2);
            case SLIDING_WINDOW_COUNTER:
                return new SlidingWindowCounterRateLimiter(param1, param2);
            case SLIDING_WINDOW_LOG:
                return new SlidingWindowLogRateLimiter(param1, param2);

            default:
                throw new IllegalAccessException("Wrong Strategy");
        }
    }
}
