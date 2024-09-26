package io.hhplus.tdd.point.util;

import static org.junit.jupiter.api.Assertions.*;

import io.hhplus.tdd.common.exception.PointException;
import io.hhplus.tdd.point.domain.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PointCalculatorTest {

    private final PointCalculator subject = new PointCalculator();

    @Test
    @DisplayName("포인트 충전 계산 검증 테스트")
    public void testCalculateAmountCharge() {
        long current = 0;
        long amount = 500;
        assertEquals(500, subject.calculateAmount(current, amount, TransactionType.CHARGE));
    }

    @Test
    @DisplayName("포인트 사용 계산 검증 테스트")
    public void testCalculateAmountUse() {
        long current = 500;
        long amount = 100;
        assertEquals(400, subject.calculateAmount(current, amount, TransactionType.USE));
    }

    @Test
    @DisplayName("포인트 부족 예외 테스트")
    public void testValidAmountExceptionLess() {
        long current = 100;
        long amount = 200;
        assertThrows(PointException.class, () -> {
            subject.calculateAmount(current, amount, TransactionType.USE);
        });
    }

    @Test
    @DisplayName("최대 포인트 초과 예외 테스트")
    public void testValidAmountExceptionMore() {
        long current = 90000;
        long amount = 20000;
        assertThrows(PointException.class, () -> {
            subject.calculateAmount(current, amount, TransactionType.CHARGE);
        });
    }
}