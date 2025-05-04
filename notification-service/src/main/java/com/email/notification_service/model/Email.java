package com.email.notification_service.model;

import org.springframework.data.annotation.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "email")
public class Email {
    @Id
    private String id;
    private String toEmail;
    private String ccEmail;
    private String body;
    private String subject;
    private String remarks;
    private String status;
}
