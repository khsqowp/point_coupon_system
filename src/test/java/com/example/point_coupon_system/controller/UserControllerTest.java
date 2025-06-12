package com.example.point_coupon_system.controller;

import com.example.point_coupon_system.domain.CouponDomain;
import com.example.point_coupon_system.domain.IssuedCouponDomain;
import com.example.point_coupon_system.domain.UserDomain;
import com.example.point_coupon_system.dto.UserSignupRequestDTO;
import com.example.point_coupon_system.repository.CouponRepository;
import com.example.point_coupon_system.repository.IssuedCouponRepository;
import com.example.point_coupon_system.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private IssuedCouponRepository issuedCouponRepository;

    // ... 기존 회원가입 API 테스트 ...

    @Test
    @DisplayName("사용자 쿠폰 목록 조회 API 성공")
    void getUserCouponsApi_success() throws Exception {
        // given
        // 1. 테스트용 사용자, 쿠폰, 발급된 쿠폰 데이터를 저장
        UserDomain user = userRepository.save(UserDomain.builder().email("couponuser@example.com").password("password").build());
        CouponDomain coupon = couponRepository.save(CouponDomain.builder().couponName("조회용 쿠폰").totalQuantity(100L).validityPeriod(30).build());
        issuedCouponRepository.save(IssuedCouponDomain.builder().user(user).coupon(coupon).build());

        // when & then
        mockMvc.perform(get("/users/" + user.getId() + "/coupons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].couponName").value("조회용 쿠폰"))
                .andDo(print());
    }

    @Test
    @DisplayName("사용자 쿠폰 목록 조회 API 실패 - 사용자를 찾을 수 없음")
    void getUserCouponsApi_fail_userNotFound() throws Exception {
        // when & then
        mockMvc.perform(get("/users/9999/coupons"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}
