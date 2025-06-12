// src/main/java/com/example/point_coupon_system/dto/UserLoginRequestDTO.java
package com.example.point_coupon_system.dto;

import lombok.Getter;

@Getter
public class UserLoginRequestDTO {
    private String email;
    private String password;
}

// src/main/java/com/example/point_coupon_system/dto/UserLoginResponseDTO.java
// 로그인은 성공/실패만 중요하므로, 별도의 응답 DTO는 생략하고 헤더에 토큰을 담아 반환합니다.
