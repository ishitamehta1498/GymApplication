package com.gym.gym_application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTrainerRequest {
    @NotBlank(message = "username is required")
    private String username;
    @NotBlank(message = "firstname is required")
    private String firstname;
    @NotBlank(message = "lastname is required")
    private String lastname;
    @Pattern(
            regexp = "^(?i)(fitness|yoga|zumba|stretching|resistance)$",
            message = "specialization should be one of: fitness,yoga,zumba,stretching or resistance"
    )
    private String specialization;
//    @Pattern(
//            regexp = "^(?i)(True|False)$",
//            message = "Activity status must be either True or False"
//    )

    @JsonProperty("isActive")
    private boolean isActive;

}
