package com.reports.ReportManagement.repository;

import com.reports.ReportManagement.model.*;
import org.springframework.data.mongodb.repository.*;
import org.springframework.stereotype.*;

@Repository

public interface TrainingSummaryRepository extends MongoRepository<TrainingSummary,Integer> {
}
