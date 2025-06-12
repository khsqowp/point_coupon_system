package com.example.point_coupon_system.service;

import com.example.point_coupon_system.domain.UserDomain;
import com.example.point_coupon_system.dto.UserSignupRequestDTO;
import com.example.point_coupon_system.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("회원가입 성공 테스트")
    void signup_success() {
        // given: 이러한 데이터가 주어지고
        UserSignupRequestDTO requestDto = new UserSignupRequestDTO();
        ReflectionTestUtils.setField(requestDto, "email", "test@example.com");
        ReflectionTestUtils.setField(requestDto, "password", "password123");

        // userRepository.findByEmail이 호출될 때 Optional.empty()를 반환하도록 설정 (중복 없음)
        given(userRepository.findByEmail(requestDto.getEmail())).willReturn(Optional.empty());

        // when: signup 메소드를 실행하면
        assertDoesNotThrow(() -> userService.signup(requestDto));

        // then: userRepository.save가 1번 호출되어야 한다
        verify(userRepository, times(1)).save(any(UserDomain.class));
    }

    @Test
    @DisplayName("회원가입 실패 테스트 - 이메일 중복")
    void signup_fail_with_duplicate_email() {
        // given: 이러한 데이터가 주어지고
        UserSignupRequestDTO requestDto = new UserSignupRequestDTO();
        ReflectionTestUtils.setField(requestDto, "email", "test@example.com");
        ReflectionTestUtils.setField(requestDto, "password", "password123");

        UserDomain existingUser = UserDomain.builder().build();

        // userRepository.findByEmail이 호출될 때 이미 존재하는 UserDomain 객체를 반환하도록 설정 (중복 있음)
        given(userRepository.findByEmail(requestDto.getEmail())).willReturn(Optional.of(existingUser));

        // when & then: signup 메소드를 실행하면 IllegalArgumentException이 발생해야 한다
        assertThrows(IllegalArgumentException.class, () -> userService.signup(requestDto));

        // then: userRepository.save가 호출되지 않아야 한다
        verify(userRepository, times(0)).save(any(UserDomain.class));
    }
}
