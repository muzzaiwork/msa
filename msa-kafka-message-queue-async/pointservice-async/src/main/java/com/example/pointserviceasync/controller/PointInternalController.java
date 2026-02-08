package com.example.pointserviceasync.controller;

import com.example.pointserviceasync.dto.AddPointRequestDto;
import com.example.pointserviceasync.dto.DeductPointRequestDto;
import com.example.pointserviceasync.service.PointService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PointInternalController {
  private final PointService pointService;

  public PointInternalController(PointService pointService) {
    this.pointService = pointService;
  }
  
  @GetMapping("/internal/points/{userId}")
  public ResponseEntity<Integer> getPoint(@PathVariable Long userId) {
    return ResponseEntity.ok(pointService.getPoint(userId));
  }
  
  @PostMapping("/internal/points/add")
  public ResponseEntity<Void> addPoints(
      @RequestBody AddPointRequestDto addPointRequestDto
  ) {
    pointService.addPoints(addPointRequestDto);
    return ResponseEntity.noContent().build();
  }
  
  @PostMapping("/internal/points/deduct") // deduct : 빼다
  public ResponseEntity<Void> deductPoints(
      @RequestBody DeductPointRequestDto deductPointRequestDto
  ) {
    pointService.deductPoints(deductPointRequestDto);
    return ResponseEntity.noContent().build();
  }
}
