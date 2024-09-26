package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.domain.TransactionType;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.dto.PointRequestDTO;
import io.hhplus.tdd.point.dto.UserPointDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepository{

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    //포인트를 조회
    public UserPoint selectPointOne(PointRequestDTO pointRequestDTO){
        return userPointTable.selectById(pointRequestDTO.userId());
    }

    //포인트를 충전/이용 내역 조회
    public List<PointHistory> selectAllHistory(PointRequestDTO pointRequestDTO){
        return pointHistoryTable.selectAllByUserId(pointRequestDTO.userId());
    }

    //포인트를 충전하는 기능
    public UserPoint insertChargePoint(UserPointDTO userPointDTO){
        UserPoint result = userPointTable.insertOrUpdate(userPointDTO.id(), userPointDTO.amount());
        pointHistoryTable.insert(result.id(), result.point(), TransactionType.CHARGE, result.updateMillis());
        return result;
    }

    //포인트를 사용하는 기능
    public UserPoint insertUsePoint(UserPointDTO userPointDTO){
        UserPoint result = userPointTable.insertOrUpdate(userPointDTO.id(), userPointDTO.amount());
        pointHistoryTable.insert(result.id(), result.point(), TransactionType.CHARGE, result.updateMillis());
        return result;
    }
}
