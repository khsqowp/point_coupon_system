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
@Table(name = "issued_coupons")
public class IssuedCouponDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserDomain user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private CouponDomain coupon;

    @Column(nullable = false)
    private LocalDateTime issuedAt; // 발급 일시

    @Column(nullable = false)
    private LocalDateTime expiresAt; // 만료 일시

    @Column(nullable = false)
    private boolean isUsed; // 사용 여부

    @Builder
    public IssuedCouponDomain(UserDomain user, CouponDomain coupon) {
        this.user = user;
        this.coupon = coupon;
        this.issuedAt = LocalDateTime.now();
        this.expiresAt = this.issuedAt.plusDays(coupon.getValidityPeriod());
        this.isUsed = false;
    }
}
