package com.gym.gym_application.controller;

import com.gym.gym_application.dto.Credentials;
import com.gym.gym_application.dto.request.ChangeLoginRequest;
import com.gym.gym_application.service.interfaces.LoginService;
import com.gym.gym_application.service.interfaces.TraineeService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RequestMapping("/login")
@RestController
public class LoginController {
    private LoginService loginService;

    @PostMapping("/changepassword")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangeLoginRequest changeLoginRequest) {
        log.info("LoginController : changePassword ");
        loginService.changePassword(changeLoginRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Void> verifyUser(@RequestBody @Valid Credentials credentials){
        log.info("LoginController : verifyUser ");
        if(loginService.verifyUser(credentials)){
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
