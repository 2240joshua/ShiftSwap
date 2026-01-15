package com.shiftswap.shiftswap.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@Table(name = "shifts")
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long startTime;
    private Long endTime;

    private String role;
    private String day;
    @ManyToOne
    @JoinColumn(name = "assigned_user_id")
    @JsonIgnoreProperties("shifts")
    private User assignedUser;


    public Shift() {}

    public Shift(Long startTime, Long endTime, String role, String day) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.role = role;
        this.day = day;
    }
    public Long getId() {return id;}
    public Long getStartTime() {return startTime;}

    public void setStartTime(Long startTime){this.startTime = startTime;}
    public Long getEndTime() {return endTime;}

    public void setEndTime(Long endTime){this.endTime = endTime;}
    public String getRole() { return role; }
    
    public void setRole(String role) { this.role = role; }

    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }

    public User getAssignedUser() {
    return assignedUser;
}

    public void setAssignedUser(User assignedUser) {
    this.assignedUser = assignedUser;
}

}


