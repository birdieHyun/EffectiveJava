package page2;

public class Item02 {

    //telescoping constructor pattern

//    private final int servingSize;
//    private final int servings;
//    private final int calories;
//    private final int fat;
//
//    // 사용자가 설정하기 원하지 않는 매개변수까지 포함하기 쉬운데, 이럴 땐 어쩔수 없이 포함시켜주어야 한다.
//    public Item02(int servingSize, int servings) {
//        this(servingSize, servings, 0);
//    }
//
//    public Item02(int servingSize, int servings, int calories) {
//        this(servingSize, servings, calories, 0);
//    }
//
//    public Item02(int servingSize, int servings, int calories, int fat) {
//        this.servingSize = servingSize;
//        this.servings = servings;
//        this.calories = calories;
//        this.fat = fat;
//    }
//

    // ------------------------------------------------

    // 자바빈즈 패턴 - 일관성이 깨지고, 불변으로 만들 수 없다.
//    private int servingSize = -1;
//    private int servings = -1;
//    private int calorie = 0;
//    private int fat = 0;
//
//    public Item02() {}
//
//    public void setServingSize(int servingSize) {
//        this.servingSize = servingSize;
//    }
//
//    public void setServings(int servings) {
//        this.servings = servings;
//    }
//
//    public void setCalorie(int calorie) {
//        this.calorie = calorie;
//    }
//
//    public void setFat(int fat) {
//        this.fat = fat;
//    }


    // ---------------------------------------
    // 빌더패턴
    private final int servingSize;
    private final int servings;
    private final int calories;
    private final int fat;

    public static class Builder {
        // 필수 매개변수
        private final int servingSize;
        private final int servings;

        // 선택 매개변수 - 기본값으로 초기화한다.
        private int calories = 0;
        private int fat = 0;

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
        public Item02 build() {
            return new Item02(this);
        }
    }
        private Item02(Builder builder) {
            servingSize = builder.servingSize;
            servings = builder.servings;
            calories = builder.calories;
            fat = builder.fat;
        }
}

