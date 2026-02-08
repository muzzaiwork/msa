package com.example.userserviceasync.controller;

import com.example.userserviceasync.dto.SignUpRequestDto;
import com.example.userserviceasync.dto.UserResponseDto;
import com.example.userserviceasync.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @WebMvcTest: MVC 테스트를 위한 어노테이션입니다.
 * UserController만 로드하여 테스트하므로 가볍고 빠릅니다.
 */
@WebMvcTest(UserController.class)
class UserControllerTest {

    /**
     * MockMvc: 웹 API를 서버에 배포하지 않고도 테스트할 수 있게 해주는 가짜 객체입니다.
     * 컨트롤러의 엔드포인트(URL) 호출을 시뮬레이션합니다.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * @MockitoBean: 스프링 컨텍스트에 Mock(가짜) 객체를 등록합니다.
     * 실제 UserService 대신 가짜 UserService를 사용하여 컨트롤러만 독립적으로 테스트합니다.
     */
    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 API 테스트")
    void signUp() throws Exception {
        // given: 테스트를 준비하는 단계
        SignUpRequestDto requestDto = new SignUpRequestDto();
        setField(requestDto, "email", "test@example.com");
        setField(requestDto, "name", "Tester");
        setField(requestDto, "password", "password123");

        /**
         * doNothing().when(userService).signUp(...):
         * userService.signUp() 메서드가 호출될 때 아무런 동작도 하지 않도록 설정(Stubbing)합니다.
         * 실제 서비스 로직을 실행하지 않고, 호출되었다는 사실만 확인하거나 에러가 발생하지 않음을 보장합니다.
         */
        doNothing().when(userService).signUp(any(SignUpRequestDto.class));

        // when & then: 테스트를 실행하고 검증하는 단계
        mockMvc.perform(post("/users/sign-up") // /users/sign-up 경로로 POST 요청을 보냄
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))) // JSON 데이터 포함
                .andExpect(status().isNoContent()); // 응답 상태 코드가 204 No Content인지 검증
    }

    @Test
    @DisplayName("유저 조회 API 테스트")
    void findById() throws Exception {
        // given: 테스트 데이터를 준비합니다.
        Long userId = 1L;
        UserResponseDto responseDto = new UserResponseDto(userId, "Tester", "test@example.com");

        /**
         * when(...).thenReturn(...):
         * userService.findById(userId)가 호출되면 responseDto를 반환하도록 설정합니다.
         */
        when(userService.findById(userId)).thenReturn(responseDto);

        // when & then: GET 요청을 보내고 결과를 검증합니다.
        mockMvc.perform(get("/users/{userId}", userId)) // /users/1 경로로 GET 요청
                .andExpect(status().isOk()) // 200 OK 응답 확인
                .andExpect(jsonPath("$.userId").value(userId)) // JSON의 userId 필드 확인
                .andExpect(jsonPath("$.name").value("Tester")) // name 필드 확인
                .andExpect(jsonPath("$.email").value("test@example.com")); // email 필드 확인
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
