package com.example.HomeAutomation.repository;

import com.example.HomeAutomation.models.FirebaseToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FirebaseTokenRepository extends JpaRepository<FirebaseToken,Long> {
    FirebaseToken findByUsername(String username);
}
