package com.mentorplatform.Backend.controller;



import com.mentorplatform.Backend.dto.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessagingTemplate;



@Controller
public class ChatController {


    @Autowired
     private SimpMessagingTemplate messagingTemplate;

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

    @MessageMapping("/chat.sendPrivate/{roomId}")
    public void sendPrivateMessage(@DestinationVariable String roomId, @Payload ChatMessage chatMessage) {
        System.out.println("Routing private message to room: " + roomId);

        // TO broadcast the message ONLY to the two people subscribed to this specific room string
        messagingTemplate.convertAndSend("/topic/session/" + roomId, chatMessage);
    }
    // Route for Private Code Collaboration
    @MessageMapping("/code.sendPrivate/{roomId}")
    public void sendPrivateCode(@DestinationVariable String roomId, @Payload ChatMessage message) {
        //  appended "/code" to the room so chat and code don't get mixed up in the frontend!
        messagingTemplate.convertAndSend("/topic/session/" + roomId + "/code", message);
    }

    // Route for Private WebRTC Signaling
    @MessageMapping("/video.sendPrivate/{roomId}")
    public void sendPrivateVideo(@DestinationVariable String roomId, @Payload ChatMessage message) {
        //  appended "/video" to keep the signaling channel clean
        messagingTemplate.convertAndSend("/topic/session/" + roomId + "/video", message);
    }
}


