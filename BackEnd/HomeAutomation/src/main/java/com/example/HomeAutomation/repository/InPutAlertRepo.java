package com.example.HomeAutomation.repository;

import com.example.HomeAutomation.models.InPutAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InPutAlertRepo extends JpaRepository<InPutAlert, Long> {
    List<InPutAlert> findByInPutId(long inPutId);
    @Query("SELECT a FROM InPutAlert a WHERE a.inPut.user.id = :userId")
    List<InPutAlert> findAlertsByUserId(@Param("userId") long userId);

    List<InPutAlert> findByInPut_User_IdAndReadFalse(long userId);



}
