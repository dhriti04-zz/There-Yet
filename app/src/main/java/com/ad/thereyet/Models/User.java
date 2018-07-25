package com.ad.thereyet.Models;

public class User {
    public String username;
    public String email;
    public String Fname;
    public String Lname;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, String fname, String lname) {
        this.username = username;
        this.email = email;
        Fname = fname;
        Lname = lname;
    }
}
