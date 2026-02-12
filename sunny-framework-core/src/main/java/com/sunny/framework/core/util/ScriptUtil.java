package com.sunny.framework.core.util;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.TypeLiteral;

import java.util.Map;

public class ScriptUtil {

    public final static String JS_GET_VALUE = "function getValue(obj, expression){ return Function('obj', 'return ' + expression)(obj) }";

    public static <T> T getJsValue(Map<String, Object> obj, String expression, Class<T> targetType) {
        try (Context context = Context.newBuilder("js").allowHostAccess(HostAccess.ALL).allowHostClassLookup(className -> true).build()) {
            return context.eval("js", String.format("(%s)", JS_GET_VALUE)).execute(obj, expression).as(targetType);
        }
    }

    public static <T> T getJsValue(Map<String, Object> object, String expression, TypeLiteral<T> targetType) {
        try (Context context = Context.newBuilder("js").allowHostAccess(HostAccess.ALL).allowHostClassLookup(className -> true).build()) {
            return context.eval("js", String.format("(%s)", JS_GET_VALUE)).execute(object, expression).as(targetType);
        }
    }
}
