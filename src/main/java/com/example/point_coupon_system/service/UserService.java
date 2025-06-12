package com.example.point_coupon_system.service;

import com.example.point_coupon_system.domain.UserDomain;
import com.example.point_coupon_system.dto.UserSignupRequestDTO;
import com.example.point_coupon_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void signup(UserSignupRequestDTO requestDto) {
        // 이메일 중복 확인
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // DTO를 Domain 객체로 변환하여 저장
        UserDomain user = UserDomain.builder()
                .email(requestDto.getEmail())
                .password(requestDto.getPassword()) // 실제 프로젝트에서는 비밀번호 암호화가 필수입니다.
                .build();

        userRepository.save(user);
    }
}
