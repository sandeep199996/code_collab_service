package com.mentorplatform.Backend.controller;



import com.mentorplatform.Backend.dto.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    // 1. A user sends a message to /app/chat.sendMessage
    @MessageMapping("/chat.sendMessage")
    // 2. The server instantly broadcasts it to everyone subscribed to /topic/public
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }

    // A special method just for announcing when a new user joins the chat
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage) {
        // We could do extra logic here like saving the user to a "currently online" database table!
        return chatMessage;
    }
    @MessageMapping("/code.sendChange") // MUST be @MessageMapping
    @SendTo("/topic/code")
    public ChatMessage sendCodeChange(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }
@MessageMapping("/video.signal")
@SendTo("/topic/video")
public ChatMessage handleVideoSignal(@Payload ChatMessage signal) {
        return signal;}
}


