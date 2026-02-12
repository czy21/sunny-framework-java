package com.sunny.framework.web.feign;

import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.jackson3.Jackson3Decoder;
import feign.jackson3.Jackson3Encoder;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.json.JsonMapper;

public class FeignConfigure {

    @Bean
    public Encoder encoder(JsonMapper jsonMapper) {
        return new Jackson3Encoder(jsonMapper);
    }

    @Bean
    public Decoder decoder(JsonMapper jsonMapper) {
        return new DecoderProxy(new Jackson3Decoder(jsonMapper));
    }
}
