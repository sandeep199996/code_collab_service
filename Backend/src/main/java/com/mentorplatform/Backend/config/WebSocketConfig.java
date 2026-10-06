package com.mentorplatform.Backend.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // This is the URL our React frontend will use to establish the initial connection
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:5173","http://localhost:5174") // Allow our Vite React app
                .withSockJS(); // A fallback mechanism if a browser doesn't support raw WebSockets
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Messages whose destination starts with "/app" are routed to our @MessageMapping controllers
        registry.setApplicationDestinationPrefixes("/app");

        // Messages whose destination starts with "/topic" are routed directly to the broker to be broadcasted
        registry.enableSimpleBroker("/topic");
    }
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);


                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {

                    String userEmail = accessor.getFirstNativeHeader("userEmail");

                    if (userEmail != null) {

                        accessor.getSessionAttributes().put("userEmail", userEmail);
                    }
                }
                return message;
            }
        });
    }
}