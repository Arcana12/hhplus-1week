package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.dto.PointRequestDTO;
import io.hhplus.tdd.point.dto.UserPointDTO;
import io.hhplus.tdd.point.repository.PointRepository;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.TransactionType;
import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.util.PointCalculator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final PointCalculator pointCalculator;
    // ReentrantLock을 사용하여 동시성 제어
    private final ReentrantLock lock = new ReentrantLock();

    // 사용자의 포인트 조회
    public UserPoint getUserPoint(PointRequestDTO pointRequestDTO) {
        return pointRepository.selectPointOne(pointRequestDTO);
    }

    // 사용자의 포인트 이용/충전 내역 조회
    public List<PointHistory> getPointHistory(PointRequestDTO pointRequestDTO) {
        return pointRepository.selectAllHistory(pointRequestDTO);
    }

    // 포인트 충전
    public UserPoint chargePoint(UserPointDTO userPointDTO) {
        lock.lock();  // 락을 획득
        try {
            // 현재 가지고 있는 포인트 조회
            Long currentPoint = getUserPoint(new PointRequestDTO(userPointDTO.id())).point();
            // 포인트 계산 및 검증
            Long updatedAmount = pointCalculator.calculateAmount(currentPoint, userPointDTO.amount(), TransactionType.CHARGE);
            return pointRepository.insertChargePoint(new UserPointDTO(userPointDTO.id(), updatedAmount, TransactionType.CHARGE));
        } finally {
            // 반드시 락을 해제
            lock.unlock();
        }
    }

    // 포인트 사용
    public UserPoint usePoint(UserPointDTO userPointDTO) {
        lock.lock();  // 락을 획득
        try {
            // 현재 가지고 있는 포인트 조회
            Long currentPoint = getUserPoint(new PointRequestDTO(userPointDTO.id())).point();
            // 포인트 계산 및 검증
            Long updatedAmount = pointCalculator.calculateAmount(currentPoint, userPointDTO.amount(), TransactionType.USE);
            return pointRepository.insertUsePoint(new UserPointDTO(userPointDTO.id(), updatedAmount, TransactionType.USE));
        } finally {
            // 반드시 락을 해제
            lock.unlock();
        }
    }

}
