package com.example.point_coupon_system.service;

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
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class CouponConcurrencyTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IssuedCouponRepository issuedCouponRepository;

    private CouponDomain testCoupon;
    private final List<UserDomain> testUsers = new ArrayList<>();

    @BeforeEach
    void setUp() {
        // 동시성 테스트를 위해 재고가 1개인 쿠폰 생성
        testCoupon = couponRepository.save(
                CouponDomain.builder()
                        .couponName("동시성 테스트 쿠폰")
                        .totalQuantity(1L)
                        .validityPeriod(7)
                        .build()
        );
        // 테스트에 사용할 사용자 100명 생성
        for (int i = 0; i < 100; i++) {
            testUsers.add(userRepository.save(
                    UserDomain.builder()
                            .email("test" + i + "@example.com")
                            .password("password")
                            .build()
            ));
        }
    }

    @AfterEach
    void tearDown() {
        issuedCouponRepository.deleteAll();
        couponRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("100명의 사용자가 동시에 쿠폰 1개를 발급 요청 시, 단 1개만 발급되어야 한다 (낙관적 락 + 재시도)")
    void issueCoupon_concurrency_test_with_optimistic_lock_and_retry() throws InterruptedException {
        // given
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            final long userId = testUsers.get(i).getId();
            executorService.submit(() -> {
                try {
                    couponService.issueCoupon(testCoupon.getId(), userId);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드가 작업을 마칠 때까지 대기
        executorService.shutdown();

        // then
        // 발급된 쿠폰의 총 개수가 1개인지 확인
        long issuedCount = issuedCouponRepository.count();
        assertEquals(1, issuedCount);

        // 쿠폰 정책의 발급된 수량이 1인지 확인
        CouponDomain coupon = couponRepository.findById(testCoupon.getId()).orElseThrow();
        assertEquals(1, coupon.getIssuedQuantity());
    }
}
