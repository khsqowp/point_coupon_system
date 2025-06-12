package com.example.point_coupon_system.kafka;

import com.example.point_coupon_system.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponIssueConsumer {

    private final CouponService couponService;
    private static final String TOPIC = "coupon_issue";

    @KafkaListener(topics = TOPIC, groupId = "point_coupon_system_group")
    public void consume(String message) {
        log.info("수신 메시지: {}", message);
        try {
            String[] parts = message.split(",");
            if (parts.length != 2) {
                log.error("잘못된 형식의 메시지입니다: {}", message);
                return;
            }
            Long couponId = Long.parseLong(parts[0]);
            Long userId = Long.parseLong(parts[1]);

            couponService.issueCoupon(couponId, userId);
            log.info("{}번 쿠폰이 {}번 사용자에게 발급 처리되었습니다.", couponId, userId);

        } catch (Exception e) {
            // 컨슈머에서 발생한 예외는 별도의 처리가 필요합니다. (e.g., Dead Letter Queue)
            // 여기서는 로그만 남깁니다.
            log.error("쿠폰 발급 처리 중 오류 발생: {}", message, e);
        }
    }
}
