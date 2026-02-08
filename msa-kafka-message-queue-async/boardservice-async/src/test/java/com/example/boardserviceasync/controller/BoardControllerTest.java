package com.example.boardserviceasync.controller;

import com.example.boardserviceasync.dto.BoardResponseDto;
import com.example.boardserviceasync.dto.CreateBoardRequestDto;
import com.example.boardserviceasync.dto.UserDto;
import com.example.boardserviceasync.service.BoardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @WebMvcTest: MVC 테스트를 위한 어노테이션입니다.
 * BoardController만 로드하여 테스트하므로 가볍고 빠릅니다.
 */
@WebMvcTest(BoardController.class)
class BoardControllerTest {

    /**
     * MockMvc: 웹 API를 서버에 배포하지 않고도 테스트할 수 있게 해주는 가짜 객체입니다.
     * 컨트롤러의 엔드포인트(URL) 호출을 시뮬레이션합니다.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * @MockitoBean: 스프링 컨텍스트에 Mock(가짜) 객체를 등록합니다.
     * 실제 BoardService 대신 가짜 BoardService를 사용하여 컨트롤러만 독립적으로 테스트합니다.
     */
    @MockitoBean
    private BoardService boardService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("게시글 생성 API 테스트")
    void createBoard() throws Exception {
        // given: 테스트를 준비하는 단계
        CreateBoardRequestDto requestDto = new CreateBoardRequestDto();
        setField(requestDto, "title", "테스트 제목");
        setField(requestDto, "content", "테스트 내용");
        setField(requestDto, "userId", 1L);

        /**
         * doNothing().when(boardService).create(...):
         * boardService.create() 메서드가 호출될 때 아무런 동작도 하지 않도록 설정(Stubbing)합니다.
         * 실제 서비스 로직을 실행하지 않고, 호출되었다는 사실만 확인하거나 에러가 발생하지 않음을 보장합니다.
         */
        doNothing().when(boardService).create(any(CreateBoardRequestDto.class));

        // when & then: 테스트를 실행하고 검증하는 단계
        mockMvc.perform(post("/api/boards") // /api/boards 경로로 POST 요청을 보냄
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))) // JSON 데이터 포함
                .andExpect(status().isNoContent()); // 응답 상태 코드가 204 No Content인지 검증
    }

    @Test
    @DisplayName("게시글 단건 조회 API 테스트")
    void getBoard() throws Exception {
        // given: 조회될 데이터를 준비합니다.
        UserDto userDto = new UserDto(1L, "임시 유저");
        BoardResponseDto responseDto = new BoardResponseDto(1L, "제목", "내용", userDto);
        when(boardService.findById(anyLong())).thenReturn(responseDto);

        // when & then: GET 요청을 보내고 결과를 검증합니다.
        mockMvc.perform(get("/api/boards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.content").value("내용"))
                .andExpect(jsonPath("$.user.userId").value(1L))
                .andExpect(jsonPath("$.user.name").value("임시 유저"));
    }

    @Test
    @DisplayName("게시글 전체 조회 API 테스트")
    void getAllBoards() throws Exception {
        // given: 조회될 데이터 리스트를 준비합니다.
        UserDto userDto = new UserDto(1L, "임시 유저");
        BoardResponseDto responseDto1 = new BoardResponseDto(1L, "제목1", "내용1", userDto);
        BoardResponseDto responseDto2 = new BoardResponseDto(2L, "제목2", "내용2", userDto);
        when(boardService.findAll()).thenReturn(List.of(responseDto1, responseDto2));

        // when & then: GET 요청을 보내고 리스트 크기를 검증합니다.
        mockMvc.perform(get("/api/boards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].user.name").value("임시 유저"));
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
