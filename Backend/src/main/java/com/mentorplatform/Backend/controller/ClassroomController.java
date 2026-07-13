package com.mentorplatform.Backend.controller;

import com.mentorplatform.Backend.dto.ClassroomLaunchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Collection;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/classroom")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class ClassroomController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    // To Store active rooms
    private final Map<String, Map<String, Object>> activeClassrooms = new ConcurrentHashMap<>();

    //  Only an ADMIN can launch a classroom
    @PostMapping("/launch")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> launchClassroom(@RequestBody ClassroomLaunchRequest request) {
        try {
            //  unique Room ID
            String roomId = UUID.randomUUID().toString();

            //  Invitation Payload
            Map<String, Object> invitation = new HashMap<>();
            invitation.put("type", "CLASSROOM_INVITE");
            invitation.put("roomId", roomId);
            invitation.put("topic", request.getTopic());
            invitation.put("mentor", request.getMentorEmail());
            invitation.put("participants", request.getMenteeEmails().size() + 1);
            activeClassrooms.put(roomId, invitation);
            // To Fire the targeted WebSocket message to the Mentor
            messagingTemplate.convertAndSend(
                    "/topic/users/" + request.getMentorEmail() + "/invites",
                    invitation
            );

            // TO Fire the targeted WebSocket messages to every Mentee
            for (String menteeEmail : request.getMenteeEmails()) {
                messagingTemplate.convertAndSend(
                        "/topic/users/" + menteeEmail + "/invites",
                        invitation
                );
            }

            // TO Tell the Admin Dashboard that the launch was successful
            return ResponseEntity.ok(Map.of(
                    "message", "Classroom launched successfully.",
                    "roomId", roomId
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
    // 2. View Active Classrooms
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Collection<Map<String, Object>>> getActiveClassrooms() {
        return ResponseEntity.ok(activeClassrooms.values());
    }

    // 3. The Kill Switch
    @PostMapping("/terminate/{roomId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> terminateClassroom(@PathVariable String roomId) {
        if (activeClassrooms.containsKey(roomId)) {
            activeClassrooms.remove(roomId);
            // Blast the kill command to the room's channel
            messagingTemplate.convertAndSend("/topic/room/" + roomId + "/commands", Map.of("command", "TERMINATE"));
            return ResponseEntity.ok(Map.of("message", "Room terminated successfully."));
        }
        return ResponseEntity.status(404).body(Map.of("error", "Room not found."));
    }

}