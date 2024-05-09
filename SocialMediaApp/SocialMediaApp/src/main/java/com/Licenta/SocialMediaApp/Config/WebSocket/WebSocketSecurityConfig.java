package com.Licenta.SocialMediaApp.Config.WebSocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

import static org.springframework.messaging.simp.SimpMessageType.*;

@Configuration
/*@EnableWebSocketMessageBroker*/
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
                .simpDestMatchers("/app/verifyToken").permitAll()// Allow unauthenticated access to token verification
                .simpSubscribeDestMatchers("/topic/**").authenticated() // Require authentication for subscribing to topics
                .simpTypeMatchers(CONNECT, UNSUBSCRIBE, DISCONNECT).permitAll()
                .anyMessage().authenticated(); // Require authentication for any other messages
    }

    @Override
    protected boolean sameOriginDisabled() {
        // Disables CSRF within WebSockets
        return true;
    }
}

/*@Configuration
@EnableWebSocketSecurity
public class WebSocketSecurityConfig {

    @Bean
    AuthorizationManager<Message<?>> messageAuthorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        messages
                .simpDestMatchers("/app/verifyToken").permitAll()
                .simpMessageDestMatchers("/app/verifyToken").permitAll()
                .anyMessage().permitAll();

        return messages.build();
    }
}*/
