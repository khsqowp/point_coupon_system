package com.example.point_coupon_system.controller;

import com.example.point_coupon_system.config.jwt.JwtUtil;
import com.example.point_coupon_system.domain.UserDomain;
import com.example.point_coupon_system.dto.IssuedCouponResponseDTO;
import com.example.point_coupon_system.dto.UserLoginRequestDTO;
import com.example.point_coupon_system.dto.UserSignupRequestDTO;
import com.example.point_coupon_system.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
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
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody UserSignupRequestDTO requestDto) {
        userService.signup(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 성공적으로 완료되었습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginRequestDTO requestDto, HttpServletResponse response) {
        try {
            UserDomain user = userService.login(requestDto);
            String token = jwtUtil.createToken(user.getEmail(), user.getRole());
            response.setHeader(JwtUtil.AUTHORIZATION_HEADER, token);
            return ResponseEntity.ok("로그인 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // [수정] 사용자 쿠폰 목록 조회 API 추가
    @GetMapping("/{userId}/coupons")
    public ResponseEntity<List<IssuedCouponResponseDTO>> getIssuedCoupons(@PathVariable Long userId) {
        List<IssuedCouponResponseDTO> coupons = userService.getIssuedCoupons(userId);
        return ResponseEntity.ok(coupons);
    }
}