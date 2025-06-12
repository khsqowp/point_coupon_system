package com.example.point_coupon_system.controller;

import com.example.point_coupon_system.dto.UserSignupRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // 각 테스트 후 롤백을 위함
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 API 호출 성공")
    void signupApi_success() throws Exception {
        // given
        UserSignupRequestDTO requestDto = new UserSignupRequestDTO();
        // DTO 필드 값을 설정하기 위해 리플렉션 대신 public setter 또는 생성자 사용을 권장합니다.
        // 테스트를 위해 임시로 public setter를 DTO에 추가하거나, 테스트용 생성자를 만들 수 있습니다.
        // 여기서는 개념 설명을 위해 필드가 public이라고 가정합니다. 실제 코드에서는 접근자를 사용하세요.
        // requestDto.setEmail("test@example.com");
        // requestDto.setPassword("password123");
        String jsonBody = "{\"email\":\"test@example.com\", \"password\":\"password123\"}";


        // when & then
        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 API 호출 실패 - 잘못된 이메일 형식")
    void signupApi_fail_with_invalid_email() throws Exception {
        // given
        String jsonBody = "{\"email\":\"invalid-email\", \"password\":\"password123\"}";

        // when & then
        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest()) // @Valid에 의해 400 Bad Request가 반환되어야 함
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 API 호출 실패 - 짧은 비밀번호")
    void signupApi_fail_with_short_password() throws Exception {
        // given
        String jsonBody = "{\"email\":\"test@example.com\", \"password\":\"pass\"}";

        // when & then
        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}
