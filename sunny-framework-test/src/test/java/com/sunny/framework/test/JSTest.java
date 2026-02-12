package com.sunny.framework.test;

import com.sunny.framework.core.util.ScriptUtil;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class JSTest {


    @Test
    public void test1() {
        Map<String, Object> row = new HashMap<>();
        row.put("query", Map.of("year", 2024));
        row.put("name", "john");
        IntStream.rangeClosed(1, 10).forEach(i -> row.put("m" + i, 1));
        Integer yearValue = ScriptUtil.getJsValue(row, "obj.query?.year", Integer.class);
        Integer yearTotal = ScriptUtil.getJsValue(row, "Array.from({length:12}).map((t,i,a)=>Number(obj[`m${i+1}`]) || 0).reduce((a, b) => a + b)", Integer.class);
        Assert.isTrue(yearValue == 2024, "year should be 2024");
        Assert.isTrue(yearTotal == 10, "yearTotal should be 10");
    }

    @Test
    public void testMultiThread() {
        List<Map<String, Object>> rows = IntStream.rangeClosed(1, 20000).mapToObj(i -> {
            Map<String, Object> r = new HashMap<>();
            r.put("name", "n" + i);
            IntStream.rangeClosed(1, 10).forEach(ri -> r.put("m" + ri, 1));
            return r;
        }).toList();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        rows.parallelStream().forEach(t -> {
            aggAttr("yearTotal1", t);
            aggAttr("yearTotal11", t);
            aggAttr("yearTotal12", t);
            aggAttr("yearTotal13", t);
        });
        stopWatch.stop();
        System.out.println("serial duration: " + stopWatch.getTime(TimeUnit.SECONDS));

        List<CompletableFuture<Void>> futures = rows.stream().map(t -> CompletableFuture.runAsync(() -> {
            aggAttr("yearTotal2", t);
            aggAttr("yearTotal21", t);
            aggAttr("yearTotal22", t);
            aggAttr("yearTotal23", t);
        })).toList();
        stopWatch.reset();
        stopWatch.start();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{})).join();
        stopWatch.stop();
        System.out.println("multi duration: " + stopWatch.getTime(TimeUnit.SECONDS));
        Assert.isTrue(rows.stream().filter(t -> ((Integer) t.get("yearTotal2")) != 10).toList().isEmpty(), "multi thread graaljs ok");
    }

    private static void aggAttr(String attr, Map<String, Object> t) {
        Integer yearTotal = ScriptUtil.getJsValue(t, "Array.from({length:12}).map((t,i,a)=>Number(obj[`m${i+1}`]) || 0).reduce((a, b) => a + b)", Integer.class);
        t.put(attr, yearTotal);
    }

}
