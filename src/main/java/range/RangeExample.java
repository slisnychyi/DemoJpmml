package range;



import org.apache.commons.lang3.Range;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RangeExample {

    private static Map<String, Integer> config = new HashMap<>();
    private static Map<String, Range<Integer>> config2 = new HashMap<>();

    public static void main(String[] args) {

        config2.put("a", Range.between(0,10));
        config2.put("b", Range.between(10,100));

        config.put("a", 90);
        config.put("b", 10);

        int a = 0;
        int b = 0;

        for (int i = 0; i < 100; i++) {
            String res = null;
            while (res == null){
//                res = selectRes();
                res = selectRes2();
            }
            if("a".equalsIgnoreCase(res)) a++;
            if("b".equalsIgnoreCase(res)) b++;

        }

        System.out.println("Result = [a="+a+"b="+b+"]");


        Random random = new Random();

        for (int j = 0; j < 10000; j++) {
            int i = random.nextInt(100) + 1;
            if(i == 0) {
                System.out.println(i);
            }
        }

    }

    private static String selectRes() {
        String result = null;
        Random i1 = new Random();
        int rnd = i1.nextInt(101);
        int count = 0;

        for (Map.Entry<String, Integer> stringIntegerEntry : config.entrySet()) {
            Integer priority = stringIntegerEntry.getValue();
            count += priority;

            if(count >= rnd){
               result = stringIntegerEntry.getKey();
               break;
            }
        }
        return result;
    }

    private static String selectRes2() {
        String result = null;
        Random i1 = new Random();
        int rnd = i1.nextInt(101);
        int count = 0;

        for (Map.Entry<String, Range<Integer>> stringIntegerEntry : config2.entrySet()) {
            Range<Integer> priority = stringIntegerEntry.getValue();

            if(priority.contains(rnd)){
                result = stringIntegerEntry.getKey();
                break;
            }
        }
        return result;
    }
}
