package io.hhplus.tdd.point.dto;

import static org.junit.jupiter.api.Assertions.*;

import io.hhplus.tdd.common.exception.UserException;
import io.hhplus.tdd.common.exception.AmountException;
import io.hhplus.tdd.point.domain.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserPointDTOTest {

    @Test
    @DisplayName("유저 ID가 잘못되었을 경우 예외 발생 테스트")
    public void testInvalidId() {
        assertThrows(UserException.class, () -> new UserPointDTO(-1L, 500L, TransactionType.CHARGE));
        assertThrows(UserException.class, () -> new UserPointDTO(null, 500L, TransactionType.CHARGE));
    }

    @Test
    @DisplayName("금액이 잘 못 입력되었을 경우 예외 발생 테스트")
    public void testInvalidAmount() {
        assertThrows(AmountException.class, () -> new UserPointDTO(1L, -1L, TransactionType.CHARGE));
        assertThrows(AmountException.class, () -> new UserPointDTO(1L, null, TransactionType.CHARGE));
    }
}