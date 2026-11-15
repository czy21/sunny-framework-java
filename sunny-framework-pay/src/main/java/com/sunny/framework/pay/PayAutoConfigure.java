package com.sunny.framework.pay;


import com.sunny.framework.pay.paypal.PaypalProperties;
import com.sunny.framework.pay.paypal.PaypalProvider;
import com.sunny.framework.pay.paypal.PaypalClient;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@EnableConfigurationProperties(PaypalProperties.class)
@Configuration(proxyBeanMethods = false)
@EnableFeignClients(clients = PaypalClient.class)
public class PayAutoConfigure {

    @Bean
    @ConditionalOnProperty(prefix = "sunny.pay.paypal", name = "client-id")
    public PaypalProvider paypalProvider(PaypalProperties properties, RedissonClient redissonClient, PaypalClient paypalClient) {
        return new PaypalProvider(properties, redissonClient, paypalClient);
    }
}
