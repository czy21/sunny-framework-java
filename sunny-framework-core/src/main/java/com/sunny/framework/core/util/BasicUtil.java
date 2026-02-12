package com.sunny.framework.core.util;

import java.util.Optional;
import java.util.function.Supplier;

public class BasicUtil {

    public static <T> Optional<T> tryApply(Supplier<T> supplier) {
        try {
            return Optional.ofNullable(supplier.get());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
