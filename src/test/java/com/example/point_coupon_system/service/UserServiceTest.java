package com.example.point_coupon_system.service;

import com.example.point_coupon_system.domain.CouponDomain;
import com.example.point_coupon_system.domain.IssuedCouponDomain;
import com.example.point_coupon_system.domain.UserDomain;
import com.example.point_coupon_system.dto.IssuedCouponResponseDTO;
import com.example.point_coupon_system.dto.UserSignupRequestDTO;
import com.example.point_coupon_system.repository.IssuedCouponRepository;
import com.example.point_coupon_system.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
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

    @Mock
    private IssuedCouponRepository issuedCouponRepository; // 의존성 Mock 추가

    @InjectMocks
    private UserService userService;

    // ... 기존 회원가입 테스트 ...

    @Test
    @DisplayName("사용자 쿠폰 목록 조회 성공 테스트")
    void getIssuedCoupons_success() {
        // given
        Long userId = 1L;
        UserDomain user = UserDomain.builder().build();
        CouponDomain coupon = CouponDomain.builder().couponName("테스트 쿠폰").build();
        IssuedCouponDomain issuedCoupon = IssuedCouponDomain.builder().user(user).coupon(coupon).build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(issuedCouponRepository.findAllByUserId(userId)).willReturn(Collections.singletonList(issuedCoupon));

        // when
        List<IssuedCouponResponseDTO> result = userService.getIssuedCoupons(userId);

        // then
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("테스트 쿠폰", result.get(0).getCouponName());
    }

    @Test
    @DisplayName("사용자 쿠폰 목록 조회 실패 - 존재하지 않는 사용자")
    void getIssuedCoupons_fail_userNotFound() {
        // given
        Long userId = 999L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> userService.getIssuedCoupons(userId));
    }
}
