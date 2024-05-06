package com.example.demo.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "userinout")
public class UserInOut {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String pin;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;
    private boolean isEntry;

    @PrePersist
    public void prePersist() {
        this.checkInTime = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.checkOutTime = LocalDateTime.now();
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(LocalDateTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public boolean isEntry() {
        return isEntry;
    }

    public void setEntry(boolean entry) {
        isEntry = entry;
    }

}