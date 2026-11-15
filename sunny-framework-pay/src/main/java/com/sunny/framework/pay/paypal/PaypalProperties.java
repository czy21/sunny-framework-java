package com.sunny.framework.pay.paypal;

import com.paypal.sdk.Environment;
import lombok.Data;
import org.slf4j.event.Level;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Data
@ConfigurationProperties(prefix = PaypalProperties.PREFIX)
public class PaypalProperties {

    public static final String PREFIX = "sunny.pay.paypal";

    private String clientId;
    private String clientKey;
    private Environment environment = Environment.SANDBOX;
    private Level logLevel = Level.DEBUG;
    private String returnUrl;
    private String cancelUrl;
    private String webhookId;
}
