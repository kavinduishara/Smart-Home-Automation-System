package com.example.HomeAutomation.repository;

import com.example.HomeAutomation.models.OutPuts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OutPutRepo extends JpaRepository<OutPuts,Long> {
    List<OutPuts> findByUsers_UserId(long userId);
}
