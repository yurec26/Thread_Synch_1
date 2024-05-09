package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(1000);

        Thread counterThr = new Thread(() -> {
            while (!Thread.interrupted()) {
                synchronized (sizeToFreq) {
                    try {
                        sizeToFreq.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                    Integer max = sizeToFreq.values().stream().mapToInt(q -> q).max().getAsInt();
                    Integer maxKey = sizeToFreq.keySet().stream().filter(s -> sizeToFreq.get(s) == max).findAny().get();
                    System.out.println("Самое частое количество повторений " + maxKey + " (встретилось " + max + " раз)");
                }
            }
        });

        counterThr.start();

        for (int i = 0; i < 1000; i++) {
            Runnable callable = () -> {
                String s = generateRoute("RLRFR", 100);
                long R = s.chars().filter(ch -> ch == 'R').count();
                synchronized (sizeToFreq) {
                    if (!sizeToFreq.containsKey((int) R)) {
                        sizeToFreq.put((int) R, 1);
                    } else {
                        int newV = sizeToFreq.get((int) R) + 1;
                        sizeToFreq.put((int) R, newV);
                    }
                    sizeToFreq.notify();
                }
            };
            service.submit(callable);
        }
        service.shutdown();

        while (true) {
            if (service.isTerminated()) {
                break;
            }
        }
        counterThr.interrupt();
        try {
            counterThr.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
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