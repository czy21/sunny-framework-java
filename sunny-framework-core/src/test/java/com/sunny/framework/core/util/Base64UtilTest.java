package com.sunny.framework.core.util;

import org.junit.jupiter.api.Test;

public class Base64UtilTest {

    @Test
    public void test1() {
        String src = "5L2g5bey5re75Yqg5LqGQXN5bmPvvIznjrDlnKjlj6/ku6XlvIDlp4vogYrlpKnkuobjgII=";
        String dst = Base64Util.decode(src);
        System.out.println(dst);
    }
}