package com.example.point_coupon_system.e2e;

import com.example.point_coupon_system.domain.CouponDomain;
import com.example.point_coupon_system.domain.UserDomain;
import com.example.point_coupon_system.repository.CouponRepository;
import com.example.point_coupon_system.repository.IssuedCouponRepository;
import com.example.point_coupon_system.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
//@Transactional // DB 상태 롤백
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
class CouponIssueE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private IssuedCouponRepository issuedCouponRepository;

    private UserDomain testUser;
    private CouponDomain testCoupon;

    @BeforeEach
    void setUp() {
        // 테스트 실행 전 Redis 데이터 초기화
        redisTemplate.getConnectionFactory().getConnection().flushDb();

        // 테스트에 사용할 사용자, 쿠폰 정책 생성
        testUser = userRepository.save(UserDomain.builder().email("e2e@example.com").password("password").build());
        // id는 DB에서 자동 생성되므로 빌더에서 제거
        testCoupon = couponRepository.save(CouponDomain.builder().couponName("E2E 테스트 쿠폰").totalQuantity(100L).validityPeriod(7).build());
    }

    @Test
    @DisplayName("E2E 테스트: 쿠폰 발급 요청부터 최종 발급까지의 전체 흐름 검증")
    void couponIssuance_E2E_Test() throws Exception {
        // given
        Long userId = testUser.getId();
        Long couponId = testCoupon.getId();
        // 실제 생성된 쿠폰 ID를 기반으로 동적으로 큐 키 생성
        String queueKey = "coupon:queue:" + couponId;

        // when
        // 1. 사용자가 쿠폰 발급 API를 호출하여 Redis 대기열에 추가
        mockMvc.perform(post("/coupons/" + couponId + "/apply")
                        .param("userId", userId.toString()))
                .andExpect(status().isOk());

        // then
        // 2. Redis 대기열에 요청이 추가되었는지 확인
        assertEquals(1, (long) Objects.requireNonNull(redisTemplate.opsForZSet().size(queueKey)));

        // 3. 스케줄러와 컨슈머가 작업을 완료하고, 발급된 쿠폰이 DB에 저장될 때까지 최대 10초간 대기
        // Awaitility 라이브러리를 사용하면 비동기 테스트를 안정적으로 수행할 수 있습니다.
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() ->
                assertEquals(1, issuedCouponRepository.count())
        );

        // 4. 쿠폰 정책의 발급 수량이 1 증가했는지 확인
        CouponDomain updatedCoupon = couponRepository.findById(couponId).orElseThrow();
        assertEquals(1, updatedCoupon.getIssuedQuantity());

        // 5. Redis 대기열에서 처리된 요청이 제거되었는지 확인
        assertEquals(0, (long) Objects.requireNonNull(redisTemplate.opsForZSet().size(queueKey)));
    }
}
