package com.example.point_coupon_system.repository;

import com.example.point_coupon_system.domain.UserDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserDomain, Long> {
    /**
     * 이메일로 사용자를 조회합니다.
     * @param email 사용자 이메일
     * @return Optional<User>
     */
    Optional<UserDomain> findByEmail(String email);
}
