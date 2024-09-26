package io.hhplus.tdd.point.dto;

import static org.junit.jupiter.api.Assertions.*;

import io.hhplus.tdd.common.exception.UserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PointRequestDTOTest {

    @Test
    @DisplayName("유저ID가 널일 때 예외 발생 테스트")
    public void testUserIdNull() {
        assertThrows(UserException.class, () -> {
            new PointRequestDTO(null);
        });
    }

    @Test
    @DisplayName("유저ID가 0보다 작을 때 예외 발생 테스트")
    public void testUserIdLessThanZero() {
        assertThrows(UserException.class, () -> {
            new PointRequestDTO(-1L);
        });
    }
}