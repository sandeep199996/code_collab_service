package com.mentorplatform.Backend.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // This is the URL our React frontend will use to establish the initial connection
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:5173") // Allow our Vite React app
                .withSockJS(); // A fallback mechanism if a browser doesn't support raw WebSockets
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Messages whose destination starts with "/app" are routed to our @MessageMapping controllers
        registry.setApplicationDestinationPrefixes("/app");

        // Messages whose destination starts with "/topic" are routed directly to the broker to be broadcasted
        registry.enableSimpleBroker("/topic");
    }
}