package com.mentorplatform.Backend.repository;

import com.mentorplatform.Backend.entity.DirectMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DirectMessageRepository extends JpaRepository<DirectMessage, Long> {

    // 1. Fetch a full chat history between User A and User B, sorted chronologically
    @Query("SELECT m FROM DirectMessage m WHERE " +
            "(m.senderEmail = :userA AND m.recipientEmail = :userB) OR " +
            "(m.senderEmail = :userB AND m.recipientEmail = :userA) " +
            "ORDER BY m.timestamp ASC")
    List<DirectMessage> findConversationHistory(@Param("userA") String userA, @Param("userB") String userB);

    // 2. Fetch the number of unread messages for a specific user (For the Navbar Bell)
    long countByRecipientEmailAndIsReadFalse(String recipientEmail);

    // 3. Find all unread messages from a specific sender (Used to mark them as read when the chat is opened)
    List<DirectMessage> findBySenderEmailAndRecipientEmailAndIsReadFalse(String senderEmail, String recipientEmail);

    // Used during the Account Deletion process we built in Phase 1 to wipe a user's footprint
    void deleteBySenderEmailOrRecipientEmail(String senderEmail, String recipientEmail);

    // Fetch a map of unread counts grouped by the sender
    @Query("SELECT m.senderEmail, COUNT(m) FROM DirectMessage m WHERE m.recipientEmail = :recipient AND m.isRead = false GROUP BY m.senderEmail")
    List<Object[]> countUnreadPerSender(@Param("recipient") String recipient);
}