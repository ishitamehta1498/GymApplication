package com.gym.gym_application.health;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CustomInfoContributor implements InfoContributor {

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("app", Map.of(
                "name", "Gym Management System",
                "version", "1.0.0",
                "description", "A Spring Boot app for gym trainers and trainees"
        ));
    }
}
