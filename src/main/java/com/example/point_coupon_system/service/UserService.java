package com.example.point_coupon_system.service;

import com.example.point_coupon_system.domain.IssuedCouponDomain;
import com.example.point_coupon_system.domain.UserDomain;
import com.example.point_coupon_system.dto.IssuedCouponResponseDTO;
import com.example.point_coupon_system.dto.UserSignupRequestDTO;
import com.example.point_coupon_system.repository.IssuedCouponRepository;
import com.example.point_coupon_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final IssuedCouponRepository issuedCouponRepository; // 의존성 추가

    @Transactional
    public void signup(UserSignupRequestDTO requestDto) {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        UserDomain user = UserDomain.builder()
                .email(requestDto.getEmail())
                .password(requestDto.getPassword())
                .build();

        userRepository.save(user);
    }

    /**
     * 사용자가 발급받은 쿠폰 목록을 조회합니다.
     * @param userId 사용자 ID
     * @return List<IssuedCouponResponseDTO>
     */
    @Transactional(readOnly = true)
    public List<IssuedCouponResponseDTO> getIssuedCoupons(Long userId) {
        // 사용자가 존재하는지 먼저 확인
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        List<IssuedCouponDomain> issuedCoupons = issuedCouponRepository.findAllByUserId(userId);

        return issuedCoupons.stream()
                .map(IssuedCouponResponseDTO::new)
                .collect(Collectors.toList());
    }
}
