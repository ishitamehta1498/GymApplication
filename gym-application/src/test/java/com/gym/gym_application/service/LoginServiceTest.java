package com.gym.gym_application.service;

import com.gym.gym_application.dto.Credentials;
import com.gym.gym_application.dto.request.ChangeLoginRequest;
import com.gym.gym_application.exception.LoginException;
import com.gym.gym_application.exception.UserException;
import com.gym.gym_application.model.User;
import com.gym.gym_application.repository.UserRepository;
import com.gym.gym_application.service.impl.LoginServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private LoginServiceImpl loginService;

    @Test
    void testVerifyUserSuccess() {
        Credentials credentials=new Credentials("testUser","testPassword");
        when(userRepository.existsByUsername(credentials.getUsername()))
                .thenReturn(true);
        when(userRepository.existsByUsernameAndPassword("testUser","testPassword"))
                .thenReturn(true);
        assertTrue(loginService.verifyUser(credentials));
    }

    @Test
    void testVerifyUserWithInvalidUser(){
        Credentials credentials=new Credentials("testUser","testPassword");
        when(userRepository.existsByUsername(credentials.getUsername()))
                .thenReturn(false);
        LoginException loginException = assertThrows(LoginException.class, () -> loginService.verifyUser(credentials));
        assertEquals("User does not exists. Please register.", loginException.getMessage());
    }

    @Test
    void testVerifyUserWithWrongPassword() {
        Credentials credentials = new Credentials("testUser","testPassword");
        when(userRepository.existsByUsername(credentials.getUsername())).thenReturn(true);
        when(userRepository.existsByUsernameAndPassword("testUser","testPassword")).thenReturn(false);
        assertFalse(loginService.verifyUser(credentials));
    }

    @Test
    void testChangePasswordSuccess() {
        ChangeLoginRequest request = new ChangeLoginRequest("testUser", "oldPass", "newPass");
        Optional<User> user=Optional.of(new User(1,"UserFirst","UserLast","testUser","oldPass","testUser@gmail.com",true, LocalDate.now()));
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        loginService.changePassword(request);
        assertEquals("newPass", user.get().getPassword());
        verify(userRepository, times(1)).save(user.get());
    }

    @Test
    void testChangePasswordWithInvalidUser() {
        ChangeLoginRequest changeLoginRequest=new ChangeLoginRequest("unknownUser", "oldPass", "newPass");
        Optional<User> user=Optional.of(new User());
        when(userRepository.findByUsername(changeLoginRequest.getUsername())).thenReturn(Optional.empty());
        UserException userException = assertThrows(UserException.class, () -> loginService.changePassword(changeLoginRequest));
        assertEquals("User does not exist. Please register.",userException.getMessage());
        verify(userRepository,times(0)).save(user.get());
    }

    @Test
    void testChangePasswordWithWrongOldPassword() {
        ChangeLoginRequest request = new ChangeLoginRequest("testUser", "wrongOldPass", "newPass");
        Optional<User> user=Optional.of(new User(1,"UserFirst","UserLast","testUser","oldPass","testUser@gmail.com",true, LocalDate.now()));
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        UserException exception = assertThrows(UserException.class, () -> loginService.changePassword(request));
        assertEquals("Old Password and New Password did not match.", exception.getMessage());
        verify(userRepository, times(0)).save(user.get());
    }
}