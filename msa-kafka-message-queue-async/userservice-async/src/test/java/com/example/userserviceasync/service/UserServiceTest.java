package com.example.userserviceasync.service;

import com.example.userserviceasync.client.PointClient;
import com.example.userserviceasync.domain.User;
import com.example.userserviceasync.dto.SignUpRequestDto;
import com.example.userserviceasync.dto.UserResponseDto;
import com.example.userserviceasync.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * @ExtendWith(MockitoExtension.class): Mockito 라이브러리를 사용하기 위한 어노테이션입니다.
 * Spring Context를 로드하지 않고 순수하게 객체의 관계만 Mock으로 설정하여 매우 빠릅니다.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    /**
     * @Mock: 가짜 객체를 생성합니다.
     * UserRepository의 실제 데이터베이스 연동 없이도 동작을 정의할 수 있습니다.
     */
    @Mock
    private UserRepository userRepository;

    @Mock
    private PointClient pointClient;

    /**
     * @InjectMocks: 위에서 생성한 @Mock 가짜 객체들을 해당 서비스 객체에 자동으로 주입해줍니다.
     * 즉, userService가 사용하는 userRepository를 가짜(Mock) 객체로 갈아끼워줍니다.
     */
    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("회원가입 성공 테스트")
    void signUpSuccess() throws Exception {
        // given: 테스트를 위한 데이터를 준비합니다.
        SignUpRequestDto requestDto = new SignUpRequestDto();
        setField(requestDto, "email", "test@example.com");
        setField(requestDto, "name", "Tester");
        setField(requestDto, "password", "password123");

        User savedUser = new User("test@example.com", "Tester", "password123");
        setField(savedUser, "userId", 1L);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // when: 실제 기능을 실행합니다.
        userService.signUp(requestDto);

        /**
         * then: 실행 결과를 검증합니다.
         * verify(userRepository, times(1)).save(any(User.class)):
         * userRepository의 save() 메서드가 최소 1번(times(1)) 호출되었는지 확인합니다.
         * 실제 DB에 저장되지는 않지만, 로직 흐름상 저장을 시도했는지를 가짜 객체(Mock)를 통해 검증하는 것입니다.
         */
        verify(userRepository, times(1)).save(any(User.class));
        verify(pointClient, times(1)).addPoints(eq(1L), eq(1000));
    }

    @Test
    @DisplayName("유저 단건 조회 테스트")
    void findById() throws Exception {
        // given: 가짜 유저 데이터를 준비합니다.
        Long userId = 1L;
        User user = new User("test@example.com", "Tester", "password123");
        setField(user, "userId", userId);

        /**
         * when(...).thenReturn(...): 가짜 객체(Mock)의 동작을 정의합니다.
         * userRepository.findById(userId)가 호출되면 미리 준비한 user 객체를 담은 Optional을 반환하도록 설정(Stubbing)합니다.
         */
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when: 서비스의 조회 메서드를 실행합니다.
        UserResponseDto response = userService.findById(userId);

        // then: 반환된 데이터가 예상과 일치하는지 검증합니다.
        assertEquals(userId, response.getUserId());
        assertEquals("Tester", response.getName());
        assertEquals("test@example.com", response.getEmail());

        // userRepository.findById()가 호출되었는지 검증합니다.
        verify(userRepository, times(1)).findById(userId);
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
