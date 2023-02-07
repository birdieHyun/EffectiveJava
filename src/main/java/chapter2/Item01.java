package chapter2;

// 이 부분에 대해서 더 찾아볼 필요가 있다.

public class Item01 {

    private String order;
    private boolean common;
    private boolean urgent;

    public Item01() {
    }

    public Item01(String order, boolean common) {
        this.order = order;
        this.common = common;
    }

//    public ItemOne(String order, boolean urgent) { // 생성자의 선언부가 같기 때문에 컴파일 에러가 난다.
//        this.order = order;
//        this.urgent = urgent;
//    }

    public Item01(boolean urgent, String order) { // 이렇게 매개변수의 순서를 바꿀 수도 있지만, 비효율적임
        this.order = order;
        this.urgent = urgent;
    }

    // 그래서 정적 팩터리 메서드를 고려해야 한다.
    public static Item01 makeUrgentOrder(String order, boolean urgent) {
        Item01 itemOne = new Item01();

        itemOne.order = order;
        itemOne.urgent = urgent;

        return itemOne;
    }

    public static Item01 makeCommonOrder(String order, boolean urgent) {
        Item01 itemOne = new Item01();
        itemOne.order = order;
//        itemOne.common = common;

        return itemOne;
    }
    // 메서드 명이 명확해진다.
}
