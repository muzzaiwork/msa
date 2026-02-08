package com.example.boardserviceasync.consumer;

import com.example.boardserviceasync.dto.SaveUserRequestDto;
import com.example.boardserviceasync.event.UserSignedUpEvent;
import com.example.boardserviceasync.service.UserService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserSignedUpEventConsumer {

  private final UserService userService;

  public UserSignedUpEventConsumer(UserService userService) {
    this.userService = userService;
  }

  @KafkaListener(
      topics = "user.signed-up",
      groupId = "board-service"
  )
  public void consume(String message) {
    UserSignedUpEvent userSignedUpEvent
        = UserSignedUpEvent.fromJson(message);

    // 사용자 정보 저장
    SaveUserRequestDto saveUserRequestDto
        = new SaveUserRequestDto(
            userSignedUpEvent.getUserId(),
            userSignedUpEvent.getName()
        );
    userService.save(saveUserRequestDto);
  }
}
