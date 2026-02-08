package com.example.boardserviceasync.client;

import com.example.boardserviceasync.dto.AddActivityScoreRequestDto;
import com.example.boardserviceasync.dto.UserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserClient {
    private final RestClient restClient;
    private final String userServiceUrl;

    public UserClient(RestClient restClient, @Value("${user-service.url}") String userServiceUrl) {
        this.restClient = restClient;
        this.userServiceUrl = userServiceUrl;
    }

    public Optional<UserDto> getUser(Long userId) {
        try {
            UserDto userDto = restClient.get()
                    .uri(userServiceUrl + "/users/{userId}", userId)
                    .retrieve()
                    .body(UserDto.class);
            return Optional.ofNullable(userDto);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<UserDto> getUsers(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            String idsParam = userIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

            UserDto[] users = restClient.get()
                    .uri(userServiceUrl + "/users?ids={ids}", idsParam)
                    .retrieve()
                    .body(UserDto[].class);
            return users != null ? List.of(users) : Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * 활동 점수 적립 API 호출
     */
    public void addActivityScore(Long userId, int score) {
        AddActivityScoreRequestDto addActivityScoreRequestDto
                = new AddActivityScoreRequestDto(userId, score);
        this.restClient.post()
                .uri(userServiceUrl + "/users/activity-score/add")
                .contentType(MediaType.APPLICATION_JSON)
                .body(addActivityScoreRequestDto)
                .retrieve()
                .toBodilessEntity();
    }
}
