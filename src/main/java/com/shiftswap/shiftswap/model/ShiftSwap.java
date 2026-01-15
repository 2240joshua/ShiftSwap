package com.shiftswap.shiftswap.model;
import jakarta.persistence.*;
import com.shiftswap.shiftswap.model.*;

@Entity
@Table(name="ShiftSwap")
public class ShiftSwap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Shift shift;
    private User fromUser;
    private User toUser;
    private String status;
    private Long createdAt;

    public ShiftSwap() {}

    public ShiftSwap(Shift shift, User fromUser, User toUser, String Status, Long createdAt){


    } 


}
