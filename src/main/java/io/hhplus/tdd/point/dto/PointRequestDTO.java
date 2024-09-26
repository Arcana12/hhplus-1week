package io.hhplus.tdd.point.dto;

import io.hhplus.tdd.common.exception.UserException;

public record PointRequestDTO(
    Long userId
) {
    //PointRequestDTO 사용시 사용자 ID 검증
    public PointRequestDTO{
        if(userId == null || userId < 0){
            throw new UserException("ID가 잘 못 되었습니다.");
        }
    }

}
