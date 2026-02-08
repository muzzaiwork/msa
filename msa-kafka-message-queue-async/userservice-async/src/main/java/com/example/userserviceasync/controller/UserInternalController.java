package com.example.userserviceasync.controller;

import com.example.userserviceasync.dto.AddActivityScoreRequestDto;
import com.example.userserviceasync.dto.UserResponseDto;
import com.example.userserviceasync.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/users")
public class UserInternalController {
  private final UserService userService;

  public UserInternalController(UserService userService) {
    this.userService = userService;
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

  @PostMapping("/activity-score/add")
  public ResponseEntity<Void> addActivityScore(
      @RequestBody AddActivityScoreRequestDto addActivityScoreRequestDto
  ) {
    userService.addActivityScore(addActivityScoreRequestDto);
    return ResponseEntity.noContent().build();
  }
}
