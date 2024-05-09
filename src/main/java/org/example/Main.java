package org.example;

import java.util.*;
import java.util.concurrent.*;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(1000);

        for (int i = 0; i < 1000; i++) {
            Runnable callable = () -> {
                String s = generateRoute("RLRFR", 100);
                long R = Arrays.stream(s.split("")).filter(q -> q.equals("R")).count();

                synchronized (sizeToFreq) {
                    if (!sizeToFreq.containsKey((int) R)) {
                        sizeToFreq.put((int) R, 1);
                    } else {
                        int newV = sizeToFreq.get((int) R) + 1;
                        sizeToFreq.put((int) R, newV);
                    }
                }
            };
            service.submit(callable);
        }
        service.shutdown();
        //
        while (true) {
            if (service.isTerminated()) {
                break;
            }
        }
        //
        Integer max = sizeToFreq.values().stream().mapToInt(q -> q).max().getAsInt();
        Integer maxKey = sizeToFreq.keySet().stream().filter(s -> sizeToFreq.get(s) == max).findAny().get();
        System.out.println("Самое частое количество повторений " + maxKey + " (встретилось " + max + " раз)");
        sizeToFreq.remove(maxKey);
        System.out.println("Другие размеры: ");
        for (Integer c : sizeToFreq.keySet()) {
            System.out.println("- " + c + " (" + sizeToFreq.get(c) + " раз)");
        }
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }
}