package com.example.userservice.dto;

public class AddActivityScoreRequestDto {
  private Long userId;
  private int score;

  public AddActivityScoreRequestDto() {
  }

  public AddActivityScoreRequestDto(Long userId, int score) {
    this.userId = userId;
    this.score = score;
  }

  public Long getUserId() {
    return userId;
  }

  public int getScore() {
    return score;
  }
}
