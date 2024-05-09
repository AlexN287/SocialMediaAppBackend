package com.Licenta.SocialMediaApp.Config.Cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/*@Configuration
public class CaffeineConfig {

    @Bean
    public Cache<Integer, byte[]> profileImageCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS)
                .maximumSize(100)
                .build();
    }
}*/
