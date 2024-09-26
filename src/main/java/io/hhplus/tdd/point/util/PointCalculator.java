package io.hhplus.tdd.point.util;

import io.hhplus.tdd.point.domain.TransactionType;
import io.hhplus.tdd.common.exception.PointException;
import org.springframework.stereotype.Component;

@Component
public class PointCalculator {

    // 현재 포인트 양에 따른 업데이트 된 포인트 양을 계산
    public long calculateAmount(long currentAmount, long amount, TransactionType type) {
        long updatedAmount = currentAmount;
        if (type == TransactionType.CHARGE) {
            updatedAmount += amount;
        } else if (type == TransactionType.USE) {
            updatedAmount -= amount;
        }
        // 업데이트된 포인트 양이 0보다 작은거나 100,000보다 큰지 확인
        validateUpdatedAmount(updatedAmount);
        return updatedAmount;
    }

    // 사용 또는 충전되는 포인트가 0이거나, 업데이트된 포인트 양이 0보다 작으면 예외 발생
    void validateUpdatedAmount(long updatedAmount) {
        if(updatedAmount < 0) {
            throw new PointException("포인트가 부족합니다.");
        }else if(updatedAmount > 100000){
            throw new PointException("최대 포인트를 초과합니다.");
        }
    }
}
