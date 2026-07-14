package com.mentorplatform.Backend.controller;

import com.mentorplatform.Backend.dto.ChatMessage;
import com.mentorplatform.Backend.entity.DirectMessage;
import com.mentorplatform.Backend.service.DirectMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class ChatController {


    @Autowired
     private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private DirectMessageService messageService;
//SYNC DM VC CODE CHANGES
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
    //ASC DIRECT MESSAGING
    // The Interceptor Endpoint: Saves to DB first, then routes globally
    @MessageMapping("/chat.sendDirect")
    public void sendDirectMessage(@Payload Map<String, String> payload) {
        String sender = payload.get("sender");
        String recipient = payload.get("recipient");
        String content = payload.get("content");

        // Save it permanently to MySQL
        DirectMessage savedMessage = messageService.saveMessage(sender, recipient, content);

        // Blast to recipient's global inbox channel
        messagingTemplate.convertAndSend("/topic/messages/" + recipient, savedMessage);

        // Blast back to sender to update their UI
        messagingTemplate.convertAndSend("/topic/messages/" + sender, savedMessage);
    }
    // HTTP Endpoint: Fetch chat history when opening the DM window
    @GetMapping("/api/messages/history/{otherUser}")
    public ResponseEntity<List<DirectMessage>> getHistory(@PathVariable String otherUser, Principal principal) {
        String myEmail = principal.getName();

        // If I open the chat, mark all messages sent to me from this user as "Read"
        messageService.markConversationAsRead(otherUser, myEmail);

        return ResponseEntity.ok(messageService.getChatHistory(myEmail, otherUser));
    }
    //  HTTP Endpoint: Get unread count for the React Notification Bell
    @GetMapping("/api/messages/unread")
    public ResponseEntity<Long> getUnreadCount(Principal principal) {
        return ResponseEntity.ok(messageService.getUnreadCount(principal.getName()));
    }
    // HTTP Endpoint: Get unread count PER USER for the Inbox Directory
    @GetMapping("/api/messages/unread-map")
    public ResponseEntity<Map<String, Long>> getUnreadMap(Principal principal) {
        return ResponseEntity.ok(messageService.getUnreadCountsPerSender(principal.getName()));
    }
}



