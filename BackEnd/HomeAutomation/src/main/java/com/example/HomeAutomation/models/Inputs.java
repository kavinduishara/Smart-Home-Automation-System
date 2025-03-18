package com.example.HomeAutomation.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Inputs {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long inPutId;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonIgnore
    private Users users;

    @OneToMany(
            mappedBy = "inPut",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<InPutAlert> inPutAlerts =new ArrayList<>();

    public Inputs() {
    }

    public Inputs(long inPutId, String name, Users users, List<InPutAlert> inPutAlerts) {
        this.inPutId = inPutId;
        this.name = name;
        this.users = users;
        this.inPutAlerts = inPutAlerts;
    }

    public long getInPutId() {
        return inPutId;
    }

    public void setInPutId(long inPutId) {
        this.inPutId = inPutId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public List<InPutAlert> getInPutAlerts() {
        return inPutAlerts;
    }

    public void setInPutAlerts(List<InPutAlert> inPutAlerts) {
        this.inPutAlerts = inPutAlerts;
    }
}
