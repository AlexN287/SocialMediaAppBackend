package com.Licenta.SocialMediaApp.Config.Cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Configuration
public class CaffeineConfig {
    @Bean
    @Profile("test")
    public Cache<Long, byte[]> testProfileImageCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.SECONDS) // Shorter duration for testing
                .maximumSize(10)
                .build();
    }

    @Bean
    public Cache<Long, byte[]> profileImageCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS)
                .maximumSize(100)
                .build();
    }

}
