package com.example.point_coupon_system.controller;

import com.example.point_coupon_system.dto.CouponCreateRequestDTO;
import com.example.point_coupon_system.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/coupons")
public class CouponController {

    private final CouponService couponService;

    // 관리자: 쿠폰 정책 등록
    @PostMapping
    public ResponseEntity<String> createCoupon(@Valid @RequestBody CouponCreateRequestDTO requestDTO) {
        couponService.createCoupon(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("쿠폰 정책이 성공적으로 등록되었습니다.");
    }

    // 사용자: 쿠폰 발급 요청 (대기열 추가)
    @PostMapping("/{couponId}/apply")
    public ResponseEntity<String> applyForCoupon(@PathVariable Long couponId, @RequestParam Long userId) {
        try {
            couponService.applyForCoupon(couponId, userId);
            return ResponseEntity.ok("쿠폰 발급 요청이 성공적으로 접수되었습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
