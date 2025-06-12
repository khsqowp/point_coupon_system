package com.example.point_coupon_system.service;

import com.example.point_coupon_system.domain.CouponDomain;
import com.example.point_coupon_system.domain.IssuedCouponDomain;
import com.example.point_coupon_system.domain.UserDomain;
import com.example.point_coupon_system.dto.CouponCreateRequestDTO;
import com.example.point_coupon_system.repository.CouponRepository;
import com.example.point_coupon_system.repository.IssuedCouponRepository;
import com.example.point_coupon_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException; // 정확한 import로 수정
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final IssuedCouponRepository issuedCouponRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public void createCoupon(CouponCreateRequestDTO requestDTO) {
        CouponDomain couponDomain = CouponDomain.builder()
                .couponName(requestDTO.getCouponName())
                .totalQuantity(requestDTO.getTotalQuantity())
                .validityPeriod(requestDTO.getValidityPeriod())
                .build();
        couponRepository.save(couponDomain);
    }

    public void applyForCoupon(Long couponId, Long userId) {
        String queueKey = "coupon:queue:" + couponId;
        long timestamp = System.currentTimeMillis();
        Boolean added = redisTemplate.opsForZSet().add(queueKey, String.valueOf(userId), timestamp);
        if (Boolean.FALSE.equals(added)) {
            throw new IllegalStateException("이미 쿠폰 발급을 요청했습니다.");
        }
    }

    @Retryable(
            value = {ObjectOptimisticLockingFailureException.class}, // 이 예외 발생 시 재시도
            maxAttempts = 5, // 최대 5번 재시도
            backoff = @Backoff(delay = 100) // 100ms 간격으로 재시도
    )
    @Transactional
    public void issueCoupon(Long couponId, Long userId) {
        // 1. 쿠폰 재고 확인 (버전 정보 포함하여 조회)
        CouponDomain coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰입니다."));

        if (coupon.getIssuedQuantity() >= coupon.getTotalQuantity()) {
            return; // 재고가 없으면 종료
        }

        // 2. 사용자 조회
        UserDomain user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 3. 쿠폰 발급 수량 증가
        coupon.increaseIssuedQuantity();

        // 4. 발급 내역 저장
        IssuedCouponDomain issuedCoupon = IssuedCouponDomain.builder()
                .user(user)
                .coupon(coupon)
                .build();
        issuedCouponRepository.save(issuedCoupon);

        // 트랜잭션 커밋 시점에 버전이 일치하지 않으면 ObjectOptimisticLockingFailureException 발생
    }
}
