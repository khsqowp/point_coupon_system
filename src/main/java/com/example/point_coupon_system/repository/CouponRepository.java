package com.example.point_coupon_system.repository;

import com.example.point_coupon_system.domain.CouponDomain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<CouponDomain, Long> {
    // 비관적 락 메소드 제거
}
