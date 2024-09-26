package io.hhplus.tdd.point.dto;

import io.hhplus.tdd.common.exception.AmountException;
import io.hhplus.tdd.common.exception.UserException;
import io.hhplus.tdd.point.domain.TransactionType;

public record UserPointDTO (
    Long id,
    Long amount,
    TransactionType type
) {
    public UserPointDTO{
        if(id == null || id < 0){
            throw new UserException("ID가 잘 못 되었습니다.");
        }
        if(amount == null || amount < 0){
            throw new AmountException("금액이 잘 못 입력되었습니다.");
        }
    }

}
