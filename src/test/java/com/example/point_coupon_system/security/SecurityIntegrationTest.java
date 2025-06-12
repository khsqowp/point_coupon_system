package com.example.point_coupon_system.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("관리자 API 접근 테스트 - ADMIN 권한")
    @WithMockUser(authorities = "ROLE_ADMIN")
    void createCouponApi_Success_WithAdminRole() throws Exception {
        String requestBody = "{\"couponName\":\"관리자 전용 쿠폰\", \"totalQuantity\":100, \"validityPeriod\":30}";

        mockMvc.perform(post("/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(csrf())) // CSRF 토큰 추가
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @DisplayName("관리자 API 접근 테스트 - USER 권한")
    @WithMockUser(authorities = "ROLE_USER")
    void createCouponApi_Fail_WithUserRole() throws Exception {
        String requestBody = "{\"couponName\":\"사용자 등록 시도\", \"totalQuantity\":100, \"validityPeriod\":30}";

        mockMvc.perform(post("/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andDo(print());
    }
}
