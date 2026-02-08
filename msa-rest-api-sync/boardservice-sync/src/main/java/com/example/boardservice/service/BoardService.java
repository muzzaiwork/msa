package com.example.boardservice.service;

import com.example.boardservice.client.PointClient;
import com.example.boardservice.client.UserClient;
import com.example.boardservice.domain.Board;
import com.example.boardservice.dto.BoardResponseDto;
import com.example.boardservice.dto.CreateBoardRequestDto;
import com.example.boardservice.dto.UserDto;
import com.example.boardservice.repository.BoardRepository;
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

  public BoardService(BoardRepository boardRepository, UserClient userClient,
      PointClient pointClient) {
    this.boardRepository = boardRepository;
    this.userClient = userClient;
    this.pointClient = pointClient;
  }

  @Transactional
  public void create(CreateBoardRequestDto createBoardRequestDto) {
    // [SAGA 패턴] 서비스 전체의 데이터 일관성을 맞추기 위해 상태 플래그를 관리합니다.
    
    // 게시글 저장을 성공했는 지 판단하는 플래그
    boolean isBoardCreated = false;
    Long savedBoardId = null;

    // 포인트 차감을 성공했는 지 판단하는 플래그
    boolean isPointDeducted = false;

    try {
      // 1단계: 포인트 차감 (외부 서비스 호출)
      // 게시글 작성 전 100 포인트 차감
      pointClient.deductPoints(createBoardRequestDto.getUserId(), 100);
      isPointDeducted = true; // 포인트 차감 성공 플래그 설정

      // 2단계: 게시글 저장 (로컬 트랜잭션)
      Board board = new Board(
          createBoardRequestDto.getTitle(),
          createBoardRequestDto.getContent(),
          createBoardRequestDto.getUserId()
      );

      Board savedBoard = this.boardRepository.save(board);
      savedBoardId = savedBoard.getBoardId();
      isBoardCreated = true; // 게시글 저장 성공 플래그 설정

      // 3단계: 활동 점수 적립 (외부 서비스 호출)
      // 게시글 작성 시 작성자에게 활동 점수 10점 부여
      // 만약 여기서 예외가 발생하면 catch 블록의 보상 트랜잭션이 실행됩니다.
      userClient.addActivityScore(createBoardRequestDto.getUserId(), 10);
      
    } catch (Exception e) {
      // [보상 트랜잭션 (Compensating Transaction)]
      // 이전에 성공했던 작업들을 논리적으로 취소하여 데이터 일관성을 유지합니다.
      
      if (isBoardCreated) {
        // 이미 저장된 게시글이 있다면 삭제 (2단계 취소)
        this.boardRepository.deleteById(savedBoardId);
      }
      if (isPointDeducted) {
        // 이미 차감된 포인트가 있다면 다시 적립 (1단계 취소)
        pointClient.addPoints(createBoardRequestDto.getUserId(), 100);
      }

      // 최종적으로 예외를 다시 던져서 호출자에게 실패를 알립니다.
      throw e;
    }
  }

  @Transactional(readOnly = true)
  public BoardResponseDto findById(Long boardId) {
    Board board = this.boardRepository.findById(boardId)
        .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id=" + boardId));

    UserDto userResponse = userClient.getUser(board.getUserId()).orElse(null);

    return new BoardResponseDto(
        board.getBoardId(),
        board.getTitle(),
        board.getContent(),
        userResponse
    );
  }

  @Transactional(readOnly = true)
  public List<BoardResponseDto> findAll() {
    List<Board> boards = this.boardRepository.findAll();
    
    List<Long> userIds = boards.stream()
        .map(Board::getUserId)
        .distinct()
        .collect(Collectors.toList());

    Map<Long, UserDto> userMap = userClient.getUsers(userIds).stream()
        .collect(Collectors.toMap(UserDto::getUserId, Function.identity()));

    return boards.stream()
        .map(board -> new BoardResponseDto(
            board.getBoardId(),
            board.getTitle(),
            board.getContent(),
            userMap.get(board.getUserId())
        ))
        .collect(Collectors.toList());
  }
}
