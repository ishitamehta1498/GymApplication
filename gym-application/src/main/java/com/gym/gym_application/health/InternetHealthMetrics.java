package com.gym.gym_application.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.net.URLConnection;

@Component
public class InternetHealthMetrics implements HealthIndicator {

    private final String checkUrl;
    public InternetHealthMetrics(@Value("${health.internet.check-url}") String checkUrl) {
        this.checkUrl = checkUrl;
    }

    @Override
    public Health health() {
        return checkInternet() ? Health.up().withDetail("success code", "Active Internet Connection").build()
                : Health.down().withDetail("error code", "InActive Internet Connection").build();
    }

    public boolean checkInternet() {
        try {
            URL url = new URL(checkUrl);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(3000);
            connection.connect();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
