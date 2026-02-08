package com.example.userserviceasync.event;

public class UserSignedUpEvent {
  private Long userId;
  private String name;
  
  public UserSignedUpEvent() {
  }

  public UserSignedUpEvent(Long userId, String name) {
    this.userId = userId;
    this.name = name;
  }

  public Long getUserId() {
    return userId;
  }

  public String getName() {
    return name;
  }
}
