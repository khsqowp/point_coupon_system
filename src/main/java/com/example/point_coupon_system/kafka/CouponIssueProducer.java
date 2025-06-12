package com.example.point_coupon_system.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponIssueProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "coupon_issue";

    /**
     * 쿠폰 발급 요청 메시지를 Kafka 토픽으로 전송합니다.
     * @param couponId 발급할 쿠폰의 ID
     * @param userId 요청한 사용자의 ID
     */
    public void send(Long couponId, Long userId) {
        // 메시지는 필요에 따라 JSON 직렬화 등을 통해 더 복잡한 구조로 만들 수 있습니다.
        // 여기서는 "{couponId},{userId}" 형식의 간단한 문자열로 전송합니다.
        String message = couponId + "," + userId;
        kafkaTemplate.send(TOPIC, message);
    }
}
