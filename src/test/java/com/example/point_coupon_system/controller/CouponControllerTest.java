package com.example.point_coupon_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("쿠폰 정책 등록 API 호출 성공")
    void createCouponApi_success() throws Exception {
        // given
        String jsonBody = "{\"couponName\":\"신규 가입 환영 쿠폰\", \"totalQuantity\":100, \"validityPeriod\":7}";

        // when & then
        mockMvc.perform(post("/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @DisplayName("쿠폰 정책 등록 API 실패 - 쿠폰 이름 누락")
    void createCouponApi_fail_with_blank_name() throws Exception {
        // given
        String jsonBody = "{\"couponName\":\"\", \"totalQuantity\":100, \"validityPeriod\":7}";

        // when & then
        mockMvc.perform(post("/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("쿠폰 정책 등록 API 실패 - 수량 0개")
    void createCouponApi_fail_with_zero_quantity() throws Exception {
        // given
        String jsonBody = "{\"couponName\":\"재고 0개 쿠폰\", \"totalQuantity\":0, \"validityPeriod\":7}";

        // when & then
        mockMvc.perform(post("/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}
