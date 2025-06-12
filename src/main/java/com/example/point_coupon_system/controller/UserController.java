package com.example.point_coupon_system.controller;

import com.example.point_coupon_system.dto.IssuedCouponResponseDTO;
import com.example.point_coupon_system.dto.UserSignupRequestDTO;
import com.example.point_coupon_system.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody UserSignupRequestDTO requestDto) {
        userService.signup(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 성공적으로 완료되었습니다.");
    }

    @GetMapping("/{userId}/coupons")
    public ResponseEntity<?> getUserCoupons(@PathVariable Long userId) {
        try {
            List<IssuedCouponResponseDTO> coupons = userService.getIssuedCoupons(userId);
            return ResponseEntity.ok(coupons);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
