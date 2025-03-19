package com.example.HomeAutomation.repository;

import com.example.HomeAutomation.models.StatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusHistoryRepo extends JpaRepository<StatusHistory,Long> {
}
