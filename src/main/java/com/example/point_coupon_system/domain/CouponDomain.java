package com.example.point_coupon_system.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "coupons")
public class CouponDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version // 낙관적 락을 위한 버전 필드
    private Long version;

    @Column(nullable = false)
    private String couponName;

    @Column(nullable = false)
    private Long totalQuantity;

    @Column(nullable = false)
    private Long issuedQuantity;

    @Column(nullable = false)
    private int validityPeriod;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public CouponDomain(String couponName, Long totalQuantity, int validityPeriod) {
        this.couponName = couponName;
        this.totalQuantity = totalQuantity;
        this.issuedQuantity = 0L;
        this.validityPeriod = validityPeriod;
        this.createdAt = LocalDateTime.now();
    }

    public void increaseIssuedQuantity() {
        if (this.issuedQuantity >= this.totalQuantity) {
            throw new IllegalStateException("모든 쿠폰이 소진되었습니다.");
        }
        this.issuedQuantity++;
    }
}

