package com.shiftswap.shiftswap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "shift_swaps")
public class ShiftSwap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shift_id")
    @JsonIgnoreProperties({"assignedUser"})
    private Shift shift;

    @ManyToOne
    @JoinColumn(name = "from_user_id")
    @JsonIgnoreProperties({"password"})
    private User fromUser;

    @ManyToOne
    @JoinColumn(name = "to_user_id")
    @JsonIgnoreProperties({"password"})
    private User toUser;

    private String status;
    private Long createdAt;

    public ShiftSwap() {}

    public ShiftSwap(Shift shift, User fromUser, User toUser, String status, Long createdAt) {
        this.shift = shift;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }

    public Shift getShift() { return shift; }
    public void setShift(Shift shift) { this.shift = shift; }

    public User getFromUser() { return fromUser; }
    public void setFromUser(User fromUser) { this.fromUser = fromUser; }

    public User getToUser() { return toUser; }
    public void setToUser(User toUser) { this.toUser = toUser; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
}
