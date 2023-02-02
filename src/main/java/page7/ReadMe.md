# 7장 람다와 스트림  
자바8 에서 함수형 인터페이스, 람다, 메서드 참조라는 개념이 추가되면서 함수 객체를 더 쉽게 만들 수 있게 되었다.  
이와 함께 API 까지 추가되어 데이터 원소의 시퀀스 처리를 라이브러리 차원에서 지원하기 시작했다.  
이번 장에서는 이 기능들을 효과적으로 사용하는 방법을 알아보겠다.   
  
## 아이템 42 익명 클래스보다는 람다를 사용해라  
예전에는 자바에서 함수 타입을 표현할 때, 추상 메서드를 하나만 담은 인터페이스(드물게는 추상 클래스)를 사용했다.  
이런 인터페이스의 인스턴스를 함수객체(function object)라고 하여, 특정 함수나 동작을 나타내는데 썼다.  
  
익명 클래스의 인스턴스를 함수 객체로 사용 -낡은 기법 
 ```java
public class Item42 {
    public static void main(String[] args) {
        // 익명 클래시의 인스턴스를 함수 객체로 사용 - 낡은 기법이다.
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add("0");
        }
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return Integer.compare(o1.length(), o2.length());
            }
        });
    }
}
```
전략 패턴처럼, 함수 객체를 사용하는 과거 객체지향 디자인 패턴에는 익명 클래스면 충분했다.  
이 코드에서는 Comparator 인터페이스가 정렬을 담당하는 추상 전략을 뜻한다.  
하지만 익명 클래스 방식은 코드가 너무 길기 때문에 자바는 함수형 프로그래밍에 적합하지 않았다.  
  
자바 8에 와서 추상 메서드 하나짜리 인터페이스는 특별한 의미를 인정받아 특별한 대우를 받아 **람다식**이 탄생하였다. 

위 코드를 람다 방식으로 바꾸면, 어떤 동작을 하는지 명확하게 드러난다.

```java
import java.util.Collections;

public class Item42 {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add("0");
        }

        Collections.sort(list,
                (s1, s2) -> Integer.compare(s1.length(), s2.length));
    }
}
```
여기서 람다의 타입은 Comparator<String>  
매개변수 (s1, s2)의 타입은 String,  
반환값의 타입은 int  
지만 코드에서 언급이 없다.  
우리 대신 컴파일러가 코드의 문맥을 읽고 타입을 추론해준 것이다. 컴파일러가 타입을 추론하지 못한다면 프로그래머가 타입을 명시해주어야 한다.   
타입 추론은 너무 복잡해서 다 알고있는 개발자는 없다.  
**타입을 명시해야 코드가 더 명확할 때만 제외하고, 람다의 모든 매개변수 타입은 생략하자 .  
> 람다의 타입은 제네릭을 통해 추론할 수 있다. 즉 컴파일러는 제네릭을 통해 타입을 추론한다.
  
람다 자리에 비교자 생성 메서드를 사용하면 이 코드를 더 간결하게 만들 수 있다.

```java
import java.util.Collections;
import java.util.Comparator;

public class Item42 {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add("0");
        }
        Collections.sort(list, Comparator.comparingInt(String::length));
        
        // 자바 8때 List 인터페이스에 추가된 sort 메서드를 활용하면 더 짧아진다. 

        list.sort(comparingInt(String::length));
    }
}
```

**람다는 이름도 없고 문서화도 못하기 때문에, 코드 자체로 동작이 명확히 설명되지 않거나, 코드 줄 수가 많아지면 람다를 사용하면 안된다.**  
람다는 한 줄일때 가장 좋고 세 줄을 넘어가면 안된다.  
 
---  
### 아이템 43 람다보다는 메서드 참조를 사용하라   
> 메서드 참조도 함수형 프로그래밍의 일부이다. 또한 람다보다 가독성이 더 좋다는 장점을 가지고 있다.  
  
람다가 익명 클래스보다 나은 점 중에서 가장 큰 특징은 간결함이다. 그런데 자바에서는 람다보다 더 간결하게 만드는 방법이 있으니, 바로 **메서드 참조(method reference)** 이다.   
> 예제에 설명된 것이 잘 이해가 되지 않는다. 추후에 다시 읽으면서 공부하도록 해야겠다.   
  
람다로 할 수 없는 일이라면 메서드 참조로도 할 수 없다. (애매한 예외가 있긴 함)  
람다로 구현했을 때 너무 길거나 복잡하다면 메서드 참조가 좋은 대안이 되어준다. 즉 람다로 작성할 코드를 새로운 메서드에 담은 다음, 람다 대신 그 메서드 참조를 사용하는 식이다.  
IDE 들은 보통 람다 대신에 메서드 참조를 이용하라고 한다.   
> 메서드 참조는 람다의 간단명료한 대안이 될 수 있다. **메서드 참조 쪽이 짧고 명확하다면 메서드 참조를 쓰고, 그렇지 않다면 람다를 사용해라**  
  

