package com.example.HomeAutomation.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class InPutAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Explicitly define the strategy
    private long id;

    @Column(nullable = false)
    private LocalDateTime alertTime;

    @Column(nullable = false)
    private String alertMessage;

    @Column(nullable = false)
    private boolean resolved;

    // Default constructor
    public InPutAlert() {}

    // Constructor with parameters
    public InPutAlert(LocalDateTime alertTime, String alertMessage, Inputs inPut, boolean resolved) {
        this.alertTime = alertTime;
        this.alertMessage = alertMessage;
        this.inPut = inPut;
        this.resolved = resolved;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getAlertTime() {
        return alertTime;
    }

    public void setAlertTime(LocalDateTime alertTime) {
        this.alertTime = alertTime;
    }

    public String getAlertMessage() {
        return alertMessage;
    }

    public void setAlertMessage(String alertMessage) {
        this.alertMessage = alertMessage;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    // Many-to-One relationship with Inputs
    @ManyToOne
    @JoinColumn(name = "inPutId")
    @JsonIgnore
    private Inputs inPut;

    public Inputs getInPut() {
        return inPut;
    }

    public void setInPut(Inputs inPut) {
        this.inPut = inPut;
    }
}
