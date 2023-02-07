# chapter8
# 아이템 49 매개변수가 유효한지 검사하라  
메서드와 생성자 대부분은 입력 매개변수의 값이 특정 조건을 만족하기를 바란다.  
ex) 인덱스는 음수이면 안된다, 객체참조는 null이면 안된다.  
이런 제약은 문서화 해야 하며, 메서드 몸체가 시작되기 전에 검사해야 한다.   
이는 오류가 발생할 때 바로 잡기 위해서이다.   
매개변수를 바로 잡지 못하면, 메서드가 실행 중에 에러가 발생할 수도 있고, 최악의 결과는 반환되고 나서도 오류가 발생하지 않다가 나중에 발생하는 경우, 오류의 원인을 찾기가 힘들다.  
public 과 protected 메서드는 매개변수 값이 잘못됐을 때, 
- IllegalArgumentException
- IndexOutOfBoundsException
- NullPointerException
중 하나의 예외를 던질것이다.  

  
매개변수의 제약을 문서화 한다면, 그 제약을 어겼을 때 발생하는 예외도 함께 기술해야 한다. 
```java
/**
 * 항상 음이 아닌 BigInteger를 반환한다는 점에서 remainder 메서드와 다르다 
 * 
 * @param m 계수(양수여야 한다) 
 * @return 현재 값 mod m 
 * @throws ArithmeticException m 이 0보다 작거나 같으면 발생한다.
 */
public class itme49 {
    public BigInteger mod(BigInteger m) {
        if (m.signum() <= 0) {
            throw new ArithmeticException("계수 m 은 양수여야 합니다. " + m)
        }
    }
}
```
  
위 메서드에 m 이 null일 경우 어떻게 되는지 기술하지 않았다. 이건 BigInteger에서 알아서 처리하기 때문  
-> 클래스 수준의 예외는 일일이 기술하지 않는다.  
  
메서드가 직접 사용하지는 않으나, 나중에 쓰기 위해 저장하는 매개변수는 특히 더 신경 써서 검사해야 한다.  
  
때로는 계산 과정에서 필요한 유효성 검사가 이뤄지지만, 실패했을 때 잘못된 예외를 던지기도 한다.  
 
이번 아이템을 "매개변수에 제약을 두는 것이 좋다."고 해석하면 안된다.  
메서드는 최대한 범용적으로 설계해야 한다.    
> 핵심 정리  
> 메서드나 생성자를 작성할 때면 그 매개변수들에 어떤 제약이 있을지 생각해야 한다.  
> 그 제약들을 문서화하고 메서드 코드 시작 부분에서 명시적으로 검사해야 한다.  
> 이런 습관을 반드시 기르도록 하자  
> 그 노력은 유효성 검사가 실제 오류를 처음 걸러낼 때 충분히 보상받을 것이다.  
  
### 아이템 50 적시에 방어적 복사본을 만들라.  
자바는 안전한 언어다.  
하지만 아무리 자바라 해도 다른 클래스로부터의 침범을 아무런 노력 없이 막을 수 있는 것은 아니다.  
**클라이언트가 우리의 불변식을 깨뜨리려 혈안이 되어 있다고 가정하고 방어적으로 프로그래밍 해야 한다.**  
평범한 프로그래머도 순전히 실수로 우리의 클래스를 오작동하게 만들 수 있다.  
  
```java
public final class Period {
    private final Date start;
    private final Date end;

    /**
     * 
     * @param start : 시작 시간
     * @param end : 종료 시각 ; 시작 시각보다 뒤에 있어야 한다. 
     */
    public Period(Date start, Date end) { 
        if (start.compareTo(end) > 0) {
            throw new IllegalArgumentException(start + "가" + end + "보다 늦다.");
        }
        this.start = start;
        this.end = end;
    }
    public Date start() {
        return start;
    }
    public Date end() { 
        return end;
        }
}
```  
얼핏 이 클래스는 불변처럼 보이고, 시작 시각이 종료 시각보다 늦을 수 없다는 불변식이 무리없이 지켜질 것 같다.  
하지만 Date 가 가변이라는 사실을 이용하면, 어렵지 않게 불변식을 깨뜨릴 수 있다. 
  
```java
public class Period {

    public static void main(String[] args) {
        Date start = new Date(); 
        Date end = new Date();
        Period p = new Period(start, end);
        end.setYear(78); // p의 내부를 쉽게 변경했다. 
    }
    
}
```  
Date 대신 불변인 Instant를 사용하면 된다.(혹은 LocalDateIme 이나 ZondedDateTime 을 사용해도 된다.) -> 자바8 이후  
  
외부 공격으로 Period 인스턴스의 내부를 보호하려면 **생성자에서 받은 가변 매개변수 각각을 방어적으로 복사(defensive copy)해야 한다.**  
그런 다음 Period 인스턴스 안에는 원본이 아닌 복사본을 사용한다.  

```java
public class Period {
    public Period(Date start, Date end) {
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());

        if (this.start.compareTo(this.end) > 0) {
            throw new IllegalArgumentException(
                    this.start + "가" + this.end + "보다 늦다."
            );
        }
    }
}
```  
새로 작성한 생성자를 사용하면 앞선 공격은 더이상 Period 에 위협이 되지 않는다.  
**매개변수의 유효성을 검사 하기 전에 방어적 복사본을 만들고, 이 복사본으로 유효성을 검사한 점에 주목하자**  
순서가 부자연스럽지만 반드시 이렇게 작성해야 한다.  
멀티스레드 환경이라면, 원본 객체의 유효성을 검사한 후 복사본을 만드는 그 찰나의 취약한 순간에 다른 스레드가 본 객체를 수정할 위험이 있기 때문이다. (라운드빈 스케줄러 때문에 바로 처리가 안돼서 그런건가?)  
  
방어적 복사에 Date 의 clone 을 사용하지 않았다.  
clone 이 악의를 가진 하위 클래스의 인스턴스를 반환할 수도 있다.  
이런 공격을 막기 위해 **매개변수가 제 3자에 의해 확장될 수 있는 타입이라면 방어적 복사본을 만들 때 clone을 사용해서는 안된다**  
  
**가변 필드의 방어적 복사본을 반환하면 된다.** 
```java
public class Period {
    public Date start() {
        return new Date(start.getTime());
    }
    public Date end() {
        return new Date(end.getTime());
    }
}
```  
   
 > 핵심 정리  
 > 클래스가 클라언트로부터 받는 혹은 클라이언트로 반환하는 구성요소가 가변이라면 그 요소는 반드시 방어적으로 복사해야 한다.  
 > 복사 비용이 너무 크거나 클라이언트가 그 요소를 잘못 수정할 일이 없음을 싢뢰한다면 방어적 복사를 수행하는 대신 해당 구성요소를 수정했을 때의 책임이 클라이언트에 있음을 문서에 명시하자.  
   
---