package com.gym.gym_application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerRegistrationRequest {
    @NotBlank(message = "firstname is required")
    private String firstname;
    @NotBlank(message = "lastname is required")
    private String lastname;
    @Email(message = "invalid email format")
    @NotBlank(message="email is required")
    private String email;
    @Pattern(
            regexp = "^(?i)(fitness|yoga|zumba|stretching|resistance)$",
            message = "specialization should be one of: fitness,yoga,zumba,stretching or resistance"
    )
    private String specialization;
}
