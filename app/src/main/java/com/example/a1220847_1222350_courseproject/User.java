package com.example.a1220847_1222350_courseproject;

public class User {
    private String email;
    private String first_name;
    private String last_name;
    private String password;
    public User(String email, String first_name, String last_name, String password){
        this.email=email;
        this.first_name=first_name;
        this.last_name=last_name;
        this.password=password;
    }
    public String getEmail() {
        return email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getPassword() {
        return password;
    }

}