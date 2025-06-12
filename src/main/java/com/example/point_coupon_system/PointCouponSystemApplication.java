package com.example.point_coupon_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableRetry // 재시도 기능 활성화
@EnableScheduling
@SpringBootApplication
public class PointCouponSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(PointCouponSystemApplication.class, args);
    }

}
