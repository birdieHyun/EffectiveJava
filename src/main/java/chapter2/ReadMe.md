# 2장 객체 생성과 파괴 

> 객체를 만들어야 할 때와, 만들지 말아야 할 때를 구분하는 법, 올바른 객체 생성 방법과 불필요한 생성을 피하는 방법,
> 제때 파괴됨을 보장하고 파괴 전에 수행해야 할 정리 작업을 괄니하는 요령을 알아본다.

### 아이템 01 - 생성자 대신 정적 팩터리 메서드를 고려하라 
클라이언트가 클래스의 인스턴스를 얻는 전통적인 수단은 public 생성자다.  
클래스는 생성자와 별도로 정적 팩터리 메서드를 제공할 수 있다.  
> 정적 팩터리 메서드는 디자인 패턴에서의 팩터리 메서드와 다르다.  
  
**정적 팩터리 메서드의 장점**  
1. 이름을 가질 수 있다. 
   - 생성자에 넘기는 매개변수와, 생성자 자체만으로는 반환될 객체의 특성을 제대로 설명하지 못한다. 
   - 반면 정적 팩터리 메서드는 이름만 잘 지으면, 반환될 객체의 특성을 쉽게 묘사할 수 있다. (코드보기)
```java
public class Application {
    public static void main(String[] args) {
        Item01 item011 = new Item01("order", true); // 이런식으로 생성자만으로는 반환될 객체의 특성을 잘 설명하지 못한다. 
        Item01 item01 = new Item01(); // 기본생성자를 통해 인스턴스를 생성하고, 
        Item01 commonOr = item01.makeCommonOrder("order", true);  // 인스턴스.정적팩터리메서드 를 통해 객체의 특성을 잘 설명하는 객체를 반환한다. 
        Item01 urgentOrder = item01.makeUrgentOrder("order", true);
    }
}
```
2. 호출될 때마다 인스턴스를 새로 생성하지는 않아도 된다. 
   - 인스턴스를 통제하면 클래스를 싱글톤으로 만들수도, 인스턴스화 불가로 만들수도 있다. 

3. 반환 타입의 하위 타입 객체를 반환할 수 있는 능력이 있다. 
   - 인터페이스를 정적 팩터리 메서드의 반환 타입으로 사용하는 인터페이스 기반 프레임워크(아이템 20)의 핵심 기술이다.  
  
  
**정적 팩터리 메서드의 단점**  
1. 상속을 하려면 public이나 protected 생성자가 필요하니, 정적 팩터리 메서드만 제공하면 하위 클래스를 만들 수 없다.  
  
2. 정적 팩터리 메서드는 프로그래머가 찾기 힘들다.  
   - 생성자처럼 API 설명에 명확히 드러나지 않으니, 사용자는 정적 팩터리 메서드 방식 클래스를 인스턴스화할 방법을 알아내야 한다.  
  
다음은 정적 팩터리 메서드에 흔히 사용하는 명명 방식들이다. 
- from : 매개변수를 하나 받아서 해당 타입의 인스턴스를 반환하는 형변환 메서드  
  
- of : 여러 매개변수를 받아 적합한 타입의 인스턴스를 반환하는 집계 메서드  
  
- valueOf: from과 of의 더 자세한 버전  
  
- instance 혹은 getInstance : (매개변수를 받는다면) 매개변수로 명시한 이스턴스를 반환하지만, 같은 인스턴스임을 보장하지는 않는다.  
  
- create 혹은 newInstance : instance 혹은 getInstacne와 같지만, 매번 새로운 인스턴스를 생성해 반환함을 보장한다.  
  
- getType : getInstance와 같으나, 생성할 클래스가 아닌 클래스에서 팩터리 메서드를 정의할 때 쓴다. ("Type"은 팩터리 메서드가 반환할 객체의 타입이다.)  
  
- newType : newInstance와 같으나, 생성할 클래스가 아닌 클래스에서 팩터리 메서드를 정의할 때 쓴다. ("Type"은 팩터리 메서드가 반환할 객체의 타입이다.)   
  
- type : getType과 newType의 간결한 버전  
  
> 핵심 정리  
> 정적 팩터리 메서드와 public 생성자는 각자의 쓰임새가 있으니, 상대적인 장단점을 이해하고 사용하는 것이 좋다.  
> 그렇다고 하더라도 정적 팩터리를 사용하는게 유리한 경우가 더 많으므로, 무작정 public 생성자를 제공하던 습관이 있다면 고치자.  
  
---  
### 아이템02. 생성자에 매개변수가 많다면 빌더를 고려하라   
정적 팩터리와 생성자에는 똑같은 제약이 하나 있다.  
선택적 매개변수가 많은 경우 적절히 대응하기 어렵다는 점이다.  
예를들어 영양정보에는 1회 내용량, 총n회 제공량, 1회 제공량당 칼로리 같은 필수항목 몇개와  
총 지방, 트랜스지방, 포화지방 ... 같은 총 20개가 넘는 선택항목으로 이루어져 있는데, 대부분의 선택항목의 값은 0이다.  
  
이럴때 프로그래머들은 점층적 생성자 패턴을 즐겨 사용한다.  
필수 매개변수만 받는 생성자, 선택 매개변수 1개 받는 생성자, 2개 받는 생성자...  선택 매개변수를 전부 받는 생성자까지 만든다.  
```java
public class NutritionFacts {
    private final int servingSize;       // 필수
    private final int servings;          // 필수
    private final int calories;          // 선택
    private final int fat;               // 선택
    private final int sodium;            // 선택
    private final int carbohydrate;      // 선택

   public NutritionFacts(int servingSize, int servings) {
      this(servingSize, servings, 0);
   }

   public NutritionFacts(int servingSize, int servings, int calories) {
       this(servingSize, servings, calories, 0);
   }

   public NutritionFacts(int servingSize, int servings, int calories, int fat) {
       this(servingSize, servings, calories, fat, 0);
   }

   public NutritionFacts(int servingSize, int servings, int calories, int fat, int sodium) {
      this(servingSize, servings, calories, fat, sodium, 0);
   }

   public NutritionFacts(int servingSize, int servings, int calories, int fat, int sodium, int carbohydrate) {
      this.servingSize = servingSize;
      this.servings = servings;
      this.calories = calories;
      this.fat = fat;
      this.sodium = sodium;
      this.carbohydrate = carbohydrate;
   }
}
```  
  
이 클래스의 인스턴스를 만들려면 원하는 매개변수를 모두 포함한 생성자 중 가장 짧은 것을 골라 호출하면 된다.  
  
NutritionFacts cocaCola = new NutritionFacts(240, 8, 100, 0 35, 27);  
  
이런 생성자는 사용자가 원치 않는 값도 넣어주어야 하는데, 값은 0으로 지정해주어야 한다.  
  
요약하면, **점층적 생성자 패턴도 쓸 수는 있지만, 매개변수 개수가 많아지면 클라이언트 코드를 작성하거나 읽기 어렵다.**  
프로그래머가 실수로 매개변수의 순서를 바꾸어도, 컴파일러는 알아차리지 못하고, 런타임 에러가 발생할 것이다.  
  
이번에는 두 번째 방법인 자바빈즈 패턴이다.  
매개변수가 없는 생성자로 객체를 만든 후, 세터를 사용하여 매개변수의 값을 설정하는 방식이다. 
그러나 자바빈즈 패턴도 단점을 가지고 있다.
**객체 하나를 만드려면, 메서드를 여러개 호출해야 하고, 객체가 완전히 생성되기 전까지는 일관성이 무너진 상태에 놓이게 된다.**
**자바빈즈 패넡에서는 클래스를 불변으로 만들 수 없으며**, 스레드 안전성을 얻으려면 프로그래머가 추가 작업을 해주어야 한다.  
  
이러한 단점을 보완하기 위해 객체를 수동으로 얼리는 방법이 있는데, 사용하기 어려워 잘 사용하지 않는다.  
  
다행히 세 번째 대안이 있다.  
점층적 생성자 패턴의 안전성과, 자바빈 패턴의 가독성을 겸비한 **빌더 패턴**이다.  

클라이언트는 필요한 객체를 직접 만드는 대신, 필수 매개변수만으로 생성자(혹은 정적 팩터리 메서드)를 호출해 빌더 객체를 얻는다.  
그런 다음 빌더 객체가 제공하는 일종의 세터 메서드들로 원하는 선택 매개변수들을 설정한다.  
마지막으로 매개변수가 없는 build 메서드를 호출해 드디어 우리에게 필요한 객체를 얻는다.  
빌더는 생성할 클래스 안에 정적 멤버 클래스로 만들어두는게 보통이다.  
  
```java
public class NutritionFacts {
    private final int servingSize;
    private final int serving;
    private final int calories;
    private final int fat;
    private final int sodium;
    private final int carbohydrate;

    public static class Builder {
        // 필수 매개변수
        private final int servingSize;
        private final int servings;

        // 선택 매개변수 
        private int calories = 0;
        private int fat = 0;
        private int sodium = 0;
        private int carbohydrate = 0;

        public Builder(int servingSize, int servings) {
            this.servingSize = servingSize;
            this.servings = servings;
        }

        public Builder calories(int val) {
            calories = val;
            return this;
        }

        public Builder fat(int val) {
            fat = val;
            return this;
        }

        public Builder sodium(int val) {
            sodium = val;
            return this;
        }

        public Builder carbohydrate(int val) {
            carbohydrate = val;
            return this;
        }

        public NutritionFacts build() {
            return new NutritionFacts(this);
        }
    }

    private NutritionFacts(Builder builder) {
        servingSize = builder.servingSize;
        serving = builder.servings;
        calories = builder.calories;
        fat = builder.fat;
        sodium = builder.sodium;
        carbohydrate = builder.carbohydrate;
    }
}
```  
  
NutritionFacts 클래스는 불변이며, 모든 매개변수의 기본값들을 한곳에 모아뒀다.  
빌더의 세터 메서드들은 빌더 자신을 반환하기 때문에 연쇄적으로 호출할 수 있다.  
이런 방식을 메서드 홏루이 흐르듯 연결된다는 뜻으로 플루언트 API, 혹은 메서드 연쇄라 한다.  
다음은 이 클래스를 사용하는 클라이언트 코드의 모습이다.  
  
```java
public class Item2{
   public static void main(String[] args) {
      NutritionFacts cocaCola = new NutritionFacts.Builder(240, 8).calories(100).sodium(35).carbohydrate(27).build();
   }
}
```  
이 클라이언트 코드는 쓰기 쉽고, 무엇보다도 읽기 쉽다.  
**빌더 패턴은 (파이썬과 스칼라에 있는) 명명된 선택적 매개변수를 흉내낸 것이다.**  
  
**빌더 패턴은 계층적으로 설계된 클래스와 함께 쓰기에 좋다.**  
각 계층의 클래스에 관련 빌더를 멤버로 정의하자.(추상 클래스는 추상 빌더를, 구체 클래스는 구체 빌더를 갖게 한다.)
  
```java
public abstract class Pizza {
    public enum Topping {HAM, MUSHROOM, ONION, PEPPER, SAUSAGE}

    final Set<Topping> topping;

    abstract static class Builder<T extends Builder<T>> {
        EnumSet<Topping> toppings = EnumSet.noneOf(Topping.class);

        public T addTopping(Topping topping) {
            toppings.add(Objects.requireNonNulKtopping);
            return self();
        }
        abstract Pizza build();
        
        
        protected abstract T self();
    }

   Pizza(Builder<?> builder) {
        toppings = builder.toppings.clone();
   }
}
```

빌더패턴 익숙해지고, 제네릭 공부하고 아이템2 다시 읽기 




--- 
  
### 아이템03. private 생성자나 열거 타입으로 싱글톤임을 보증하라  
싱글톤이란, 인스턴스를 오직 하나만 생성할 수 있는 클래스를 말한다.  
싱글톤의 전형적인 예로는, 함수(아이템 24)와 같은 무상태 객체나,  
설계상 유일해야 하는 시스템 컴포넌트를 들 수 있다.  
  
**클래스를 싱글톤으로 만들면 이를 사용하는 클라이언트를 테스트하기 어려워진다.**   

```java
public class Elvis {
   public static final Elvis INSTANCE = new Elvis();

   private Elvis() {}
}
```

public이나 protected 생성자가 없으므로, Elvis 초기화될 때 만들어진 인스턴스가 하나임이 보장된다.    
아이템65 에서 단 private 생성자를 호출할 수 있는 방법이 있긴 한데, 이는 인스턴스가 두개 이상 생성될 때 예외처리를 해주면 된다.  
  
  
싱글톤으로 만드는 두 번째 방법은 정적 팩터리 메서드를 public static 멤버로 제공한다.  
```java
public class Elvis {
   private static final Elvis INSTANCE = new Elvis();
   private Elvis(){}
   
   public static Elvis getInstance() {
      return INSTANCE;
   }
}
```
  
Elvis.getInstance는 항상 같은 객체의 참조를 반환하므로, 제 2의 Elvis 인스턴스란 결코 만드러지지 않는다.  
      
---  

싱글톤 다른 방식에 따른 장단점은 이펙티브 자바 다른 아이템에 대한 지식이 필요하므로, 다음에 다시 정리하기   
  
--- 
  
싱글톤 만드는 세 번째 방법은 원소가 하나인 열거타입을 선언하는 것이다.  
```java
public enum Elvis {
    INSTANCE; 
   
   public void leaveTheBuilding(){...}
}
```



























