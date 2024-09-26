package io.hhplus.tdd.point.domain;

import static org.junit.jupiter.api.Assertions.*;

import io.hhplus.tdd.common.exception.PointException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserPointTest {

    @Test
    @DisplayName("id가 null인 경우 예외 발생 확인")
    void testUserPointCreationWithNullId() {
        Exception exception = assertThrows(PointException.class, () -> {
            new UserPoint(null, 100L, System.currentTimeMillis());
        });

        // 예외 메시지 검증
        assertEquals("포인트 조회에 실패하였습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("point가 null인 경우 예외 발생 확인")
    void testUserPointCreationWithNullPoint() {
        Exception exception = assertThrows(PointException.class, () -> {
            new UserPoint(1L, null, System.currentTimeMillis());
        });

        // 예외 메시지 검증
        assertEquals("포인트 조회에 실패하였습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("updateMillis가 null인 경우 예외 발생 확인")
    void testUserPointCreationWithNullUpdateMillis() {

        Exception exception = assertThrows(PointException.class, () -> {
            new UserPoint(1L, 100L, null);
        });

        // 예외 메시지 검증
        assertEquals("포인트 조회에 실패하였습니다.", exception.getMessage());
    }
}