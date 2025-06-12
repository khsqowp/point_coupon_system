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

    /**
     * 발급 수량을 1 증가시킵니다.
     * 재고가 모두 소진되었다면 예외를 발생시킵니다.
     */
    public void increaseIssuedQuantity() {
        if (this.issuedQuantity >= this.totalQuantity) {
            throw new IllegalStateException("모든 쿠폰이 소진되었습니다.");
        }
        this.issuedQuantity++;
    }
}
