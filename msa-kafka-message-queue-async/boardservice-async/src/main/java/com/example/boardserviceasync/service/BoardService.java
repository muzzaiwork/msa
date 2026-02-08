package com.example.boardserviceasync.service;

import com.example.boardserviceasync.client.PointClient;
import com.example.boardserviceasync.client.UserClient;
import com.example.boardserviceasync.domain.Board;
import com.example.boardserviceasync.dto.BoardResponseDto;
import com.example.boardserviceasync.dto.CreateBoardRequestDto;
import com.example.boardserviceasync.dto.UserDto;
import com.example.boardserviceasync.event.BoardCreatedEvent;
import com.example.boardserviceasync.repository.BoardRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BoardService {
  private final BoardRepository boardRepository;
  private final UserClient userClient;
  private final PointClient pointClient;
  private final KafkaTemplate<String, String> kafkaTemplate;

  public BoardService(
      BoardRepository boardRepository,
      UserClient userClient,
      PointClient pointClient,
      KafkaTemplate<String, String> kafkaTemplate
  ) {
    this.boardRepository = boardRepository;
    this.userClient = userClient;
    this.pointClient = pointClient;
    this.kafkaTemplate = kafkaTemplate;
  }

  public void create(Long userId, CreateBoardRequestDto createBoardRequestDto) {
    // 게시글 저장을 성공했는 지 판단하는 플래그
    boolean isBoardCreated = false;
    Long savedBoardId = null;
    
    // 포인트 차감을 성공했는 지 판단하는 플래그
    boolean isPointDeducted = false;

    try {
      // 게시글 작성 전 100 포인트 차감
      pointClient.deductPoints(userId, 100);
      isPointDeducted = true; // 포인트 차감 성공 플래그
      System.out.println("포인트 차감 성공");

      // 게시글 작성
      Board board = new Board(
          createBoardRequestDto.getTitle(),
          createBoardRequestDto.getContent(),
          userId
      );

      Board savedBoard = this.boardRepository.save(board);
      savedBoardId = savedBoard.getBoardId();
      isBoardCreated = true; // 게시글 저장 성공 플래그
      System.out.println("게시글 저장 성공");

      // 게시글 작성 시 작성자에게 활동 점수 10점 부여
      userClient.addActivityScore(userId, 10);
      System.out.println("활동 점수 적립 성공");
      
      // '게시글 작성 완료' 이벤트 발행
      BoardCreatedEvent boardCreatedEvent
          = new BoardCreatedEvent(userId);
      this.kafkaTemplate.send("board.created", toJsonString(boardCreatedEvent));
      System.out.println("게시글 작성 완료 이벤트 발행");

    } catch (Exception e) {
      if (isBoardCreated) {
        // 게시글 작성 보상 트랜잭션 => 게시글 삭제
        this.boardRepository.deleteById(savedBoardId);
        System.out.println("[보상 트랜잭션] 게시글 삭제");
      }
      if (isPointDeducted) {
        // 포인트 차감 보상 트랜잭션 => 포인트 적립
        pointClient.addPoints(userId, 100);
        System.out.println("[보상 트랜잭션] 포인트 적립");
      }

      // 실패 응답으로 처리하기 위해 예외 던지기
      throw e;
    }
  }

  @Transactional(readOnly = true)
  public BoardResponseDto findById(Long boardId) {
    Board board = boardRepository.findById(boardId)
        .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

    // BoardResponseDto 생성
    BoardResponseDto boardResponseDto = new BoardResponseDto(
        board.getBoardId(),
        board.getTitle(),
        board.getContent(),
        new UserDto(
            board.getUser().getUserId(),
            board.getUser().getName()
        )
    );

    return boardResponseDto;
  }

  @Transactional(readOnly = true)
  public List<BoardResponseDto> findAll() {
    List<Board> boards = boardRepository.findAll();

    return boards.stream()
        .map(board -> new BoardResponseDto(
            board.getBoardId(),
            board.getTitle(),
            board.getContent(),
            new UserDto(
                board.getUser().getUserId(),
                board.getUser().getName()
            )
        ))
        .toList();
  }

  // 객체를 Json 형태의 String으로 만들어주는 메서드
  // (클래스로 분리하면 더 좋지만 편의를 위해 메서드로만 분리)
  private String toJsonString(Object object) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      String message = objectMapper.writeValueAsString(object);
      return message;
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Json 직렬화 실패");
    }
  }
}
