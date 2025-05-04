package com.gym.gym_application.service.impl;

import com.gym.gym_application.dto.Credentials;
import com.gym.gym_application.dto.request.ChangeLoginRequest;
import com.gym.gym_application.exception.LoginException;
import com.gym.gym_application.exception.UserException;
import com.gym.gym_application.model.User;
import com.gym.gym_application.repository.TraineeRepository;
import com.gym.gym_application.repository.UserRepository;
import com.gym.gym_application.service.interfaces.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class LoginServiceImpl implements LoginService {
    private final UserRepository userRepository;
   // private final PasswordEncoder passwordEncoder;

    @Override
    public void changePassword(ChangeLoginRequest changeLoginRequest) {
        log.info("LoginServiceImpl : changePasswordMethod");
        User user = userRepository.findByUsername(changeLoginRequest.getUsername())
                .orElseThrow(() -> new UserException("User does not exist. Please register."));
//        if(!passwordEncoder.matches(changeLoginRequest.getOldPassword(),user.getPassword())){
//            throw new UserException("Old password is incorrect.");
//        }
        if (!user.getPassword().equals(changeLoginRequest.getOldPassword())) {
            throw new UserException("Old Password and New Password did not match.");
        }
        user.setPassword(changeLoginRequest.getNewPassword());
        //  user.setPassword(passwordEncoder.encode((changeLoginRequest.getNewPassword())));
        userRepository.save(user);
        log.info("Password changed successfully for user: {}", user.getUsername());
    }

    @Override
    public boolean verifyUser(Credentials credentials) {
        log.info("LoginServiceImpl : verifyUserMethod ");
        if (!userRepository.existsByUsername(credentials.getUsername())) {
            throw new LoginException("User does not exists. Please register.");
        }
        // return passwordEncoder.matches(credentials.getPassword(),user.getPassword());
        return userRepository.existsByUsernameAndPassword(credentials.getUsername(), credentials.getPassword());
    }
}
