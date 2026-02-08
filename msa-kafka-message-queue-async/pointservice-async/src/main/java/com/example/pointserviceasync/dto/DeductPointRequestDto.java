package com.example.pointserviceasync.dto;

public class DeductPointRequestDto {
  private Long userId;
  private int amount;

  public DeductPointRequestDto() {
  }

  public DeductPointRequestDto(Long userId, int amount) {
    this.userId = userId;
    this.amount = amount;
  }

  public Long getUserId() {
    return userId;
  }

  public int getAmount() {
    return amount;
  }
}
