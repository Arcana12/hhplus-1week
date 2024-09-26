package io.hhplus.tdd.point.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.hhplus.tdd.common.exception.AmountException;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.TransactionType;
import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.common.exception.UserException;
import io.hhplus.tdd.point.dto.PointRequestDTO;
import io.hhplus.tdd.point.dto.UserPointDTO;
import io.hhplus.tdd.point.repository.PointRepository;
import io.hhplus.tdd.point.util.PointCalculator;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


class PointServiceTest {


    private static final Logger log = LoggerFactory.getLogger(PointServiceTest.class);
    private PointService pointService;

    @Mock
    private PointRepository pointRepository;

    @Mock
    private PointCalculator pointCalculator;


    private List<PointHistory> historyList;

    @BeforeEach
    public void init() {
        //모든 mockito Annotation이 적용된 필드 초기화
        MockitoAnnotations.openMocks(this);
        pointService = new PointService(pointRepository, pointCalculator);

        //충전 내역 조회 테스트 데이터
        historyList = new ArrayList<>();
        historyList.add(new PointHistory(1L, 1L, 500L, TransactionType.CHARGE, 0L));
        historyList.add(new PointHistory(2L, 1L,200L, TransactionType.USE, 0L));
    }

    @Test
    @DisplayName("포인트 조회 테스트")
    public void testGetUserPoint() {
        UserPoint userPoint = new UserPoint(1L, 500L, 0L);
        when(pointRepository.selectPointOne(any(PointRequestDTO.class))).thenReturn(userPoint);

        UserPoint result = pointService.getUserPoint(new PointRequestDTO(1L));
        assertEquals(userPoint, result);
    }

    @Test
    @DisplayName("포인트 이용/충전 내역 조회 테스트")
    public void testGetPointHistory() {


        when(pointRepository.selectAllHistory(any(PointRequestDTO.class))).thenReturn(historyList);

        List<PointHistory> result = pointService.getPointHistory(new PointRequestDTO(1L));
        assertEquals(historyList, result);
    }

    @Test
    @DisplayName("포인트 이용/충전 내역 조회 실패 테스트 - 잘못된 사용자 ID")
    public void testGetPointHistoryFailureInvalidUserId() {

        when(pointRepository.selectAllHistory(any(PointRequestDTO.class))).thenReturn(new ArrayList<>());

        assertThrows(UserException.class, () -> pointService.getPointHistory(new PointRequestDTO(-1L)));
    }

    @Test
    @DisplayName("포인트 이용/충전 내역 조회 실패 테스트 - Repository에서 예외 발생")
    public void testGetPointHistoryFailureRepositoryException() {

        when(pointRepository.selectAllHistory(any(PointRequestDTO.class))).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> pointService.getPointHistory(new PointRequestDTO(1L)));
    }

    @Test
    @DisplayName("포인트 충전 성공")
    public void testChargePointSuccess() {
        UserPointDTO request = new UserPointDTO(1L, 100L, TransactionType.CHARGE);
        UserPoint currentPoint = new UserPoint(1L, 500L, 0L);
        UserPoint updatedPoint = new UserPoint(1L, 600L, 0L);

        when(pointService.getUserPoint(any(PointRequestDTO.class))).thenReturn(currentPoint);
        when(pointCalculator.calculateAmount(anyLong(), anyLong(), any(TransactionType.class))).thenReturn(600L);
        when(pointRepository.insertChargePoint(any(UserPointDTO.class))).thenReturn(updatedPoint);

        UserPoint result = pointService.chargePoint(request);
        assertEquals(updatedPoint, result);
    }

    @Test
    @DisplayName("포인트 충전 실패")
    public void testChargePointFailure() {
        UserPointDTO request = new UserPointDTO(1L, 100L, TransactionType.CHARGE);

        when(pointService.getUserPoint(any(PointRequestDTO.class))).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> pointService.chargePoint(request));
    }

    @Test
    @DisplayName("포인트 사용 성공")
    public void testUsePointSuccess() {
        UserPointDTO request = new UserPointDTO(1L, 100L, TransactionType.USE);
        UserPoint currentPoint = new UserPoint(1L, 500L, 0L);
        UserPoint updatedPoint = new UserPoint(1L, 400L, 0L);

        when(pointService.getUserPoint(any(PointRequestDTO.class))).thenReturn(currentPoint);
        when(pointCalculator.calculateAmount(anyLong(), anyLong(), any(TransactionType.class))).thenReturn(400L);
        when(pointRepository.insertUsePoint(any(UserPointDTO.class))).thenReturn(updatedPoint);

        UserPoint result = pointService.usePoint(request);
        assertEquals(updatedPoint, result);
    }

    @Test
    @DisplayName("포인트 사용 실패")
    public void testUsePointFailure() {
        UserPointDTO request = new UserPointDTO(1L, 100L, TransactionType.USE);

        when(pointService.getUserPoint(any(PointRequestDTO.class))).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> pointService.usePoint(request));
    }


    @Test
    @DisplayName("포인트 사용을 이용한 동시성 테스트")
    public void testConcurrentUsePoint() throws InterruptedException {
        // Given
        Long userId = 1L;
        Long initialPoint = 1000L;
        Long useAmount = 50L;

        // 사용자 포인트 초기값 설정
        when(pointRepository.selectPointOne(any(PointRequestDTO.class)))
            .thenReturn(new UserPoint(userId, initialPoint, 0L));

        // 포인트 계산 로직 설정
        when(pointCalculator.calculateAmount(anyLong(), anyLong(), any(TransactionType.class)))
            .thenAnswer(invocation -> {
                Long currentPoint = invocation.getArgument(0);
                Long amount = invocation.getArgument(1);
                TransactionType type = invocation.getArgument(2);

                if (type == TransactionType.USE) {
                    return currentPoint - amount; // 포인트 사용
                }
                return currentPoint;
            });

        // 데이터베이스에 저장되는 포인트는 동일하게 반환
        when(pointRepository.insertUsePoint(any(UserPointDTO.class)))
            .thenAnswer(invocation -> new UserPoint(userId, invocation.getArgument(0, UserPointDTO.class).amount(), 0L));

        // 스레드 풀과 CountDownLatch로 동시성 테스트 실행
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 스레드가 동시에 포인트 사용을 실행하도록 설정
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    // 포인트 사용
                    pointService.usePoint(new UserPointDTO(userId, useAmount, TransactionType.USE));
                    log.info("amount :  {}, Thread : {}", useAmount, Thread.currentThread().getName());
                } finally {
                    latch.countDown();  // 스레드가 끝날 때 latch 카운트 감소
                }
            });
        }

        // 모든 스레드가 종료될 때까지 대기
        latch.await();
        executorService.shutdown();

        // PointRepository의 메서드가 호출되었는지 검증
        verify(pointRepository, atLeast(threadCount)).selectPointOne(any(PointRequestDTO.class));
        verify(pointRepository, atLeast(threadCount)).insertUsePoint(any(UserPointDTO.class));
    }

}
