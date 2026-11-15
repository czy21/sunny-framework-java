package com.sunny.framework.pay.provider;

import java.util.Map;

public interface PayProvider {
    String getAccessToken();

    void verifySignature(Map<String, Object> param, Map<String, String> header);
}
