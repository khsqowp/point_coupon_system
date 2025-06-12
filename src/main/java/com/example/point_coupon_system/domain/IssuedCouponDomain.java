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
    private LocalDateTime issuedAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING) // Enum 타입을 문자열로 저장
    @Column(nullable = false)
    private CouponStatus status; // isUsed 필드를 status로 변경

    @Builder
    public IssuedCouponDomain(UserDomain user, CouponDomain coupon) {
        this.user = user;
        this.coupon = coupon;
        this.issuedAt = LocalDateTime.now();
        this.expiresAt = this.issuedAt.plusDays(coupon.getValidityPeriod());
        this.status = CouponStatus.ACTIVE; // 초기 상태는 ACTIVE
    }

    // 쿠폰 상태를 만료로 변경하는 메소드
    public void expire() {
        this.status = CouponStatus.EXPIRED;
    }
}
