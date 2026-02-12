package com.sunny.framework.core.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.Assert;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class ScriptUtilTest {

    @Test
    public void testGetValue() {
        String val = ScriptUtil.getJsValue(Map.of("name", "hao"), "obj.name", String.class);
        Assert.hasText("hao", "success");
    }
}