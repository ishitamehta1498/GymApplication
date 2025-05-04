package com.gym.gym_application.controller;

import com.gym.gym_application.dto.EmailDto;
import com.gym.gym_application.feign.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequestMapping("/email")
@RestController
public class EmailController {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EmailFeignClient emailFeignClient;
    private String emailUrl="http://localhost:1000/email";

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> sendMail(@RequestBody EmailDto emailDto) {
        log.info("EmailController: AddEmail");
        log.info(String.valueOf(emailDto));
        return restTemplate.postForEntity(emailUrl, emailDto, String.class);
    }

    @PostMapping("/feign")
    public ResponseEntity<Void> sendSimpleMailMessage(@RequestBody EmailDto emailDto){
        log.info("NotificationController : Send Email Method");
        return emailFeignClient.sendSimpleMailMessage(emailDto);
    }

}
