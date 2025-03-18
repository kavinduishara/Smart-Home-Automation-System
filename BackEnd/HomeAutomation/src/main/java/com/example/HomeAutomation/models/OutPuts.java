package com.example.HomeAutomation.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OutPuts {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long outPutId;

    @Column(nullable = false)
    private String name;

    private double wattage;

    private boolean switchOn;

    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonIgnore
    private Users users;

    @OneToMany(
            mappedBy = "outPuts",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<StatusHistory> statusHistories =new ArrayList<>();

    public long getOutPutId() {
        return outPutId;
    }

    public void setOutPutId(long outPutId) {
        this.outPutId = outPutId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWattage() {
        return wattage;
    }

    public void setWattage(double wattage) {
        this.wattage = wattage;
    }

    public boolean isSwitchOn() {
        return switchOn;
    }

    public void setSwitchOn(boolean switchOn) {
        this.switchOn = switchOn;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public List<StatusHistory> getStatusHistories() {
        return statusHistories;
    }

    public void setStatusHistories(List<StatusHistory> statusHistories) {
        this.statusHistories = statusHistories;
    }
}
