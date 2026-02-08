package com.example.userserviceasync.controller;

import com.example.userserviceasync.dto.LoginRequestDto;
import com.example.userserviceasync.dto.LoginResponseDto;
import com.example.userserviceasync.dto.SignUpRequestDto;
import com.example.userserviceasync.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/api/users/login")
  public ResponseEntity<LoginResponseDto> login(
      @RequestBody LoginRequestDto loginRequestDto
  ) {
    return ResponseEntity.ok(userService.login(loginRequestDto));
  }

  @PostMapping("/api/users/sign-up")
  public ResponseEntity<Void> signUp(
      @RequestBody SignUpRequestDto signUpRequestDto
  ) {
    userService.signUp(signUpRequestDto);
    return ResponseEntity.noContent().build();
  }
}
