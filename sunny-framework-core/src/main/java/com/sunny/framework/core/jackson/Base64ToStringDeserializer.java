package com.sunny.framework.core.jackson;

import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class Base64ToStringDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser p, DeserializationContext deserializationContext) throws IOException {
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