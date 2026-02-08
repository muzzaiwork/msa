package com.example.boardserviceasync.service;

import com.example.boardserviceasync.client.PointClient;
import com.example.boardserviceasync.client.UserClient;
import com.example.boardserviceasync.domain.Board;
import com.example.boardserviceasync.domain.User;
import com.example.boardserviceasync.dto.BoardResponseDto;
import com.example.boardserviceasync.dto.CreateBoardRequestDto;
import com.example.boardserviceasync.dto.UserDto;
import com.example.boardserviceasync.repository.BoardRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @ExtendWith(MockitoExtension.class): Mockito 라이브러리를 사용하기 위한 어노테이션입니다.
 * Spring Context를 로드하지 않고 순수하게 객체의 관계만 Mock으로 설정하여 매우 빠릅니다.
 */
@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    /**
     * @Mock: 가짜 객체를 생성합니다.
     * BoardRepository의 실제 데이터베이스 연동 없이도 동작을 정의할 수 있습니다.
     */
    @Mock
    private BoardRepository boardRepository;

    @Mock
    private UserClient userClient;

    @Mock
    private PointClient pointClient;

    @Mock
    private org.springframework.kafka.core.KafkaTemplate<String, String> kafkaTemplate;

    /**
     * @InjectMocks: 위에서 생성한 @Mock 가짜 객체들을 해당 서비스 객체에 자동으로 주입해줍니다.
     * 즉, boardService가 사용하는 boardRepository를 가짜(Mock) 객체로 갈아끼워줍니다.
     */
    @InjectMocks
    private BoardService boardService;

    @Test
    @DisplayName("게시글 생성 성공 테스트")
    void createBoardSuccess() throws Exception {
        // given: 테스트를 위한 데이터를 준비합니다.
        CreateBoardRequestDto requestDto = new CreateBoardRequestDto();
        setField(requestDto, "title", "테스트 제목");
        setField(requestDto, "content", "테스트 내용");
        Long userId = 1L;

        Board savedBoard = new Board("테스트 제목", "테스트 내용", userId);
        setField(savedBoard, "boardId", 1L);
        when(boardRepository.save(any(Board.class))).thenReturn(savedBoard);

        // when: 실제 기능을 실행합니다.
        boardService.create(userId, requestDto);

        /**
         * then: 실행 결과를 검증합니다.
         * verify(boardRepository, times(1)).save(any(Board.class)):
         * boardRepository의 save() 메서드가 최소 1번(times(1)) 호출되었는지 확인합니다.
         * 실제 DB에 저장되지는 않지만, 로직 흐름상 저장을 시도했는지를 가짜 객체(Mock)를 통해 검증하는 것입니다.
         */
        verify(pointClient, times(1)).deductPoints(userId, 100);
        verify(boardRepository, times(1)).save(any(Board.class));
        verify(userClient, times(1)).addActivityScore(userId, 10);
    }

    @Test
    @DisplayName("게시글 단건 조회 테스트")
    void findByIdSuccess() throws Exception {
        // given: 테스트용 Board 객체를 생성합니다.
        User user = new User(1L, "테스트 유저");
        Board board = new Board("제목", "내용", 1L);
        setField(board, "boardId", 1L);
        setField(board, "user", user);

        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));

        // when: 조회를 수행합니다.
        BoardResponseDto response = boardService.findById(1L);

        // then: 결과가 예상과 일치하는지 확인합니다.
        assertEquals("제목", response.getTitle());
        assertEquals("내용", response.getContent());
        assertEquals(1L, response.getUser().getUserId());
        assertEquals("테스트 유저", response.getUser().getName());
        verify(boardRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("게시글 전체 조회 테스트")
    void findAllSuccess() throws Exception {
        // given: 테스트용 리스트를 준비합니다.
        User user1 = new User(1L, "테스트 유저1");
        User user2 = new User(2L, "테스트 유저2");
        Board board1 = new Board("제목1", "내용1", 1L);
        setField(board1, "user", user1);
        Board board2 = new Board("제목2", "내용2", 2L);
        setField(board2, "user", user2);
        when(boardRepository.findAll()).thenReturn(List.of(board1, board2));

        // when: 전체 조회를 수행합니다.
        List<BoardResponseDto> responseList = boardService.findAll();

        // then: 리스트 크기와 내용을 검증합니다.
        assertEquals(2, responseList.size());
        assertEquals("테스트 유저1", responseList.get(0).getUser().getName());
        assertEquals("테스트 유저2", responseList.get(1).getUser().getName());
        verify(boardRepository, times(1)).findAll();
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
