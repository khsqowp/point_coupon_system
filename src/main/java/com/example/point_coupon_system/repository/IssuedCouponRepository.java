package com.example.point_coupon_system.repository;

import com.example.point_coupon_system.domain.CouponStatus;
import com.example.point_coupon_system.domain.IssuedCouponDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface IssuedCouponRepository extends JpaRepository<IssuedCouponDomain, Long> {

    List<IssuedCouponDomain> findAllByUserId(Long userId);

    /**
     * 특정 시간 이전에 만료되는 활성 상태의 쿠폰들을 조회합니다.
     * @param status 쿠폰 상태 (ACTIVE)
     * @param now 현재 시간
     * @return List<IssuedCouponDomain>
     */
    List<IssuedCouponDomain> findAllByStatusAndExpiresAtBefore(CouponStatus status, LocalDateTime now);
}
