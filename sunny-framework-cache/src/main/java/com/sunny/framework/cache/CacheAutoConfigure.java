package com.sunny.framework.cache;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.Optional;

@PropertySource("classpath:sunny-common-cache-default.properties")
@EnableCaching
@Configuration
public class CacheAutoConfigure {

    JsonMapper jsonMapper;

    public CacheAutoConfigure(ObjectProvider<JsonMapper> jsonMapper) {
        JsonMapper.Builder jsonMapperBuilder = Optional.ofNullable(jsonMapper.getIfAvailable()).map(JsonMapper::rebuild).orElse(JsonMapper.builder());
        jsonMapperBuilder = jsonMapperBuilder.changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL));
        jsonMapperBuilder = jsonMapperBuilder.changeDefaultPropertyInclusion(incl -> incl.withContentInclusion(JsonInclude.Include.NON_NULL));
        this.jsonMapper = jsonMapperBuilder.build();
    }

    @Primary
    @Bean
    public RedisCacheManager jacksonRedisCacheManager(RedisConnectionFactory factory) {
        RedisSerializer<String> keySerializer = new StringRedisSerializer();
        GenericJacksonJsonRedisSerializer valueSerializer = new GenericJacksonJsonRedisSerializer(jsonMapper);
        RedisCacheConfiguration config = RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofHours(2))
                .computePrefixWith(cacheName -> cacheName + ":")
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer));
        return RedisCacheManager.builder(factory).cacheDefaults(config).build();
    }
}
