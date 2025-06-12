package com.example.point_coupon_system.dto;

import com.example.point_coupon_system.domain.IssuedCouponDomain;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class IssuedCouponResponseDTO {
    private final String couponName;
    private final LocalDateTime issuedAt;
    private final LocalDateTime expiresAt;
    private final boolean isUsed;

    public IssuedCouponResponseDTO(IssuedCouponDomain issuedCoupon) {
        this.couponName = issuedCoupon.getCoupon().getCouponName();
        this.issuedAt = issuedCoupon.getIssuedAt();
        this.expiresAt = issuedCoupon.getExpiresAt();
        this.isUsed = issuedCoupon.isUsed();
    }
}
