package com.Licenta.SocialMediaApp.Config.WebSocket;

import com.Licenta.SocialMediaApp.Config.Security.JwtProvider;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Service
public class AuthChannelInterceptorAdapter implements ChannelInterceptor {

    private final JwtProvider jwtProvider;

    public AuthChannelInterceptorAdapter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    /*@Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        System.out.println("Interceptor");
        Authentication authentication = (Authentication) accessor.getSessionAttributes().get("authentication");
        if (authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        return message;
    }*/

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");
            System.out.println("Interceptor: " + token);
            if (token != null) {
                try {
                    //token = token.substring(7); // Remove 'Bearer ' prefix
                    if (jwtProvider.validateToken(token)) {
                        Authentication auth = jwtProvider.getAuthentication(token);
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        accessor.setUser(auth); // Important: Set user as principal
                        System.out.println("Authentication successful for user: " + auth.getName());
                    }
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid Token");
                }
            }
        }
        return message;
    }

    /*@Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        Boolean disconnect = (Boolean) accessor.getSessionAttributes().get("disconnect");
        if (disconnect != null && disconnect && StompCommand.CONNECT.equals(accessor.getCommand())) {
            throw new RuntimeException("Invalid token provided, disconnecting.");
        }
    }*/

/*    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        SimpMessageHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, SimpMessageHeaderAccessor.class);
        Boolean disconnect = (Boolean) accessor.getSessionAttributes().get("disconnect");
        if (disconnect != null && disconnect && sent) {
            String sessionId = accessor.getSessionId();
            eventPublisher.publishEvent(new SessionDisconnectEvent(this, message, sessionId, CloseStatus.NORMAL));
            SecurityContextHolder.clearContext(); // Ensure security context is cleared
        }
    }*/

}

