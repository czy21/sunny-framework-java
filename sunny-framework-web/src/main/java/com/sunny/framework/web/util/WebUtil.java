package com.sunny.framework.web.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.ZoneId;

public class WebUtil {


    public static String getHeader(String name, String defaultValue) {
        return StringUtils.defaultIfEmpty(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getHeader(name), defaultValue);
    }

    public static String getHeader(String name) {
        return getHeader(name, null);
    }

    public static String getHeaderTimeZone() {
        return getHeader("X-TimeZone", ZoneId.systemDefault().getId());
    }

}
