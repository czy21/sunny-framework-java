package com.sunny.framework.core.jackson;

import tools.jackson.core.Base64Variants;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

import java.nio.charset.StandardCharsets;


public class Base64ToStringDeserializer extends ValueDeserializer<String> {

    @Override
    public String deserialize(JsonParser p, DeserializationContext deserializationContext) {
        String base64 = p.getValueAsString();
        if (base64 == null || base64.isEmpty()) {
            return null;
        }
        try {
            byte[] decodedBytes = Base64Variants.getDefaultVariant().decode(base64);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return base64;
        }
    }
}