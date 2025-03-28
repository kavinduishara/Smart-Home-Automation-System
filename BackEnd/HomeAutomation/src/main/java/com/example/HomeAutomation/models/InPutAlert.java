package com.example.HomeAutomation.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class InPutAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(nullable = false)
    private LocalDateTime alertTime;
    @Column(nullable = false)
    private String alertMessage;

    @Column(nullable = false)
    private boolean read;

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    @ManyToOne
    @JoinColumn(name = "inPutId")
    @JsonIgnore
    private Inputs inPut;

    public InPutAlert() {
    }

    public InPutAlert(LocalDateTime alertTime, String alertMessage, Inputs inPut,boolean read) {
        this.alertTime = alertTime;
        this.alertMessage = alertMessage;
        this.inPut = inPut;
        this.read=read;
    }

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

    public Inputs getInPut() {
        return inPut;
    }

    public void setInPut(Inputs inPut) {
        this.inPut = inPut;
    }
}
