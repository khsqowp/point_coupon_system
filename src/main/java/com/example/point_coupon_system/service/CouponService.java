package com.example.point_coupon_system.service;

import com.example.point_coupon_system.domain.CouponDomain;
import com.example.point_coupon_system.domain.IssuedCouponDomain;
import com.example.point_coupon_system.domain.UserDomain;
import com.example.point_coupon_system.dto.CouponCreateRequestDTO;
import com.example.point_coupon_system.repository.CouponRepository;
import com.example.point_coupon_system.repository.IssuedCouponRepository;
import com.example.point_coupon_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final IssuedCouponRepository issuedCouponRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String COUPON_QUEUE_KEY_PREFIX = "coupon:queue:";

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
        String queueKey = COUPON_QUEUE_KEY_PREFIX + couponId;
        long timestamp = System.currentTimeMillis();

        Boolean added = redisTemplate.opsForZSet().add(queueKey, userId.toString(), timestamp);

        if (Boolean.FALSE.equals(added)) {
            throw new IllegalStateException("이미 쿠폰 발급을 요청했습니다.");
        }
    }

    @Transactional
    public void issueCoupon(Long couponId, Long userId) {
        // 쿠폰 정책 조회
        CouponDomain coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰입니다."));

        // 사용자 조회
        UserDomain user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 발급 수량 증가 및 재고 확인 (동시성 문제는 다음 스텝에서 해결)
        coupon.increaseIssuedQuantity();

        // 쿠폰 발급 내역 저장
        IssuedCouponDomain issuedCoupon = IssuedCouponDomain.builder()
                .user(user)
                .coupon(coupon)
                .build();

        issuedCouponRepository.save(issuedCoupon);
    }
}
