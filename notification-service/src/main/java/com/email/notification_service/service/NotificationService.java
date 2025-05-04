package com.email.notification_service.service;

import com.email.notification_service.dto.EmailDto;

public interface NotificationService {
    void sendMail(EmailDto emailDto);
}
