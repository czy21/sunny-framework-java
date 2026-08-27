package com.sunny.framework.pay;


import com.sunny.framework.pay.paypal.PayPalProperties;
import com.sunny.framework.pay.paypal.PayPalProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(PayPalProperties.class)
@Configuration(proxyBeanMethods = false)
public class PayAutoConfigure {

    @Bean
    @ConditionalOnProperty(prefix = "sunny.pay.paypal", name = "client-id")
    public PayPalProvider paypalProvider(PayPalProperties properties) {
        return new PayPalProvider(properties);
    }
}
