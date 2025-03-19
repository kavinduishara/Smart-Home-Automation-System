package com.example.HomeAutomation.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class StatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(nullable = false)
    private LocalDateTime onTime;

    private LocalDateTime offTime;
    @ManyToOne
    @JoinColumn(name = "outPutId")
    @JsonIgnore
    private OutPuts outPuts;

    public StatusHistory() {
    }

    public StatusHistory(LocalDateTime onTime, LocalDateTime offTime, OutPuts outPuts) {
        this.onTime = onTime;
        this.offTime = offTime;
        this.outPuts = outPuts;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getOnTime() {
        return onTime;
    }

    public void setOnTime(LocalDateTime onTime) {
        this.onTime = onTime;
    }

    public LocalDateTime getOffTime() {
        return offTime;
    }

    public void setOffTime(LocalDateTime offTime) {
        this.offTime = offTime;
    }

    public OutPuts getOutPuts() {
        return outPuts;
    }

    public void setOutPuts(OutPuts outPuts) {
        this.outPuts = outPuts;
    }
}
