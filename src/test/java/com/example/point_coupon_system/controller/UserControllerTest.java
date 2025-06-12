package com.example.point_coupon_system.controller;

import com.example.point_coupon_system.config.jwt.JwtUtil; // [수정] JwtUtil import
import com.example.point_coupon_system.domain.CouponDomain;
import com.example.point_coupon_system.domain.IssuedCouponDomain;
import com.example.point_coupon_system.domain.UserDomain;
import com.example.point_coupon_system.domain.UserRoleEnum;
import com.example.point_coupon_system.repository.CouponRepository;
import com.example.point_coupon_system.repository.IssuedCouponRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private IssuedCouponRepository issuedCouponRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired // [수정] JwtUtil 의존성 주입 추가
    private JwtUtil jwtUtil;

    private UserDomain testUser;

    @BeforeEach
    void setUp() {
        testUser = UserDomain.builder()
                .email("testuser@example.com")
                .password(passwordEncoder.encode("password123"))
                .role(UserRoleEnum.USER)
                .build();
        userRepository.save(testUser);
    }

    @Test
    @DisplayName("회원가입 API 호출 성공")
    void signupApi_success() throws Exception {
        String jsonBody = "{\"email\":\"newuser@example.com\", \"password\":\"password123\"}";
        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() throws Exception {
        String jsonBody = "{\"email\":\"testuser@example.com\", \"password\":\"password123\"}";
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(header().exists(JwtUtil.AUTHORIZATION_HEADER))
                .andDo(print());
    }

    @Test
    @DisplayName("사용자 쿠폰 목록 조회 API 성공")
    void getUserCouponsApi_success() throws Exception {
        // given
        // [수정] 테스트 사용자를 위한 JWT 생성
        String token = jwtUtil.createToken(testUser.getEmail(), testUser.getRole());

        CouponDomain coupon = couponRepository.save(CouponDomain.builder().couponName("조회용 쿠폰").totalQuantity(100L).validityPeriod(30).build());
        issuedCouponRepository.save(IssuedCouponDomain.builder().user(testUser).coupon(coupon).build());

        // when & then
        mockMvc.perform(get("/users/" + testUser.getId() + "/coupons")
                        // [수정] 생성된 JWT를 헤더에 추가하여 인증된 사용자의 요청을 시뮬레이션
                        .header(JwtUtil.AUTHORIZATION_HEADER, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andDo(print());
    }
}