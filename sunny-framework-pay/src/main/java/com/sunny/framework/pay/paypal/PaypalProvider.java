package com.sunny.framework.pay.paypal;

import com.paypal.sdk.ClientCredentialsAuth;
import com.paypal.sdk.PaypalServerSdkClient;
import com.paypal.sdk.authentication.ClientCredentialsAuthModel;
import com.paypal.sdk.models.OAuthToken;
import com.sunny.framework.pay.provider.PayProvider;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PaypalProvider implements PayProvider {

    PaypalProperties properties;
    RedissonClient redissonClient;
    PaypalClient paypalClient;
    PaypalServerSdkClient paypalServerSdkClient;
    RBucket<OAuthToken> oAuthTokenOperator;

    public PaypalProvider(PaypalProperties properties, RedissonClient redissonClient, PaypalClient paypalClient) {
        this.properties = properties;
        this.redissonClient = redissonClient;
        this.paypalClient = paypalClient;
        this.oAuthTokenOperator = redissonClient.getBucket("paypal:%s:token".formatted(properties.getClientId()));
        ClientCredentialsAuthModel clientCredentialsAuthModel = new ClientCredentialsAuthModel.Builder(properties.getClientId(), properties.getClientKey())
                .oAuthTokenProvider((lastToken, manager) -> getOAuthToken())
                .oAuthOnTokenUpdate(this::setOAuthToken)
                .build();
        paypalServerSdkClient = new PaypalServerSdkClient.Builder()
                .loggingConfig(builder -> builder
                        .level(properties.getLogLevel())
                        .requestConfig(logConfigBuilder -> logConfigBuilder.headers(true).body(true))
                        .responseConfig(logConfigBuilder -> logConfigBuilder.headers(true).body(true)))
                .httpClientConfig(configBuilder -> configBuilder.timeout(0))
                .clientCredentialsAuth(clientCredentialsAuthModel)
                .environment(properties.getEnvironment())
                .build();
    }

    public PaypalServerSdkClient getClient() {
        return paypalServerSdkClient;
    }

    private OAuthToken getOAuthToken() {
        ClientCredentialsAuth manager = paypalServerSdkClient.getClientCredentialsAuth();
        OAuthToken token = oAuthTokenOperator.get();
        if (token != null && !manager.isTokenExpired(token)) {
            return token;
        }
        RLock lock = redissonClient.getLock("paypal:%s:lock".formatted(properties.getClientId()));
        try {
            if (!lock.tryLock(10, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Failed to acquire PayPal token lock");
            }
            token = oAuthTokenOperator.get();
            if (token != null && !manager.isTokenExpired(token)) {
                return token;
            }
            return manager.fetchToken();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while acquiring PayPal token lock", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private void setOAuthToken(OAuthToken token) {
        long ttl = token.getExpiresIn() - 300;
        if (ttl > 0) {
            oAuthTokenOperator.set(token, Duration.ofSeconds(ttl));
        } else {
            oAuthTokenOperator.delete();
        }
    }

    @Override
    public String getAccessToken() {
        return getOAuthToken().getAccessToken();
    }

    @Override
    public void verifySignature(Map<String, Object> param, Map<String, String> header) {
        if (StringUtils.isBlank(properties.getWebhookId())) return;
        Map<String, Object> input = new HashMap<>();
        input.put("webhook_id", properties.getWebhookId());
        input.put("webhook_event", param);
        input.put("auth_algo", header.get("paypal-auth-algo"));
        input.put("cert_url", header.get("paypal-cert-url"));
        input.put("transmission_id", header.get("paypal-transmission-id"));
        input.put("transmission_sig", header.get("paypal-transmission-sig"));
        input.put("transmission_time", header.get("paypal-transmission-time"));
        try {
            Map<String, Object> body = paypalClient.verifyWebhookSignature(URI.create(getClient().getBaseUri()), "Bearer " + getAccessToken(), input);
            if ("SUCCESS".equals(body.get("verification_status"))) return;
        } catch (Exception e) {
            log.error("", e);
        }
        throw new IllegalStateException("PayPal webhook signature verification failed");
    }
}
