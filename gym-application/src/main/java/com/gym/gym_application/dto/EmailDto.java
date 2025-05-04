package com.gym.gym_application.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailDto {
    private String toEmail;
    private String ccEmail;
    private String body;
    private String subject;
    private String remarks;
}
