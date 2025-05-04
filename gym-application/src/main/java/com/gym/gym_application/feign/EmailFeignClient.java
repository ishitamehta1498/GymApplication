package com.gym.gym_application.feign;

import com.gym.gym_application.dto.*;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.openfeign.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

//@FeignClient(url="http://localhost:1000/email", name="email")
@FeignClient(name="EmailNotificationService", fallback = EmailFeignClientImpl.class)
public interface EmailFeignClient {
    //@PostMapping
    @PostMapping("/email")
    public ResponseEntity<Void> sendSimpleMailMessage(@RequestBody EmailDto emailDto);
}
