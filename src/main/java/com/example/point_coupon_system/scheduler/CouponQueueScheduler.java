package com.example.point_coupon_system.scheduler;

import com.example.point_coupon_system.kafka.CouponIssueProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponQueueScheduler {

    private final RedisTemplate<String, String> redisTemplate;
    private final CouponIssueProducer couponIssueProducer;
    private static final String COUPON_QUEUE_KEY_PREFIX = "coupon:queue:";
    private static final long BATCH_SIZE = 100L; // 한 번에 처리할 요청 수

    // 1초마다 실행
    @Scheduled(fixedRate = 1000)
    public void processCouponQueue() {
        log.info("쿠폰 대기열 처리 스케줄러 실행");

        // 예시로 couponId=1에 대해서만 처리합니다.
        // 실제로는 여러 쿠폰 이벤트에 대해 동적으로 처리하는 로직이 필요합니다.
        long couponId = 1L;
        String queueKey = COUPON_QUEUE_KEY_PREFIX + couponId;

        // ZRANGE key min max (0-based index)
        // 대기열에서 BATCH_SIZE만큼의 사용자 ID를 가져옵니다.
        Set<String> userIds = redisTemplate.opsForZSet().range(queueKey, 0, BATCH_SIZE - 1);

        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        log.info("대기열에서 {}개의 요청 처리 시작", userIds.size());

        for (String userIdStr : userIds) {
            Long userId = Long.parseLong(userIdStr);
            // Kafka 토픽으로 발급 요청 메시지 전송
            couponIssueProducer.send(couponId, userId);
        }

        // ZREMRANGEBYRANK key min max
        // 처리된 사용자들을 대기열에서 제거합니다.
        redisTemplate.opsForZSet().removeRange(queueKey, 0, userIds.size() - 1);

        log.info("처리 완료. {}개의 요청을 대기열에서 제거했습니다.", userIds.size());
    }
}
