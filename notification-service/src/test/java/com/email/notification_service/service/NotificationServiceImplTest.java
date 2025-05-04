package com.email.notification_service.service;

import com.email.notification_service.dto.EmailDto;
import com.email.notification_service.model.Email;
import com.email.notification_service.repository.EmailRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {
    @Mock
    private JavaMailSender javaMailSender;
    @Mock
    private EmailRepository emailRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private NotificationServiceImpl notificationService;

    private static final String FROM_EMAIL = "sender@example.com";

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        //@Value fields like emailFrom won't auto inject unless you manually set them.
        Field emailFromField = NotificationServiceImpl.class.getDeclaredField("emailFrom");
        emailFromField.setAccessible(true);
        emailFromField.set(notificationService, FROM_EMAIL);
    }

    @Test
    public void sendMailTest() {
        EmailDto emailDto = new EmailDto();
        emailDto.setToEmail("recipient@example.com");
        emailDto.setCcEmail("cc@example.com");
        emailDto.setSubject("Test Subject");
        emailDto.setBody("Test Body");
        Email email = new Email();
        email.setToEmail(emailDto.getToEmail());
        email.setCcEmail(emailDto.getCcEmail());
        email.setSubject(emailDto.getSubject());
        email.setBody(emailDto.getBody());
        when(modelMapper.map(emailDto, Email.class)).thenReturn(email);
        when(emailRepository.insert(email)).thenReturn(email);
        notificationService.sendMail(emailDto);
        verify(emailRepository, times(1)).insert(email);
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
        assertEquals(emailDto.getToEmail(), email.getToEmail());
    }

    @Test
    public void sendMail_shouldSetFromEmailCorrectly() {
        EmailDto emailDto = new EmailDto();
        emailDto.setToEmail("recipient@example.com");
        emailDto.setCcEmail("cc@example.com");
        emailDto.setSubject("Test Subject");
        emailDto.setBody("Test Body");
        Email email = new Email();
        when(modelMapper.map(emailDto, Email.class)).thenReturn(email);
        when(emailRepository.insert(email)).thenReturn(email);
        notificationService.sendMail(emailDto);
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(messageCaptor.capture());
        verify(emailRepository, times(1)).insert(email);
        SimpleMailMessage capturedMessage = messageCaptor.getValue();
        // Assert
        assertEquals("sender@example.com", capturedMessage.getFrom(), "From email should match");
        assertEquals("recipient@example.com", capturedMessage.getTo()[0], "To email should match");
        assertEquals("cc@example.com", capturedMessage.getCc()[0], "CC email should match");
        assertEquals("Test Subject", capturedMessage.getSubject(), "Subject should match");
        assertEquals("Test Body", capturedMessage.getText(), "Body should match");
    }

    @Test
    public void sendMail_nullDto_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            notificationService.sendMail(null);
        });
        assertEquals("EmailDto cannot be null", exception.getMessage());
        // No email should be sent, no db insert
        verify(javaMailSender, never()).send(any(SimpleMailMessage.class));
        verify(emailRepository, never()).insert(any(Email.class));
    }

}