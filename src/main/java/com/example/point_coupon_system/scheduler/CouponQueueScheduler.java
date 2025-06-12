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
    private static final long BATCH_SIZE = 100L;

    @Scheduled(fixedRate = 1000)
    public void processCouponQueues() {
        log.info("쿠폰 대기열 처리 스케줄러 실행");

        Set<String> queueKeys = redisTemplate.keys(COUPON_QUEUE_KEY_PREFIX + "*");
        if (queueKeys == null || queueKeys.isEmpty()) {
            return;
        }

        for (String queueKey : queueKeys) {
            try {
                Set<String> userIds = redisTemplate.opsForZSet().range(queueKey, 0, BATCH_SIZE - 1);
                if (userIds == null || userIds.isEmpty()) {
                    continue;
                }

                String couponIdStr = queueKey.substring(COUPON_QUEUE_KEY_PREFIX.length());
                Long couponId = Long.parseLong(couponIdStr);

                log.info("{} 대기열에서 {}개의 요청 처리 시작", queueKey, userIds.size());

                for (String userIdStr : userIds) {
                    couponIssueProducer.send(couponId, Long.parseLong(userIdStr));
                }

                redisTemplate.opsForZSet().removeRange(queueKey, 0, userIds.size() - 1);
                log.info("처리 완료. {}개의 요청을 {} 대기열에서 제거했습니다.", userIds.size(), queueKey);

            } catch (Exception e) {
                log.error("{} 처리 중 오류 발생", queueKey, e);
            }
        }
    }
}
