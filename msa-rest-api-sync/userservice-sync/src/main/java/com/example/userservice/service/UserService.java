package com.example.userservice.service;

import com.example.userservice.client.PointClient;
import com.example.userservice.domain.User;
import com.example.userservice.dto.AddActivityScoreRequestDto;
import com.example.userservice.dto.SignUpRequestDto;
import com.example.userservice.dto.UserResponseDto;
import com.example.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
  private final UserRepository userRepository;
  private final PointClient pointClient;

  public UserService(UserRepository userRepository, PointClient pointClient) {
    this.userRepository = userRepository;
    this.pointClient = pointClient;
  }

  @Transactional
  public void signUp(SignUpRequestDto signUpRequestDto) {
    User user = new User(
      signUpRequestDto.getEmail(),
      signUpRequestDto.getName(),
      signUpRequestDto.getPassword()
    );

    User savedUser = this.userRepository.save(user);

    // 회원가입하면 포인트 1000점 적립
    pointClient.addPoints(savedUser.getUserId(), 1000);
  }

  @Transactional(readOnly = true)
  public UserResponseDto findById(Long userId) {
    User user = this.userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다. id=" + userId));

    return new UserResponseDto(user.getUserId(), user.getName(), user.getEmail());
  }

  @Transactional(readOnly = true)
  public List<UserResponseDto> findByIds(List<Long> ids) {
    return userRepository.findAllById(ids).stream()
        .map(user -> new UserResponseDto(user.getUserId(), user.getName(), user.getEmail()))
        .collect(Collectors.toList());
  }

  @Transactional
  public void addActivityScore(
      AddActivityScoreRequestDto addActivityScoreRequestDto
  ) {
    User user = userRepository.findById(addActivityScoreRequestDto.getUserId())
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

    user.addActivityScore(addActivityScoreRequestDto.getScore());

    userRepository.save(user);
  }
}
