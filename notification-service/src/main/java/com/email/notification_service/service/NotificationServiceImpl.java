package com.email.notification_service.service;

import com.email.notification_service.dto.EmailDto;
import com.email.notification_service.model.Email;
import com.email.notification_service.repository.EmailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService{
    private final JavaMailSender mailSender;
    private final ModelMapper modelMapper;
    private final EmailRepository emailRepository;

    @Value("${spring.mail.username}")
    private String emailFrom;

    @Override
    @KafkaListener(topics = "email-messages", groupId = "emailapp")
    public void sendMail(EmailDto emailDto) {
        log.info("NotificationServiceImpl : sendMail");
        if (emailDto == null) {
            throw new IllegalArgumentException("EmailDto cannot be null");
        }
        if (emailDto.getRemarks() == null) {
            emailDto.setRemarks("No additional remarks");
        }
        emailDto.setStatus("Sent");
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom(emailFrom);
        message.setTo(emailDto.getToEmail());
        message.setSubject(emailDto.getSubject());
        message.setCc(emailDto.getCcEmail());
        message.setText(emailDto.getBody());
//        if(emailDto.getCcEmail()!=null) {
//            message.setCc(emailDto.getCcEmail());
//        }
//        if(emailDto.getSubject()!=null){
//            message.setSubject(emailDto.getSubject());
//        }
//        if(emailDto.getBody()!=null){
//            message.setText(emailDto.getBody());
//        }
        mailSender.send(message);
        emailRepository.insert(modelMapper.map(emailDto, Email.class));
    }
}

