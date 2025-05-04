package com.gym.gym_application.service.interfaces;

import com.gym.gym_application.dto.Credentials;
import com.gym.gym_application.dto.request.ChangeLoginRequest;

public interface LoginService {
    void changePassword(ChangeLoginRequest changeLoginRequest);

    boolean verifyUser(Credentials credentials);
}
