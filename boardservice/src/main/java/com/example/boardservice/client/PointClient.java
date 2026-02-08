package com.example.boardservice.client;

import com.example.boardservice.dto.AddPointsRequestDto;
import com.example.boardservice.dto.DeductPointsRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PointClient {

  private final RestClient restClient;

  public PointClient(
      @Value("${client.point-service.url}") String pointServiceUrl
  ) {
    this.restClient = RestClient.builder()
        .baseUrl(pointServiceUrl)
        .build();
  }

  /**
   * 포인트 적립 API 호출
   * SAGA 패턴에서 보상 트랜잭션(차감된 포인트 복구) 용도로도 사용됩니다.
   */
  public void addPoints(Long userId, int amount) {
    AddPointsRequestDto addPointsRequestDto
        = new AddPointsRequestDto(userId, amount);
    this.restClient.post()
        .uri("/points/add")
        .contentType(MediaType.APPLICATION_JSON)
        .body(addPointsRequestDto)
        .retrieve()
        .toBodilessEntity();
  }

  /**
   * 포인트 차감 API 호출
   */
  public void deductPoints(Long userId, int amount) {
    DeductPointsRequestDto deductPointsRequestDto
        = new DeductPointsRequestDto(userId, amount);
    this.restClient.post()
        .uri("/points/deduct")
        .contentType(MediaType.APPLICATION_JSON)
        .body(deductPointsRequestDto)
        .retrieve()
        .toBodilessEntity();
  }
}
