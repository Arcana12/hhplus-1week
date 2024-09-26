package io.hhplus.tdd.point.domain;

import io.hhplus.tdd.common.exception.PointException;

public record UserPoint(
        Long id,
        Long point,
        Long updateMillis
) {
    //조회 시 값이 null 일때
    public UserPoint{
        if(id == null || point == null || updateMillis == null){
            throw new PointException("포인트 조회에 실패하였습니다.");
        }
    }

    public static UserPoint empty(Long id) {
        return new UserPoint(id, 0L, System.currentTimeMillis());
    }
}
