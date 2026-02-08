package com.example.userserviceasync.controller;

import com.example.userserviceasync.dto.UserResponseDto;
import com.example.userserviceasync.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserInternalController.class)
class UserInternalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("유저 조회 API 테스트")
    void findById() throws Exception {
        // given
        Long userId = 1L;
        UserResponseDto responseDto = new UserResponseDto(userId, "Tester", "test@example.com");

        when(userService.findById(userId)).thenReturn(responseDto);

        // when & then
        mockMvc.perform(get("/internal/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.name").value("Tester"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }
}
