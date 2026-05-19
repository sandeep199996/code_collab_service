package com.mentorplatform.Backend.controller;



import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class SessionController {

    private final SimpMessagingTemplate messagingTemplate;

    public SessionController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Listens for User A saying "I want to connect with User B"
    @MessageMapping("/session.invite")
    public void sendSessionInvite(@Payload Map<String, String> payload) {
        String targetEmail = payload.get("target");
        String senderEmail = payload.get("sender");

        System.out.println("Switchboard: Routing invite from " + senderEmail + " to " + targetEmail);

        // We route the message EXCLUSIVELY to the target's personal mailbox
        messagingTemplate.convertAndSend("/topic/invites/" + targetEmail, payload);
    }

    // Listens for User B saying "I accept User A's invite!"
    @MessageMapping("/session.accept")
    public void acceptSessionInvite(@Payload Map<String, String> payload) {
        String originalSender = payload.get("target"); // We send the acceptance back to the person who invited us
        System.out.println("Switchboard: Routing acceptance back to " + originalSender);
        messagingTemplate.convertAndSend("/topic/invites/" + originalSender, payload);
    }



}