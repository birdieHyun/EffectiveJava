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
**타입을 명시해야 코드가 더 명확할 때만 제외하고, 람다의 모든 매개변수 타입은 생략하자.**
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
   
 
### 아이템 44 표준 함수형 인터페이스를 사용하라  
자바가 람다를 지원하면서, 상위 클래스의 기본 메서드를 재정의해 원하는 동작을 수현하는 템플릿 메서드 패턴의 매력이 크게 줄었다.  
**필요한 용도에 맞는게 있다면, 직접 구현하지 말고, 표준 함수형 인터페이스를 활용하라** 그러면 API 가 다루는 개념의 수가 줄어들어 익히기 더 쉬워진다.  
  
|인터페이스|함수 시그니처|예|
|:---|:---:|:---:|
|UnaryOperator<T>|T apply(T t)|String::toLowerCase|
|BinaryOperator<T>|T apply(T t1, T t2)|BigInteger::add|
| Predicate<T>    |boolean test(T t)|Collection::isEmpty|
| Function<T, R>  |R apply(T t)|Arrays::asList|
| Supplier<T>     |T get()|Instant::now|
| Consumer<T>     |void accept(T t)|System.out::println|  
  
기본 인터페이스는 기본 타입인 int, long, double 용으로 각 3개씩 변형이 생겨난다.  
그 이름도 기본 인터페이스 앞에 해당 기본 타입 이름을 붙여 지었다.  
ex) int 를 받는 Predicate = IntPredicate  
  
기본 인터페이스들의 변형까지 고려하면 너무 많아서 전부를 외우기는 힘들다. (총 43개)  
하지만 실무에서 자주 쓰이는 함수형 인터페이스 중 상당수를 제공하며, 필요할 때 찾아 쓸 수 있을 만큼은 범용적인 이름을 사용했다.  
  
표준 함수형 인터페이스 대부분은 기본 타입만 지원한다.   
그렇다고 **기본 함수형 인터페이스에 박싱된 기본 타입을 넣어 사용하지는 말자** ?? 이 부분 이해가 잘 안된다.  
    
혹시 직접 함수형 인터페이스를 만든다면 항상 @FunctionalInterface 애너테이션을 사용해야 한다.  
> 핵심 정리 : 자바8 이후로 자바가 람다를 지원하기 때문에 API 를 설계할 때 람다도 염두해두어야 한다.   
  
### 아이템 45 스트림은 주의해서 사용하라  
스트림 API 는 다량의 데이터 처리 작업 (순차적이든 병렬적이든)을 돕고자 자바8에 추가되었다.  
이 API 에서 제공하는 추상 개념 중 핵심은 두가지다.  
1. 스트림은 데이터 원소의 유한 혹은 무한 시퀀스를 뜻한다.
2. 스트림 파이프라인은 이 원소들로 수행하는 연산 단계를 표현하는 개념이다.   
  
스트림 파이프라인은 소스 스트림에서 시작해 종단 연산으로 끝나며, 그 사이에 하나 이상의 중간 연산이 있을 수 있다.  
각 중간 연산은 스트림을 어떠한 방식으로 변환한다.   
  
스트림 파이프라인은 지연평가된다. 평가는 종단 연산이 호출될 때 이뤄지며, 종단 연산에 쓰이지 않는 데이터 원소는 계산에 쓰이지 않는다.  
이러한 지연 평가가 무한 스트림을 다룰 수 있게 해주는 열쇠다.  

스트림 API 는 다재다능하여 사실상 어떠한 계산이라도 해낼 수 있다. 하지만 할 수 있다는 뜻히지, 해야한다는 뜻히 아니다.  
스트림을 제대로 사용하면 프로그램이 짧고 깔끔해지지만, 잘못 사용하면 읽기 어렵고 유지보수도 힘들어진다.  
스트림을 언제 써야 하는지 규정하는 확고부동한 규칙은 없지만, 참고할 만한 노하우는 있다.  
  
```java
package page7;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Item45 {
    public static void main(String[] args) throws FileNotFoundException {

        File dictionary = new File(args[0]);
        int minGroupSize = Integer.parseInt(args[1]);

        Map<String, Set<String>> groups = new HashMap<>();
        try (Scanner s = new Scanner(dictionary)) {
            while (s.hasNext()) {
                String word = s.next();
                groups.computeIfAbsent(alphabetize(word),
                        (unused) -> new TreeSet<>()).add(word);
            }
        }
        for (Set<String> group : groups.values()) {
            if (group.size() >= minGroupSize) {
                System.out.println(group.size() + " : " +  group);
            }
        }
    }
    
    private static String alphabetize(String s) {
        char[] a = s.toCharArray();
        Arrays.sort(a);
        return new String(a);
    }
}

```  
위 코드는 사전에 단어를 삽입할 때, 자바8에 추가된 computeIfAbsent 메서드를 사용했다.  
이 메서드는 맵 안에 키가 있는지 찾은 다음, 있으면 단순히 그 키에 매핑된 값을 반환하고, 키가 없으면 건네진 함수 객체를 키에 적용하여 값을 계산해낸 다음 그 키와 값을 매핑하고 계산된 값을 반환한다.  
이처럼 computeIAbsent 를 사용하면 각 키에 다수의 값을 매핑하는 맵을 쉽게 구현할 수 있다.  

다음 코드는 과하게 스트림을 사용한 경우다.  
  
```java
// 모든 것을 스트림으로 처리 
```  
  
**스트림을 과용하면 프로그램이 읽거나 유지보수하기 어려워진다.**  
  
**다음과 같이 스트림을 적절히 활용하면 깔끔하고 명료해진다.**

```java
import java.util.stream.Stream;

public class Anagrams {
    public static void main(String[] args) {

        Path dictionary = Path.get(args[0]);
        int minGroupSize = Integer.parseInt(args[1]);

        try (Stream<String> words = Files.lines(dictionary)) {
            words.collect(groupingBy(word -> alphabetize(word)))
                    .values().stream()
                    .filter(group -> group.size() >= minGroupSize)
                    .forEach(g -> System.out.println(group.size() + " : " + group ));
        }


    }

    private static String alphabetize(String s) {
        char[] a = s.toCharArray();
        Arrays.sort(a);
        return new String(a);
    }
}
```
  
스트림을 전에 본 적이 없더라도 이 코드는 이해하기 쉬울 것이다. 
try 블록에서 사전 파일을 열고, 파일의 모든 라인으로 구성된 스트림을 얻는다.  
스트림 변수의 이름을 words 로 지어 스트림 안의 각 원소가 단어(word) 임을 명확히 했다.  
  
> 람다 매개변수의 이름은 주의해서 정해야 한다. 
> **람다에서는 타입 이름을 자주 생략하므로 word, group 이와 같이 명확히 매개변수 명을 정해야, 스트림 파이프라인의 가독성이 유지된다.
> 한편 단어의 철자를 알파벳순으로 정렬하는 일은 별도 메서드인 alphabetize 에서 수행했다. 
> 연산에 적절한 이름을 지어주고, 세부 구현을 주 프로그램 로직 밖으로 빼내 전체적인 가독성을 높였다.  
> **도우미 메서드를 적절히 활용하는 일의 중요성은 일반 반복문에서 보다 스트림 파이프라인에서 훨씬 크다**
  
  
char 값을 처리할 땐 스트림을 삼가는 편이 낫다.  
```java
"Hello world!".chars().forEach(System.out::println)
```
위의 스트림은 H,e,l,l 처럼 char 형태를 반환하는 것이 아닌,  
72101107107... 처럼 int 값을 반환한다.   
형변환을 해주면 해결할 수 있지만, char 값들은 스트림을 삼가는 편이 좋다.  
  
반복문은 stream 으로 바꾸도록 노력은 하되, 가독성이 떨어진다면 기존 코드를 유지하자.  
  
> 핵심 정리   
> 스트림을 사용해야 멋지게 처리할 수 있는 일이 있고, 반복 방식이 더 알맞은 일도 있다.  
> 가장 좋은 작업은 두개를 적절히 섞는 것이다.  
> **스트림과 반복문 중 어느쪽이 나은지 확신하기 어렵다면 둘 다 해보고 더 나은 쪽은 택하라**  
  
