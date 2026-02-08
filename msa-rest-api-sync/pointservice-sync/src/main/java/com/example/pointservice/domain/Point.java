package com.example.pointservice.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "points")
public class Point {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "point_id")
  private Long pointId;

  @Column(name = "user_id")
  private Long userId;

  private int amount;

  public Point() {
  }

  public Point(Long userId, int amount) {
    this.userId = userId;
    this.amount = amount;
  }

  public Long getPointId() {
    return pointId;
  }

  public Long getUserId() {
    return userId;
  }

  public int getAmount() {
    return amount;
  }
  
  // 포인트 적립 (나중에 Service 로직 짤 때 사용)
  public void addAmount(int amount) {
    this.amount += amount;
  }
  
  // 포인트 차감 (나중에 Service 로직 짤 때 사용)
  public void deductAmount(int amount) {
    this.amount -= amount;
  }
}
