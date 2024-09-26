1. Synchronized
자바 멀티 스레드 환경에서는 스레드끼리 static 영역과 heap 영역을 공유하므로 공유자원에 대한 동기화 문제를 신경써야 합니다.
이 때, 원자성 문제를 해결하기 위한 방법 중 하나인 자바에서 제공하는 키워드 Synchronized를 사용하게 됩니다.
스레드 동기화는 멀티스레드 환경에서 여러 스레드가 하나의 공유자원에 동시에 접근하지 못하도록 막는 것을 말합니다. 공유 데이터가 상요되어 동기화가 필요한 부분을 임계영역이라고 부르며 자바에서는 이 임계 영역에 Synchronized 키워드를 사용하여 여러 스레드가 동시에 접근하는 것을 막습니다.
Synchronized로 지정된 임계영역은 lock이 걸리게 되어 다른 스레드가 접근할 수 없게 됩니다.
이후 해당 스레드가 이 임계영역의 코드를 다 실행한 후 벗어나면 unlock 상태가 되고, 대기하던 다른 스레드가 다시 lock을 걸고 사용하는 것입니다.

Synchronized는 lock을 이용해 동기화를 수행하며 네 가지의 사용 방법이 존재합니다.

1-1. Synchronized Method
메소드 이름 앞에 Synchronized 키워드를 사용하며 해당 메소드 전체를 임계영역으로 설정할 수 있습니다.
1-2. static Synchronized method
static 키워드가 포함된 Synchronized 메소드는 인스턴스가 아닌 크래스 단위로 lock을 공유합니다.
1-3. Synchronized block
인스턴스의 blcok 단위로 lock을 거는 것입니다.
Synchronized(Object)는 사용 시, 블록마다 다른 lock이 걸리게 하여 효율적으로 사용할 수 있습니다.
1-4. static Synchronized block
static method 안에 Synchronized block을 지정할 수 있습니다. static Synchronized method방식과의 차이는 lock 객체를 지정하고, block으로 범위를 한정 지을 수 있다는 것입니다.

이러한 Synchronized의 단점은 lock을 명시적으로 해제하거나 재획득 하는 등의 제어가 어렵고, 타임아웃 설정이 불가능해 lock이 언제 해제될 지 제어 할 수 없다는 것입니다.

그리고 비슷한 기능을 하는 ReentrantLock과 비교했을 때, 성능이 떨어질 수 있습니다.

2. ReentrantLock
ReentrantLock은 java.util.concurrent.locks 패키지에 포함된 명시적인 동기화 제어를 제공하는 lock입니다. 이는 synchronized보다 더 많은 기능과 유연성을 제공합니다.
ReentrantLock은 명시적으로 락을 획득하고 해제할지 제어할 수 있습니다.
tryLock()을 사용하여 타임아웃을 설정하거나 락을 바로 획득할 수 없을 때 다른 작업을 할 수 있습니다.

3. ThreadLocal
스레드 로컬은 해당 스레드만 접근할 수 있는 특별한 저장소를 말합니다.
스레드 로컬은 스레드마다 별도의 내부 저장소를 제공하여 같은 인스턴스의 스레드 로컬 필드에 접근해도 문제 없습니다.
주의사항으로는 스레드 로컬을 모두 사용하고 나면 remove를 호출해서 저장된 값을 제거해 주어야 합니다.
스레드 로컬의 값을 사용 후 제거하지 않고 그냥 두면 WAS(톰캣)처럼 쓰레드 풀을 사용하는 경우에 다른 사람의 데이터를 확인하게 되는 문제가 발생할 수 있습니다.


이 세가지 방법을 요약하면 아래와 같습니다.

synchronized: 가장 간단한 동기화 방법으로, 간단한 락 처리에 적합합니다. 하지만 복잡한 동시성 제어가 필요한 경우 유연성이 떨어집니다.

ReentrantLock: 더 유연하고 고급 기능을 제공합니다. 락의 획득과 해제를 제어할 수 있고, 타임아웃 설정 및 Condition 객체를 통한 세밀한 스레드 제어가 가능합니다.

ThreadLocal: 동시성 제어를 위한 것이 아니라, 스레드마다 독립적인 데이터를 관리할 때 유용합니다. 동시성이 문제되는 상황에서 사용하지는 않으며, 스레드 간 데이터 공유가 필요하지 않을 때 적합합니다.

각각의 방법은 상황에 따라 선택적으로 사용될 수 있으며, 단순한 경우에는 synchronized가, 복잡한 동기화가 필요할 때는 ReentrantLock, 스레드별 독립적 데이터 관리가 필요한 경우에는 ThreadLocal이 적합하다고 생각합니다.