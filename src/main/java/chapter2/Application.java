package chapter2;

public class Application {
    public static void main(String[] args) {
        Item01 item011 = new Item01("order", true);
        Item01 item01 = new Item01();
        Item01 commonOr = item01.makeCommonOrder("order", true);
        Item01 urgentOrder = item01.makeUrgentOrder("order", true);
    }
}
