package com.example.point_coupon_system.repository;

import com.example.point_coupon_system.domain.IssuedCouponDomain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssuedCouponRepository extends JpaRepository<IssuedCouponDomain, Long> {
}
