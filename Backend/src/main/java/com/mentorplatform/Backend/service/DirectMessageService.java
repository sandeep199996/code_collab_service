package com.mentorplatform.Backend.service;

import com.mentorplatform.Backend.entity.DirectMessage;
import com.mentorplatform.Backend.repository.DirectMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.Map;

import java.util.List;

@Service
public class DirectMessageService {

    @Autowired
    private DirectMessageRepository messageRepository;

    // 1. Save a new message to the database
    public DirectMessage saveMessage(String sender, String recipient, String content) {
        DirectMessage message = new DirectMessage();
        message.setSenderEmail(sender);
        message.setRecipientEmail(recipient);
        message.setContent(content);
        message.setRead(false); // Unread by default
        return messageRepository.save(message);
    }

    // 2. Load the full chat history between two people
    public List<DirectMessage> getChatHistory(String userA, String userB) {
        return messageRepository.findConversationHistory(userA, userB);
    }

    // 3. Get the unread count for the notification bell
    public long getUnreadCount(String userEmail) {
        return messageRepository.countByRecipientEmailAndIsReadFalse(userEmail);
    }

    // 4. When User A opens the chat with User B, mark all User B's messages as "Read"
    @Transactional
    public void markConversationAsRead(String senderEmail, String recipientEmail) {
        List<DirectMessage> unreadMessages = messageRepository
                .findBySenderEmailAndRecipientEmailAndIsReadFalse(senderEmail, recipientEmail);

        for (DirectMessage msg : unreadMessages) {
            msg.setRead(true);
        }
        messageRepository.saveAll(unreadMessages);
    }
    public Map<String, Long> getUnreadCountsPerSender(String userEmail) {
        List<Object[]> results = messageRepository.countUnreadPerSender(userEmail);
        Map<String, Long> unreadMap = new HashMap<>();
        for (Object[] result : results) {
            unreadMap.put((String) result[0], (Long) result[1]);
        }
        return unreadMap;
    }
}