package com.example.boardserviceasync.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "boards")
public class Board {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long boardId;
  
  private String title;
  
  private String content;

  @ManyToOne
  @JoinColumn(name = "user_id", insertable = false, updatable = false) // 조회용
  private User user;

  @Column(name = "user_id")
  private Long userId;

  public Board() {
  }

  public Board(String title, String content, Long userId) {
    this.title = title;
    this.content = content;
    this.userId = userId;
  }

  public Long getBoardId() {
    return boardId;
  }

  public String getTitle() {
    return title;
  }

  public String getContent() {
    return content;
  }

  public User getUser() {
    return user;
  }

  public Long getUserId() {
    return userId;
  }
}
