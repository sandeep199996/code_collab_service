package com.mentorplatform.Backend.dto;

import lombok.Data;

@Data
public class ChatMessage {
    private String content;
    private String sender;
    private MessageType type;

    public enum MessageType {
        CHAT,   // A normal message
        JOIN, // Someone entered the room
        CODE, // A code change message
        LEAVE// Someone left the room

    }
}