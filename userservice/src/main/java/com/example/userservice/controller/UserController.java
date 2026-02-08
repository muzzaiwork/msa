package com.example.userservice.controller;

import com.example.userservice.dto.AddActivityScoreRequestDto;
import com.example.userservice.dto.SignUpRequestDto;
import com.example.userservice.dto.UserResponseDto;
import com.example.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("sign-up")
  public ResponseEntity<Void> signUp(
      @RequestBody SignUpRequestDto signUpRequestDto
  ) {
    userService.signUp(signUpRequestDto);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{userId}")
  public ResponseEntity<UserResponseDto> findById(
      @PathVariable Long userId
  ) {
    return ResponseEntity.ok(userService.findById(userId));
  }

  @GetMapping
  public ResponseEntity<List<UserResponseDto>> findByIds(
      @RequestParam List<Long> ids
  ) {
    return ResponseEntity.ok(userService.findByIds(ids));
  }

  @PostMapping("activity-score/add")
  public ResponseEntity<Void> addActivityScore(
      @RequestBody AddActivityScoreRequestDto addActivityScoreRequestDto
  ) {
    userService.addActivityScore(addActivityScoreRequestDto);
    return ResponseEntity.noContent().build();
  }
}
