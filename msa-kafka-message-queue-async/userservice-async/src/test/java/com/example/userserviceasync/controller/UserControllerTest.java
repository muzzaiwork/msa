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
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 API 테스트")
    void signUp() throws Exception {
        // given
        SignUpRequestDto requestDto = new SignUpRequestDto();
        setField(requestDto, "email", "test@example.com");
        setField(requestDto, "name", "Tester");
        setField(requestDto, "password", "password123");

        doNothing().when(userService).signUp(any(SignUpRequestDto.class));

        // when & then
        mockMvc.perform(post("/api/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNoContent());
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
