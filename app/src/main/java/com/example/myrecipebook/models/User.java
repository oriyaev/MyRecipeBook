package com.example.myrecipebook.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String username;
    public String email;
    public String password;
    public long createdAt;

    public User() {
        this.createdAt = System.currentTimeMillis();
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.createdAt = System.currentTimeMillis();
    }
}