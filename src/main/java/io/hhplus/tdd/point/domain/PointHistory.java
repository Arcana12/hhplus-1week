package io.hhplus.tdd.point.domain;

import io.hhplus.tdd.common.exception.UserException;

public record PointHistory(
        Long id,
        Long userId,
        Long amount,
        TransactionType type,
        Long updateMillis
) {

}
