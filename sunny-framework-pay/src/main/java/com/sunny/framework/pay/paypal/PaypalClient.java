package com.sunny.framework.pay.paypal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.net.URI;
import java.util.Map;

@FeignClient(name = "paypalClient", url = "https://api-m.sandbox.paypal.com")
public interface PaypalClient {
    @PostMapping(path = "/v1/notifications/verify-webhook-signature", headers = {"Content-Type=application/json",})
    Map<String, Object> verifyWebhookSignature(URI uri, @RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody Map<String, Object> body);
}
