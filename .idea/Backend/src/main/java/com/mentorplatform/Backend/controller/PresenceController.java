package com.mentorplatform.Backend.controller;


import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class PresenceController {

    private final SimpMessagingTemplate messagingTemplate;

    // Server Memory: Keeps track of Network Sessions and User Statuses
    private final Map<String, String> activeSessions = new ConcurrentHashMap<>(); // SessionID -> Email
    private final Map<String, String> userStatuses = new ConcurrentHashMap<>();   // Email -> Status

    public PresenceController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // 1. When a React frontend connects, it shouts "I am here!"
    @MessageMapping("/presence.announce")
    public void announcePresence(@Payload String email, SimpMessageHeaderAccessor headerAccessor) {
        // Link their physical network session to their email
        String sessionId = headerAccessor.getSessionId();
        activeSessions.put(sessionId, email);

        // Turn their LED green
        userStatuses.put(email, "ONLINE");

        // Broadcast the entire map of all users' statuses to everyone's directory
        messagingTemplate.convertAndSend("/topic/presence", userStatuses);
    }

    // 2. The "Ghost" Catcher: Automatically triggers if the browser tab is closed
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        // Look up who owned that network session
        String email = activeSessions.remove(sessionId);
        if (email != null) {
            // Turn their LED red and broadcast the update
            userStatuses.put(email, "OFFLINE");
            messagingTemplate.convertAndSend("/topic/presence", userStatuses);

            System.out.println("Presence Radar: " + email + " disconnected.");
        }
    }
    // Allows a user to manually change their LED to BUSY or ONLINE
    @MessageMapping("/presence.setStatus")
    public void setStatus(@Payload Map<String, String> payload) {
        String email = payload.get("email");
        String status = payload.get("status");

        if(activeSessions.containsValue(email)) {
            userStatuses.put(email, status);
            messagingTemplate.convertAndSend("/topic/presence", userStatuses);
        }
    }

}