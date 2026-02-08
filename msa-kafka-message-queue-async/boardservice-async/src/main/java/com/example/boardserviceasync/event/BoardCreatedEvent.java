package com.example.boardserviceasync.event;

public class BoardCreatedEvent {
  private Long userId;

  public BoardCreatedEvent() {
  }

  public BoardCreatedEvent(Long userId) {
    this.userId = userId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }
}
