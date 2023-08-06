package org.example;

import org.example.Blogic.Factories.RateLimiterFactory;
import org.example.Models.RateLimiter;
import org.example.Models.RateLimiterStrategy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RateLimiterApplication {
    public static void main(String[] args) throws IllegalAccessException, IOException, InterruptedException {

        System.out.println("Rate Limiter");

        while (true) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Enter Strategy");
            String strategy = bufferedReader.readLine();

            System.out.println("Enter Strategy Param1");
            int param1 = Integer.parseInt(bufferedReader.readLine());

            System.out.println("Enter Strategy Param2");
            int param2 = Integer.parseInt(bufferedReader.readLine());

            RateLimiter rateLimiter = RateLimiterFactory.createRateLimiter(RateLimiterStrategy.valueOf(strategy), param1, param2);
            worker(rateLimiter);
            Thread.sleep(5000);//wait for 5sec for  next input
        }
    }

    static void worker(RateLimiter rateLimiter) throws InterruptedException {
        for (int i = 0; i <= 100; i++) {
            int finalI = i;
            Runnable request = () -> System.out.println("Request " + finalI + " processed at " + System.currentTimeMillis());
            rateLimiter.acceptRequest(request);
            Thread.sleep(200);//trigger request evert 200ms
        }
    }
}

