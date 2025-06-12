package com.example.point_coupon_system.service;

import com.example.point_coupon_system.domain.CouponDomain;
import com.example.point_coupon_system.dto.CouponCreateRequestDTO;
import com.example.point_coupon_system.repository.CouponRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    @Test
    @DisplayName("쿠폰 정책 생성 성공 테스트")
    void createCoupon_success() {
        // given
        CouponCreateRequestDTO requestDTO = new CouponCreateRequestDTO();
        ReflectionTestUtils.setField(requestDTO, "couponName", "6월 감사 쿠폰");
        ReflectionTestUtils.setField(requestDTO, "totalQuantity", 1000L);
        ReflectionTestUtils.setField(requestDTO, "validityPeriod", 30);

        ArgumentCaptor<CouponDomain> captor = ArgumentCaptor.forClass(CouponDomain.class);

        // when
        couponService.createCoupon(requestDTO);

        // then
        // couponRepository.save가 1번 호출되었는지 검증
        verify(couponRepository, times(1)).save(captor.capture());
        CouponDomain savedCoupon = captor.getValue();

        // 저장된 CouponDomain 객체의 필드가 DTO의 값과 일치하는지 검증
        assertEquals("6월 감사 쿠폰", savedCoupon.getCouponName());
        assertEquals(1000L, savedCoupon.getTotalQuantity());
        assertEquals(0L, savedCoupon.getIssuedQuantity()); // 초기 발급 수량은 0
        assertEquals(30, savedCoupon.getValidityPeriod());
    }
}
