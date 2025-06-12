package com.example.point_coupon_system.dto;

import com.example.point_coupon_system.domain.CouponStatus;
import com.example.point_coupon_system.domain.IssuedCouponDomain;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class IssuedCouponResponseDTO {

    private final String couponName;
    private final LocalDateTime issuedAt;
    private final LocalDateTime expiresAt;
    private final CouponStatus status; // isUsed 필드를 status Enum으로 변경

    public IssuedCouponResponseDTO(IssuedCouponDomain issuedCoupon) {
        this.couponName = issuedCoupon.getCoupon().getCouponName();
        this.issuedAt = issuedCoupon.getIssuedAt();
        this.expiresAt = issuedCoupon.getExpiresAt();
        this.status = issuedCoupon.getStatus(); // isUsed() 대신 getStatus()를 사용
    }
}
