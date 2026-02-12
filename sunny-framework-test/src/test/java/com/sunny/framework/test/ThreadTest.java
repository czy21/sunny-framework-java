package com.sunny.framework.test;

import lombok.Data;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class ThreadTest {
    @Data
    static class Object1 {
        static ThreadLocal<Object2> tl = new ThreadLocal<>();
    }

    @Data
    static class Object2 {
        private String name;
    }

    @Test
    public void pool1() throws InterruptedException {
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("core size: " + cores);
        ExecutorService fixedThreadPool = new ThreadPoolExecutor(3, 5,
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(5));
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            fixedThreadPool.execute(new Thread(() -> {
//                Byte[] bytes = new Byte[1024 * 1000 * 1000];
                System.out.println(Thread.currentThread() + ": " + finalI);
            }));
        }
        // 所有任务执行完成且等待队列中也无任务关闭线程池
        fixedThreadPool.shutdown();
        // 阻塞主线程, 直至线程池关闭
        fixedThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
//        Thread.sleep(200000);
    }

    @Test
    public void pool2() throws InterruptedException {
        List<String> strs = IntStream.range(0, 20).mapToObj(Integer::toString).toList();
        ExecutorService pool = new ThreadPoolExecutor(
                12,
                12,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>()
        );
        LocalDateTime concurrentStartTime = LocalDateTime.now();
        List<CompletableFuture<String>> ps = strs.stream().map(t -> CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread());
            return t;
        }, pool)).toList();
        var ret = ps.stream().map(CompletableFuture::join).toList();
        LocalDateTime concurrentEndTime = LocalDateTime.now();
        System.out.println("concurrent timeout: " + Duration.between(concurrentStartTime, concurrentEndTime).toMillis());
        System.out.println(ret);
    }

    @Test
    public void testThreadLocal1() {
        Object1 o1 = new Object1();
        Object2 o2 = new Object2();
        new Thread(() -> {
            o2.setName("你是");
            Object1.tl.set(o2);
            System.out.println(Object1.tl.get().name);
            Object1.tl.remove();
        }).start();

        new Thread(() -> {
            o2.setName("我是");
            Object1.tl.set(o2);
            System.out.println(Object1.tl.get().name);
            Object1.tl.remove();
        }).start();

//        o1.getTl().remove();
        System.out.println("ff");
    }

    // get async task all result
    @Test
    public void getAsyncTaskAllResult() {
        Map<String, Object> ret = new HashMap<>();
        List<CompletableFuture<Map<String, Object>>> futures = IntStream.range(0, 50)
                .mapToObj(t -> {
                    return CompletableFuture.<Map<String, Object>>supplyAsync(() -> {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println(Thread.currentThread());
                        return Map.of("name" + t, t);
                    });
                }).toList();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{}))
                .thenApply(v -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList())).join()
                .forEach(ret::putAll);
        stopWatch.stop();
        System.out.println(stopWatch.getTime(TimeUnit.SECONDS));
        System.out.println(ret);
        System.out.println(ret.size());
    }
}