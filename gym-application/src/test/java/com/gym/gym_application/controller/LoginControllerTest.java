package com.gym.gym_application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.gym_application.dto.Credentials;
import com.gym.gym_application.dto.request.ChangeLoginRequest;
import com.gym.gym_application.exception.UserException;
import com.gym.gym_application.service.interfaces.LoginService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(LoginController.class)
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LoginService loginService;

    @TestConfiguration
    static class Config {
        @Bean
        public LoginService loginService() {
            return Mockito.mock(LoginService.class);
        }
    }

    @Test
    void testVerifyUser() throws Exception {
        Credentials credentials = new Credentials("testuser", "Password@1");
        when(loginService.verifyUser(any(Credentials.class))).thenReturn(true);
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isOk());
        verify(loginService, times(1)).verifyUser(any(Credentials.class));
    }

    @Test
    void testVerifyWithUserInvalidDetails() throws Exception {
        Credentials credentials = new Credentials("testuser", "Password@1");
        when(loginService.verifyUser(any(Credentials.class))).thenReturn(false);
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testVerifyUserWithUserException() throws Exception {
        Credentials credentials = new Credentials("testuser", "Password@1");
        when(loginService.verifyUser(any(Credentials.class))).thenThrow(UserException.class);
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testChangePassword() throws Exception {
        ChangeLoginRequest changeLoginRequest = new ChangeLoginRequest();
        changeLoginRequest.setUsername("testuser");
        changeLoginRequest.setOldPassword("oldPass@1#");
        changeLoginRequest.setNewPassword("newPass@1#");
        doNothing().when(loginService).changePassword(any(ChangeLoginRequest.class));
        mockMvc.perform(post("/login/changepassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changeLoginRequest)))
                .andExpect(status().isOk());
    }

}