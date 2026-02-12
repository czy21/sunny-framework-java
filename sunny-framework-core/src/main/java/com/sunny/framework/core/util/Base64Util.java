package com.sunny.framework.core.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Util {

    public static String decode(byte[] src) {
        try {
            byte[] decoded = Base64.getDecoder().decode(src);
            return new String(decoded, StandardCharsets.UTF_8);
        } catch (Exception ignore) {
            return null;
        }
    }

    public static String decode(String src) {
        try {
            byte[] decoded = Base64.getDecoder().decode(src);
            return new String(decoded, StandardCharsets.UTF_8);
        } catch (Exception ignore) {
            return null;
        }
    }
}
