package chapter7;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Item42 {

    public static void main(String[] args) {
        // 익명 클래시의 인스턴스를 함수 객체로 사용 - 낡은 기법이다.
        List<String> list = new ArrayList<>();
        list.add("abc");
        list.add("ab");
        list.add("a");
//        Collections.sort(list, new Comparator<String>() {
//            @Override
//            public int compare(String o1, String o2) {
//                return Integer.compare(o1.length(), o2.length());
//            }
//        });

        Collections.sort(list,
                (s1, s2) -> Integer.compare(s1.length(), s2.length()));

        for (String s : list) {
            System.out.println(s);
        }

    }

}
