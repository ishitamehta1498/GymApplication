package com.gym.gym_application.health;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class CustomMetricService {
    private final Counter loginCounter;

    public CustomMetricService(MeterRegistry meterRegistry) {
        this.loginCounter = meterRegistry.counter("app.login.count");
    }

    public void trackLogin() {
        loginCounter.increment();
    }
}
