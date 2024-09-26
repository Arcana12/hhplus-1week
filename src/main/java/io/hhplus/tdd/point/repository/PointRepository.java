package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.dto.PointRequestDTO;
import io.hhplus.tdd.point.dto.UserPointDTO;
import java.util.List;

public interface PointRepository {

    //포인트를 조회
    public UserPoint selectPointOne(PointRequestDTO pointRequestDTO);

    //포인트를 충전/이용 내역 조회
    public List<PointHistory> selectAllHistory(PointRequestDTO pointRequestDTO);

    //포인트를 충전하는 기능
    public UserPoint insertChargePoint(UserPointDTO userPointDTO);

    //포인트를 사용하는 기능
    public UserPoint insertUsePoint(UserPointDTO userPointDTO);

}
