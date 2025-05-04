package com.reports.ReportManagement;

import org.modelmapper.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.*;

@SpringBootApplication
public class ReportManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReportManagementApplication.class, args);
	}

	@Bean
	public ModelMapper getModelMapper() {
		return new ModelMapper();
	}

}
