package com.example.boardserviceasync.controller;

import com.example.boardserviceasync.dto.BoardResponseDto;
import com.example.boardserviceasync.dto.CreateBoardRequestDto;
import com.example.boardserviceasync.service.BoardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boards")
public class BoardController {
  private final BoardService boardService;

  public BoardController(BoardService boardService) {
    this.boardService = boardService;
  }

  @PostMapping
  public ResponseEntity<Void> create(
      @RequestBody CreateBoardRequestDto createBoardRequestDto
  ) {
    boardService.create(createBoardRequestDto);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{boardId}")
  public ResponseEntity<BoardResponseDto> findById(
      @PathVariable Long boardId
  ) {
    return ResponseEntity.ok(boardService.findById(boardId));
  }

  @GetMapping
  public ResponseEntity<List<BoardResponseDto>> findAll() {
    return ResponseEntity.ok(boardService.findAll());
  }
}
