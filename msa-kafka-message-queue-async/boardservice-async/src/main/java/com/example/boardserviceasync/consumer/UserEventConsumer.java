package com.example.boardserviceasync.consumer;

import com.example.boardserviceasync.domain.User;
import com.example.boardserviceasync.event.UserSignedUpEvent;
import com.example.boardserviceasync.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventConsumer {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserEventConsumer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @KafkaListener(topics = "user.signed-up", groupId = "board-group")
    public void consume(String message) {
        try {
            UserSignedUpEvent event = objectMapper.readValue(message, UserSignedUpEvent.class);
            System.out.println("[BoardService] 유저 동기화 이벤트 수신: " + event.getUserId());
            
            User user = new User(event.getUserId(), event.getName());
            userRepository.save(user);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
