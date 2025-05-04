package com.email.notification_service.controller;

import com.email.notification_service.dto.EmailDto;
import com.email.notification_service.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/email")
@RestController
public class NotificationController {

    @Autowired
    private NotificationService notificationService;
    @PostMapping
    public ResponseEntity<Void> sendSimpleMailMessage(@RequestBody EmailDto emailDto){
        log.info("NotificationController : Send Email Method");
        notificationService.sendMail(emailDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
