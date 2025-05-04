package com.gym.gym_application.feign;

import com.gym.gym_application.dto.EmailDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailFeignClientImpl implements EmailFeignClient{

    @Override
    public ResponseEntity<Void> sendSimpleMailMessage(EmailDto emailDto) {
        log.info("Email Service is down");
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
