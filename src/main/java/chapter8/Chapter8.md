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
### 아이템 51. 메서드 시그니처를 신중히 설계하라  
- **메서드 이름을 신중히 짓자.** 
  - 항상 표준 명명규칙 (아이템 68)을 따라야 한다.  
  - 긴 이름은 피하자(길어도 의미파악 되면 되는거 아닌가?)
  

- **편의 메서드를 너무 많이 만들지 말자.**  
  - 메서드가 너무 많은 클래스는 익히고, 사용하고, 테스트하고, 유지보수하기 힘들다. (하나의 메서드가 하나의 역할만 하는건?) 
  - 아주 자주 쓰일 경우에만 별도의 약칭 메서드를 두기 바란다(**확신이 서지 않으면 만들지 말자**)  
  
  
- **매개변수 목록은 짧게 유지하자 (최대 3개로)** 
  - 매개변수가 너무 많으면 기억하기 힘들다.  
  - **같은 타입의 매개변수 여러개가 연달아 나오는 경우가 특히 해롭다.**  
    - 사용자가 매개변수 순서를 기억하기 힘들고, 실수로 순서를 바꿔도 그대로 컴파일 되고 실행된다. (의도와 다를뿐)  
> 과도하게 긴 매개변수 목록을 짧게 줄여주는 기술 세가지!!  
> 1. 여러 메서드로 쪼갠다. 
>    - 쪼개진 메서드는 각각의 원래 매개변수 목록의 부분집합을 받는다. 
>    - 자칫 메서드가 너무 많아질 수 있지만, 직교성(orthogonality)을 높여 오히려 메서드 수를 줄여주는 효과도 있다. (java.util.List) 
>    
> 직교성이 높다 -> 공통점이 없는 기능들이 잘 분리되어 있다. 
> -> 이 부분 책 다시 읽어보자 아직 어려움... 
>  
> 2. 매개변수 여러개를 묶어주는 도우미 클래스를 만드는 것이다.!! 
>    - 일반적으로 이런 도우미 클래스는 정적 멤버 클래스(아이템 24)로 둔다.  
>    - 예를들어 카드게임 클래스를 만들때, 메서드를 호출할 때 카드의 숫자와 무늬를 뜻하는 두 매개변수를 항상 같은 순서로 전달할 것이다.  
>    - 따라서 이 둘을 묶는 도우미 클래스를 만들어 하나의 매개변수로 주고받으면 API는 물론 클래스 내부 구현도 깔끔해 진다.  
>   
> 3. 앞서의 두 기법을 혼합한 것으로, 객체 생성에 사용한 빌더 패턴(아이템 2)을 메서드 호출에 으용한다고 보면 된다.  
  
  
**매개변수의 타입으로는 클래스보다는 인터페이스가 더 낫다.** 
매개변수로 적합한 인터페이스가 있다면 그 인터페이스를 직접 사용하자.(다형성인가? )  
예를 들어 메서드에 HahsMap을 넘길 일은 전혀 없으니 대신 Map을 사용하자.   
그렇다면 HahsMap뿐 아니라, TreeMap, ConcurrentHashMap, TreeMap의 부분맵 등 어떤 Map 구현체도 인수로 건낼 수 있다.  
심지어 아직 존재하지 않는 Map도 가능하다.  
인터페이스 대신 클래스를 사용하면 클라이언트에게 특정 구현체만 사용하도록 제한하는 꼴이다.   
  
**boolean보다 원소 2개짜리 열거 타입이 낫다**(메서드 이름상 boolean을 받아야 의미가 명확할 때는 예외)  
열거 타입을 사용하면 코드를 읽고 쓰기가 더 쉬워진다.  
예를 들어 화씨온도(Fahrenheit)와 섭씨온도(Celsius)를 원소로 정의한 열거 타입이다.  
```java
public enum TemperatureScale {FAHRENHEIT, CELSIUS}  
```  
  
온도계 클래스의 정적 팩터리 메서드가 이 열거 타입을 입력받아 적합한 온도계 인스턴스를 생성해준다고 해보자.  
확실히 Thermometer.newInstance(true) 보다는  
Thermometer.newInstance(TemperatureScale.CELSIUS)가 하는 일을 훨씬 명확이 알려준다.  
나중에 다른 온도를 추가해야 한다면, 열거타입에 온도를 추가해주기만 하면 된다.  
  
### 아이템 52. 다중정의는 신중히 사용하라 (오버로딩)
다음은 컬렉션을 집합, 리스트, 그 외로 구분하고자 만든 프로그램이다.   
  
출력 결과는 그외 만 세번 출력한다.  
그 이유는 : 다중정의된 세 classify 중 **어느 메서드를 호출할지가 컴파일타임에 정해지기 때문**이다.  
  
컴파일 타임에 for문 안의 c 는 항상 Collection<?> 타입이다.  
런타임에는 타입이 매번 달라지지만, 호출할 메서드를 선택하는데는 영향을 주지 못한다.   
따라서 컴파일 타입의 매개변수 타입을 기준으로 항상 세 번째 메서드만 호출한다.  
  
이처럼 직관과 어긋나는 이유는  
**재정의한 메서드는 동적으로 선택되고 (오버라이딩)**  
**다중정의한 메서드는 정적으로 선택되기 때문이다.(오버로딩)**  
  
다음 코드는 이러한 상황을 구체적으로 보여준다.  
```java
class Wine {
String name(){ 
    return "포도주"; 
    }
}

class SparklingWine extends Wine {
    @Override 
    String name() { 
    return "발포성 포도주"; 
    }
}

class Champagne extends SparklingWine { 
    @Override 
    String name() { 
        return "샴페인"; 
    }
}

public class Overriding {
    public static void main(String[] args) {
        
        List<Wine> wineList = List.of(
            new Wine(), new SparklingWine(), new Champagne());
        for (Wine wine : wineList) {
            System.out.printIn(wine.name());
        }
        
    } 
}
```  
위 코드를 실행하면 포도주, 발포성 포도주, 샴페인을 차례대로 출력한다.   
for문에서의 컴파일 타임 타입이 모두 Wine인 것에 무관하게 항상 '가장 하위에서 정의한' 재정의 메서드가 실행되는 것이다.  
  
한편 다중정의된 메서드 사이에서는 객체의 런타임 타입은 전혀 중요치 않다.  
선택은 컴파일 타임에, 오직 매개변수의 컴파일 타임 타입에 의해 이루어진다.   
  
CollectionClassifier의 예에서 프로그램의 원래 의도는 매개변수의 런타임 타입에 기초해 적절한 다중정의 메서드로 자동 분배하는 것이었다.  
Wine의 예시와 똑같이 말이다.  
하지만 다중정의는 이렇게 동작하지 않는다.  
  
이 문제는 CollectionClassifier의 모든 classify 메서드를 하나로 합친 후 instanceof 로 명시적으로 검사하면 말끔히 해결된다.  
```java
public class Classifier {
    public static String classify(Collection<?> c) {
        return c instanceof Set ? "집합" : 
                c instanceof List ? "리스트" : "그 외";
    }
}
```  
이렇게 하면 처음 의도한 대로 출력이 잘 된다. (Item52 클래스 확인해서 실행해보기)  
  
프로그래머에게는 재정의가 정상적인 동작 방식이고, 다중정의가 예외적인 동작으로 보일 것이다.  
즉 재정의한 메서드는 프로그래머가 기대한대로 동작하지만, CollectionsClassifier 예에서처럼 다중정의한 메서드는 이러한 기대를 가볍게 무시한다.  
**다중정의가 혼동을 일으키는 상황을 피해야 한다.**  
**안전하고 보수적으로 가려면, 매개변수 수가 같은 다중정의는 만들지 말자**  
**다중정의하는 대신 메서드 이름을 다르게 지어주는 길도 항상 열려있으니 말이다.**
  
한편 생성자는 이름을 다르게 징르 수 없으니, 두 번째 생성자부터는 무조건 다중정의가 된다.  
하지만 정적 팩터리라는 대안을 활용할 수 있는 경우가 많다.(아이템 1)  
또한 생성자는 재정의할 수 없으니, 다중정의와 재정의가 혼용될 걱정은 하지 않아도 된다.  
  
---
이후에는 함수형 프로그래밍... 람다스트림 더 공부하고 읽어보자  
  
> 핵심 정리  
> 프로그래밍 언어가 다중정의를 허용한다고 해서 다중정의를 꼭 활용하라는 뜻은 아니다.  
> 일반적으로 매개변수의 수가 같을 때는 다중정의를 피하는게 좋다.  
> 상황에 따라, 특히 생성자라면 이 조언을 따르기가 불가능할 수 있다.  
> 그럴 때는 헷갈릴만한 매개변수는 형변환하여, 정확한 다중정의 메섣드가 선택되도록 해야 한다.  
> 이것이 불가능하면, 예컨대 기존 클래스를 수정해 새로운 인터페이스를 구현해야 할 때는 같은 객체를 입력받는 다중 정의 메서드들이 모두 동일하게 동작하도록 만들어야 한다.  
> 그렇지 못하면 프로그래머들은 다중정의된 메서드나 생성자를 효과적으로 사용하지 못할 것이고, 의도대로 동작하지 않는 이유를 이해하지도 못할 것이다.
  
---  
### 아이템 53. 가변인수는 신중히 사용하라   
가변인수 메서드는 명시한 타입의 인수를 0개이상 받을 수 있다.  
가변인수 메서드를 호출하면, 가장 먼저 인수의 개수와 길이가 같은 배열을 만들고, 인수들을 이 배열에 저장하여 가변인수 메서드에 전너준다.    
```java
public class Item53 {
    static int sum(int... args) {
        int sum = 0; 
        for (int arg : args) {
            sum += arg;
        }
        return sum;
    }
}
```  
인수가 1개 이상이어야 할 때도 있다. 
예컨대 최솟값을 찾는 메서드인데, 인수를 0개만 받을 수 있도록 설계하는건 좋지 않다. 
인수 개수를 확인해서 0개이면 throw 를 하도록 하자 .  
```java
class Item53 {
    static int min(int ..args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("인수가 1개 이상 필요합니다.");
        }
        int min = args[0];
        for (int i = 0; i < args.length; i++) {
            if (args[i] < min) {
                min = args[i];
            }
        }
        return min; 
    }
}
```  
이 방식에는 문제 몇 가지가 있다.  
가장 심각한 문제는 인수를 0개 넣어 호출하면 컴파일타임이 아닌 런타임에 실패한다는 것이다.  
  
다행히 훨씬 나은 방법이 있다.  
매개변수를 2개 받도록 하면 된다.  
즉 첫 번째는 평범한 매개변수를 받고, 가변인수는 두 번째로 받으면 앞서의 문제가 해결된다.  
```java
public class Item53 {
    static int min(int firstArg, int ... remainingArgs) {
        int min = firstArg;
        for (int arg : remainingArgs) {
            if(arg < min) {
                min = arg;
            }
        }
        return min;
    }
}
```  
  
성능에 민감한 경우 가변인수가 걸림돌이 될 수 있다.  
가변인수 메서드는 호출될 때마다 배열을 새로 하나 할당하고 초기화 한다.  
  
다중정의를 활용하면 해결할 수 있다.  
  
EnumSet의 정적 팩터리도 이 기법을 사용해 열거 타입 집합 생성 비용을 최소화 한다.  
(아이템 36)
  

> 핵심 정리  
> 인수 개수가 일정하지 않은 메서드를 정의해야 한다면 가변 인수가 반드시 필요하다.  
> 메서드를 정의할 때 필수 매개변수는 가변인수 앞에 두고, 가변 인수를 사용할 때는 성능 문제까지 고려하자.  
  
### 아이템 54. null 이 아닌, 빈 컬렉션이나 배열을 반환하라  
다음은 주변에서 흔히 볼 수 있는 메서드이다.  - **절대 따라하지 말것!!**  
```java
public class Item53 {
    private final List<Cheese> cheeseList=...;

    /**
     * @return 매장 안의 모든 치즈 목록을 반환한다. 
     * 단, 재고가 하나도 없다면 null을 반환한다. 
     */
    public List<Cheese> getCheeses() {
        return cheeseList.isEmpty() ? null
                : new ArrayList<>(cheeseList);
    }
}
```  
사실 재고가 없다고 특별한 취급을 할 필요는 없다.   
그러나 이렇게 null을 반환하면 null을 처리하는 추가 코드를 작성해야 한다.  
그러므로 null이 아닌 빈 컬렉션은 반환하도록 해야한다.  
  
떄로는 빈 컨테이너를 반환하는데 비요이 드니 null을 반환해야 한다고 주장하는 쪽이 있다.  
그러나 이는 틀린 주장이다.  
1. 성능 분석 결과 이 할당이 성능 저하의 주범이라고 확인되지 않는 한, 이정도의 성능 차이는 신경 쓸 수준이 못된다.  
2. 빈 컬렉션과 배열은 굳이 새로 할당하지 않고도 반환할 수 있다. 
   - 다음과 같이 빈 컬렉션을 반환할 수 있다.

```java
public class Item54 {
    public List<Cheese> getCheeses() {
        return new ArrayList<>(cheeseInStock);
    }
}
```  
  
배열을 사용할 때도 마찬가지이다.  
크기가 0인 배열을 반환하면 된다.  
```java
public class Item54 {
    public Cheese[] getCheese() {
        return cheesesInStock.toArray(new Cheese[0]);
    }
}
```  
이 방식이 성능을 떨어뜨릴 것 같다면, 길이가 0인 배열을 미리 선언하는 것으로 해결할 수 있다.  
  
```java
public class Item54 {
    private static final Cheese[] EMPTY_CHEESE_ARRAY = new Cheese[0];
    
    public Cheese[] getCheeses() {
        return cheesesInStock.toArray(EMPTY_CHEESE_ARRAY);
    }
}
```  
그러나 단순히 성능을 개선할 목적이면 toArray에 넘기는 배열을 미리 생성하지 말아아, 오히려 성능이 떨어진다는 연구결과도 있다.  
  
> 핵심 정리  
> **null 이 아닌, 빈 배열이나 컬렉션을 반환하라. null을 반환하는 API는 사용하기 어렵고, 오류 처리 코드도 늘어난다.  
> 그렇다고 성능이 좋은 것도 아니다.    
  
### 아이템 55. 옵셔널 반환은 신중히 하라   
자바 8 이전에는 메서드가 특정 조건에서 값을 반환할 수 없을 때 취할 수 있는 선택지가 두 가지가 있었다.  
- 예외를 던진다. 
  - 예외는 진짜 필요한 상황에서만 해야한다. 
- null을 반환한다.  
  - null 처리 추가 코드를 작성해야 한다.
  
  
자바 버전이 8로 올라가면서 또 하나의 선택지가 생겼다. 
- Optional<T>  
  
Optional<T> 는 null 이 아닌 T 타입 참조를 하나 담거나, 혹은 아무것도 담지 않을 수 있다.  
아무것도 담지 않은 옵셔널은 비었다 라고 표현한다.  
옵셔널은 원소를 최대 1개 가질 수 있는 '불변'컬렉션이다.  
보통은 T를 반환해야 하지만, 아무것도 반환하지 않아야 할 때 T 대신 Optional<T>를 반환하도록 선언하면 된다.  
옵셔널을 반환하면 예외를 던지는것 보다 유연성이 높고, 사용하기 쉬우며, null을 반환할때보다 오류 가능성이 작다.  

컬렉션에서 최댓값을 구한다 (컬렉션이 비었으면 예외를 던진다.)

```java
import java.util.Objects;

public class Item55 {
    public statkc<E extends Comparable<E>> E

    max(Collection<E> c) {
        if (c.isEmpty()) {
            throw new IllegalArgumentException("빈 컬렉션")
        }
        E result = null;
        for (E e : c) {
            if (result == null || e.compareTo(result) > 0) {
                result = Objects.requireNonNullElse(e);
            }
        }
        return result;
    }
}
```
이 메서드에 빈 컬렉션을 건내면 IllegalArgumentException을 던진다. 
Optional로 반환하도록 변경해보자.

```java
import java.util.Optional;

public class Item55 {
    public static <E extends Comparable<E>>
    Optional<E> max(Comllection<E> c) {
        if (c.isEmpty()) {
            return Optional.empty();
        }
        E result = null;
        for(E e : c) {
            if (result == null || e.compareTo(result) > 0) {
                result = Object.requireNoneNull(e);
            }
            return Optional.of(result);
        }
    }
}
```
보다시피 Optional로 반환하는 것은 어렵지 않다.  
적절한 정적 팩터리를 사용해 옵셔널 생성하기만 하면 된다.  
  
**다만 Optional을 반환하는 메서드에는 절대 null을 반환하면 안된다.** 
-> 옵셔널을 도입한 취지를 완전히 무시하는 행위다.  
  
스트림의 종단 연산중 상당수가 옵셔널을 반환한다.   
앞의 max 메서드를 스트림 버전으로 바꾼다면 Stream의 max 연산이 우리에게 필요한 옵셔널을 생성해줄 것이다.

```java
import java.util.Comparator;

public class Item55 {
    public static <E extends Comparable<E>> Optional<E> max(Collection<E> c) {
        return c.stream().max(Comparator.naturalOrder());
    }
}
```
    
그렇다면 예외를 던지는 대신 옵셔널 반환을 선택해야 하는 기준은?  
**옵셔널은 검사 예외와 취지가 비슷하다.**  
반환값이 없을 수도 있다고 API 사용자에게 명확히 알려주는 역할을 한다.  
  
메서드가 옵셔널을 반환한다면 클라이언트는 값을 받지 못했을 때 취할 행동을 선택해야 한다.  
그중 하나는 기본값을 설정하는 방법이다.  
  
혹은 상황에 맞는 예외를 던진다.  
  
여전히 적합한 메서드를 찾지 못했다면 isPresent 메서드를 살펴보자.  
안전밸브 역할의 메서드로, 옵셔널이 채워져 있으면 true, 비워져 있으면 false를 반환한다.  
  
이 메서드는 원하는 모든 작업을 수행할 수 있지만 신중히 사용해야 한다.  
앞에 언급한. filter, map, flatMap, ifPresent 등으로 대체할 수 있고, 더 명확하고 짭은 코드가 될것이다.  
  
반환값으로 옵셔널을 사용한다고 해서 무조건 득이 되는 것은 아니다.  
**컬렉션, 스트림, 배열, 옵셔널 같은 컨테이너 타입은 옵셔널로 감싸면 안된다.** 
빈 Optional<List<T>>를 반환하는 것보다, 빈 List<T>를 반환하는 것이 더 좋다.  
  
그렇다면 언제 Optional로 반환해야 할까?  
**결과가 없을 수 있으며, 클라이언트가 이 상황을 특별하게 처리해야 한다면 Optional<T> 를 반환한다.**  
Optional을 사용하는 것은 한단계 싸는 것이므로, 성능이 중요한 상황에서 Optional은 맞지 않을 수 있다.  
  
박싱된 기본 타입을 Optional로 한번 더 박싱을 하게 되면, 기본타입보다 무거워진다.  
그래서 자바 API 설계자는 OptionalInt, OptionalLong, OptionalDouble을 전부했다.  
이 옵셔널들도 Optional<T> 가 제공하는 메서드들을 거의 다 제공한다.  
이렇게 대체재까지 있으니, **박싱된 기본 타입을 담은 옵셔널을 반환하는 일은 없도록 하자**  
단 덜 중요한 기본타입인 Boolean, Byte, Character, Short, Float은 예외일 수 있다.  
  
**Optional을 컬렉션의 키, 값, 원소나 배열의 원소로 사용하는게 적절한 상황은 거의 없다** 
Map이 Optional이라고 생각해보자.  
맵 안에 키가 없다는 사실을 나타내는 방법이 두가지가 된다.  
하나는 키 자체가 없다는 것이고,  
하나는 키의 값이 비어있다는 것이다.  
쓸데없이 복잡성만 높여서, 혼란과 오류 가능성을 높인다.  
  
> 핵심 정리  
> 값을 반환하지 못할 가능성이 있고, 호출할 때마다 반환값이 없을 가능성을 염두에 둬야 하는 메서드라면, 옵셔널을 반환해야 할 상황일 수 있다.  
> 하지만 옵셔널 반환에는 성능 저하가 뒤따르니, 성능에 민감한 메서드라면 null을 반환하거나 예외를 던지는 편이 나을 수 있다.  
> 그리고 옵셔널을 반환값 이외의 용도로 쓰는 경우는 매우 드물다.  


  
### 아이템 56. 공개된 API 요소에는 항상 문서화 주석을 작성하라  
API를 쓸모 있게 하려면 잘 작성된 문서도 곁들여야 한다.  
- 아직 할 단계가 아닌것 같다. 
- 더 공부해서 2회독차에 다시 제대로 정리해보자.  