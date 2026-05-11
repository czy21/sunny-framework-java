package com.sunny.framework.pay.paypal;

import com.paypal.sdk.PaypalServerSdkClient;
import com.paypal.sdk.authentication.ClientCredentialsAuthModel;

public class PayPalProvider {

    PayPalProperties properties;

    PaypalServerSdkClient paypalServerSdkClient;

    public PayPalProvider(PayPalProperties properties) {
        this.properties = properties;
        ClientCredentialsAuthModel clientCredentialsAuthModel = new ClientCredentialsAuthModel.Builder(properties.getClientId(), properties.getClientKey()).build();
        paypalServerSdkClient = new PaypalServerSdkClient.Builder()
                .loggingConfig(builder -> builder
                        .level(properties.getLogLevel())
                        .requestConfig(logConfigBuilder -> logConfigBuilder.body(true))
                        .responseConfig(logConfigBuilder -> logConfigBuilder.headers(true)))
                .httpClientConfig(configBuilder -> configBuilder.timeout(0))
                .clientCredentialsAuth(clientCredentialsAuthModel)
                .environment(properties.getEnvironment())
                .build();
    }

    public PaypalServerSdkClient getClient() {
        return paypalServerSdkClient;
    }
}
