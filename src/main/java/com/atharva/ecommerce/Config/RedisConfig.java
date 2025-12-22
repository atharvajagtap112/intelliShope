package com.atharva.ecommerce.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig implements CachingConfigurer {

    @Value("${spring.data.redis.url}")
    private String redisUrl;
    @Bean
    public CommandLineRunner testRedisConnection(RedisTemplate<String, Object> redisTemplate) {
        return args -> {
            try {
                System.out.println("=== Testing Redis Connection ===");
                redisTemplate.opsForValue().set("test:key", "test-value", Duration.ofMinutes(1));
                String value = (String) redisTemplate.opsForValue().get("test:key");
                System.out.println("✅ Redis connected successfully!  Test value: " + value);
                redisTemplate.delete("test:key");
            } catch (Exception e) {
                System.err.println("❌ Redis connection failed: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        System.out.println("=== Redis Configuration ===");
        System.out.println("Redis URL: " + (redisUrl != null ? redisUrl.replaceAll(":[^:/@]+@", ":***@") : "NOT SET"));
        System.out.println("========================");

        // Parse Redis URL
        // Format: rediss://default:password@host:port
        String cleanUrl = redisUrl.replace("rediss://", "").replace("redis://", "");
        boolean useSsl = redisUrl.startsWith("rediss://");

        String[] parts = cleanUrl.split("@");
        String credentials = parts[0];
        String hostPort = parts[1];

        String[] credParts = credentials.split(":");
        String password = credParts.length > 1 ? credParts[1] : null;

        String[] hostPortParts = hostPort.split(":");
        String host = hostPortParts[0];
        int port = Integer.parseInt(hostPortParts[1]);

        System.out.println("Parsed - Host: " + host + ", Port: " + port + ", SSL: " + useSsl);

        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(host);
        redisConfig.setPort(port);

        if (password != null && !password.isEmpty()) {
            redisConfig.setPassword(password);
        }

        LettuceClientConfiguration.LettuceClientConfigurationBuilder clientConfigBuilder =
                LettuceClientConfiguration.builder();

        if (useSsl) {
            clientConfigBuilder.useSsl().disablePeerVerification();
        }

        clientConfigBuilder.commandTimeout(Duration.ofSeconds(10));

        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisConfig, clientConfigBuilder.build());
        factory.afterPropertiesSet();

        return factory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.activateDefaultTyping(
                objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL
        );

        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofDays(365))
                .disableCachingNullValues()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(serializer)
                );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(cacheConfig)
                .build();
    }

    @Bean
    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, org.springframework.cache.Cache cache, Object key) {
                System.err.println("⚠️ Cache GET error - continuing without cache: " + exception.getMessage());
            }

            @Override
            public void handleCachePutError(RuntimeException exception, org.springframework.cache.Cache cache, Object key, Object value) {
                System.err.println("⚠️ Cache PUT error - continuing without cache: " + exception.getMessage());
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, org.springframework.cache.Cache cache, Object key) {
                System.err.println("⚠️ Cache EVICT error: " + exception.getMessage());
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, org.springframework.cache.Cache cache) {
                System.err.println("⚠️ Cache CLEAR error: " + exception.getMessage());
            }
        };
    }
}