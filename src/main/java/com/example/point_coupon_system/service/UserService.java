package com.example.point_coupon_system.service;

import com.example.point_coupon_system.domain.IssuedCouponDomain;
import com.example.point_coupon_system.domain.UserDomain;
import com.example.point_coupon_system.domain.UserRoleEnum;
import com.example.point_coupon_system.dto.IssuedCouponResponseDTO;
import com.example.point_coupon_system.dto.UserLoginRequestDTO;
import com.example.point_coupon_system.dto.UserSignupRequestDTO;
import com.example.point_coupon_system.repository.IssuedCouponRepository;
import com.example.point_coupon_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IssuedCouponRepository issuedCouponRepository;

    @Value("${admin.token}")
    private String adminToken;

    @Transactional
    public void signup(UserSignupRequestDTO requestDto) {
        String email = requestDto.getEmail();
        String password = passwordEncoder.encode(requestDto.getPassword());

        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        UserRoleEnum role = UserRoleEnum.USER;
        if (requestDto.isAdmin()) {
            if (!adminToken.equals(requestDto.getAdminToken())) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = UserRoleEnum.ADMIN;
        }

        UserDomain user = UserDomain.builder()
                .email(email)
                .password(password)
                .role(role)
                .build();
        userRepository.save(user);
    }

    public UserDomain login(UserLoginRequestDTO requestDto) {
        String email = requestDto.getEmail();
        String password = requestDto.getPassword();

        UserDomain user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("등록된 사용자가 없습니다."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return user;
    }

    @Transactional(readOnly = true)
    public List<IssuedCouponResponseDTO> getIssuedCoupons(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        List<IssuedCouponDomain> issuedCoupons = issuedCouponRepository.findAllByUserId(userId);

        return issuedCoupons.stream()
                .map(IssuedCouponResponseDTO::new)
                .collect(Collectors.toList());
    }
}
