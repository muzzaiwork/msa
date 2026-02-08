package com.example.pointserviceasync.consumer;

import com.example.pointserviceasync.dto.DeductPointRequestDto;
import com.example.pointserviceasync.event.BoardCreatedEvent;
import com.example.pointserviceasync.service.PointService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BoardEventConsumer {

    private final PointService pointService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BoardEventConsumer(PointService pointService) {
        this.pointService = pointService;
    }

    @KafkaListener(topics = "board.created", groupId = "point-group")
    public void consume(String message) {
        try {
            BoardCreatedEvent event = objectMapper.readValue(message, BoardCreatedEvent.class);
            System.out.println("[PointService] 게시글 생성 이벤트 수신: " + event.getUserId());
            
            // 게시글 생성 시 100 포인트 차감
            pointService.deductPoints(new DeductPointRequestDto(event.getUserId(), 100));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
