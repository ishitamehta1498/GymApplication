package com.gym.gym_application.feign;

import com.gym.gym_application.dto.*;
import org.springframework.cloud.openfeign.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@FeignClient( url = "http://localhost:3000/training-summary", name="training-summary")
public interface ReportFeignClient {
    @PostMapping
    ResponseEntity<Void> addTrainingSummary(@RequestBody TrainingSummaryDto trainingSummaryDto);


//    @GetMapping
//    public ResponseEntity<Void> getAllTrainings(@RequestParam("trainerUsername") String trainerUsername, HttpServletResponse response);

}
