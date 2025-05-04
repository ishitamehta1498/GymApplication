package com.email.notification_service.repository;

import com.email.notification_service.model.Email;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmailRepository  extends MongoRepository<Email, Integer> {
}
