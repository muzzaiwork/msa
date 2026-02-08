package com.example.userserviceasync.dto;

public class LoginRequestDto {
    private String email;
    private String password;

    public LoginRequestDto() {
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}