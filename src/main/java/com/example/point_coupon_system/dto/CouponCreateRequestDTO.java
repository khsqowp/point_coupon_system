package com.example.point_coupon_system.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CouponCreateRequestDTO {

    @NotBlank(message = "쿠폰 이름은 비워둘 수 없습니다.")
    private String couponName;

    @NotNull(message = "총 수량은 비워둘 수 없습니다.")
    @Min(value = 1, message = "총 수량은 1개 이상이어야 합니다.")
    private Long totalQuantity;

    @NotNull(message = "유효기간은 비워둘 수 없습니다.")
    @Min(value = 1, message = "유효기간은 1일 이상이어야 합니다.")
    private int validityPeriod;

}
