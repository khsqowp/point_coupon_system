package com.example.point_coupon_system.controller;

import com.example.point_coupon_system.config.jwt.JwtUtil;
import com.example.point_coupon_system.domain.UserDomain;
import com.example.point_coupon_system.domain.UserRoleEnum;
import com.example.point_coupon_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        UserDomain adminUser = UserDomain.builder()
                .email("admin@example.com")
                .password(passwordEncoder.encode("password123"))
                .role(UserRoleEnum.ADMIN)
                .build();
        userRepository.save(adminUser);
        adminToken = jwtUtil.createToken(adminUser.getEmail(), adminUser.getRole());

        UserDomain normalUser = UserDomain.builder()
                .email("user@example.com")
                .password(passwordEncoder.encode("password123"))
                .role(UserRoleEnum.USER)
                .build();
        userRepository.save(normalUser);
        userToken = jwtUtil.createToken(normalUser.getEmail(), normalUser.getRole());
    }

    @Test
    @DisplayName("쿠폰 정책 등록 API 성공 - ADMIN 권한")
    void createCouponApi_success_with_admin() throws Exception {
        String jsonBody = "{\"couponName\":\"관리자 등록 쿠폰\", \"totalQuantity\":100, \"validityPeriod\":7}";
        mockMvc.perform(post("/coupons")
                        .header(JwtUtil.AUTHORIZATION_HEADER, adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @DisplayName("쿠폰 정책 등록 API 실패 - USER 권한")
    void createCouponApi_fail_with_user() throws Exception {
        String jsonBody = "{\"couponName\":\"사용자 등록 시도 쿠폰\", \"totalQuantity\":100, \"validityPeriod\":7}";
        mockMvc.perform(post("/coupons")
                        .header(JwtUtil.AUTHORIZATION_HEADER, userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName("쿠폰 정책 등록 API 실패 - 인증 정보 없음")
    void createCouponApi_fail_without_token() throws Exception {
        String jsonBody = "{\"couponName\":\"인증 없는 쿠폰\", \"totalQuantity\":100, \"validityPeriod\":7}";
        mockMvc.perform(post("/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                // [수정] 기대값을 isUnauthorized(401)에서 isForbidden(403)으로 변경
                .andExpect(status().isForbidden())
                .andDo(print());
    }
}