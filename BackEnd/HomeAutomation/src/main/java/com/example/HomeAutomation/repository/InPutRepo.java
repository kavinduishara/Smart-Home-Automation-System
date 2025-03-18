package com.example.HomeAutomation.repository;

import com.example.HomeAutomation.models.Inputs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InPutRepo extends JpaRepository<Inputs,Long> {
    List<Inputs> findByUsers_UserId(long userId);
}
