package com.example.point_coupon_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UserSignupRequestDTO {

    @Email(message = "유효한 이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 비워둘 수 없습니다.")
    private String email;

    @NotBlank(message = "비밀번호는 비워둘 수 없습니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{8,15}$", message = "비밀번호는 8~15자의 영문, 숫자를 포함해야 합니다.")
    private String password;

    // 관리자 회원가입 여부를 확인하는 필드
    private boolean admin = false;

    // 관리자 토큰
    private String adminToken = "";
}
