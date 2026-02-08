package com.example.boardserviceasync.repository;

import com.example.boardserviceasync.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
