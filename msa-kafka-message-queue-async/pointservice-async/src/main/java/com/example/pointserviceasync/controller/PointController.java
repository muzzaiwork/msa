package com.example.pointserviceasync.controller;

import com.example.pointserviceasync.dto.AddPointRequestDto;
import com.example.pointserviceasync.dto.DeductPointRequestDto;
import com.example.pointserviceasync.service.PointService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/points")
public class PointController {
  private final PointService pointService;

  public PointController(PointService pointService) {
    this.pointService = pointService;
  }
  
  @GetMapping("{userId}")
  public ResponseEntity<Integer> getPoint(@PathVariable Long userId) {
    return ResponseEntity.ok(pointService.getPoint(userId));
  }
  
  @PostMapping("add")
  public ResponseEntity<Void> addPoints(
      @RequestBody AddPointRequestDto addPointRequestDto
  ) {
    pointService.addPoints(addPointRequestDto);
    return ResponseEntity.noContent().build();
  }
  
  @PostMapping("deduct") // deduct : 빼다
  public ResponseEntity<Void> deductPoints(
      @RequestBody DeductPointRequestDto deductPointRequestDto
  ) {
    pointService.deductPoints(deductPointRequestDto);
    return ResponseEntity.noContent().build();
  }
}
