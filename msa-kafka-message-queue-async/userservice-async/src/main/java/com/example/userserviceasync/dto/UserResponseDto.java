package com.example.userserviceasync.dto;

public class UserResponseDto {
    private Long userId;
    private String name;
    private String email;
    private int activityScore;

    public UserResponseDto(Long userId, String name, String email, int activityScore) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.activityScore = activityScore;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getActivityScore() {
        return activityScore;
    }
}
