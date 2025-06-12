package com.example.point_coupon_system.scheduler;

import com.example.point_coupon_system.domain.CouponDomain;
import com.example.point_coupon_system.domain.CouponStatus;
import com.example.point_coupon_system.domain.IssuedCouponDomain;
import com.example.point_coupon_system.domain.UserDomain;
import com.example.point_coupon_system.repository.CouponRepository;
import com.example.point_coupon_system.repository.IssuedCouponRepository;
import com.example.point_coupon_system.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class CouponBatchSchedulerTest {

    @Autowired
    private CouponBatchScheduler couponBatchScheduler;

    @Autowired
    private IssuedCouponRepository issuedCouponRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    @AfterEach
    void tearDown() {
        issuedCouponRepository.deleteAllInBatch();
        couponRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @Transactional
    @DisplayName("만료된 쿠폰 처리 스케줄러 성공 테스트")
    void expireCoupons_scheduler_test() {
        // given
        UserDomain user = userRepository.save(UserDomain.builder().email("scheduler@test.com").password("password").build());
        CouponDomain couponPolicy = couponRepository.save(CouponDomain.builder().couponName("스케줄러 테스트 쿠폰").totalQuantity(10L).validityPeriod(30).build());

        // 1. 이미 만료된 쿠폰 생성
        IssuedCouponDomain expiredCoupon = IssuedCouponDomain.builder().user(user).coupon(couponPolicy).build();
        ReflectionTestUtils.setField(expiredCoupon, "expiresAt", LocalDateTime.now().minusDays(1)); // 만료일을 어제로 설정
        issuedCouponRepository.save(expiredCoupon);

        // 2. 아직 유효한 쿠폰 생성
        IssuedCouponDomain activeCoupon = IssuedCouponDomain.builder().user(user).coupon(couponPolicy).build();
        ReflectionTestUtils.setField(activeCoupon, "expiresAt", LocalDateTime.now().plusDays(1)); // 만료일을 내일로 설정
        issuedCouponRepository.save(activeCoupon);

        // when
        couponBatchScheduler.expireCoupons();

        // then
        // ID로 각 쿠폰의 최신 상태를 다시 조회
        IssuedCouponDomain foundExpiredCoupon = issuedCouponRepository.findById(expiredCoupon.getId()).orElseThrow();
        IssuedCouponDomain foundActiveCoupon = issuedCouponRepository.findById(activeCoupon.getId()).orElseThrow();

        // 만료된 쿠폰의 상태가 EXPIRED로 변경되었는지 확인
        assertEquals(CouponStatus.EXPIRED, foundExpiredCoupon.getStatus());

        // 유효한 쿠폰의 상태가 그대로 ACTIVE인지 확인
        assertEquals(CouponStatus.ACTIVE, foundActiveCoupon.getStatus());
    }
}
