package com.example.point_coupon_system.scheduler;

import com.example.point_coupon_system.domain.CouponStatus;
import com.example.point_coupon_system.domain.IssuedCouponDomain;
import com.example.point_coupon_system.repository.IssuedCouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponBatchScheduler {

    private final IssuedCouponRepository issuedCouponRepository;

    // 매일 자정에 실행 (cron = "초 분 시 일 월 요일")
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void expireCoupons() {
        log.info("만료 쿠폰 처리 스케줄러 실행");

        // 1. 현재 시간을 기준으로 만료된 쿠폰들을 조회
        List<IssuedCouponDomain> expiredCoupons = issuedCouponRepository.findAllByStatusAndExpiresAtBefore(
                CouponStatus.ACTIVE,
                LocalDateTime.now()
        );

        if (expiredCoupons.isEmpty()) {
            log.info("만료 처리할 쿠폰이 없습니다.");
            return;
        }

        // 2. 조회된 쿠폰들의 상태를 EXPIRED로 변경
        for (IssuedCouponDomain coupon : expiredCoupons) {
            coupon.expire();
        }

        // @Transactional에 의해 메소드 종료 시 변경된 내용이 DB에 반영됨
        log.info("{}개의 쿠폰을 만료 처리했습니다.", expiredCoupons.size());
    }
}
