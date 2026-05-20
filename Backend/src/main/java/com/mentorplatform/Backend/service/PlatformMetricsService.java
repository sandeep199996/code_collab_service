package com.mentorplatform.Backend.service;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PlatformMetricsService {

    //  it won't crash if 100 users connect at the exact same millisecond!
    private final AtomicInteger activeWebSocketConnections = new AtomicInteger(0);

    //  to track exactly who is online and busy
    private final ConcurrentHashMap<String, String> userStatuses = new ConcurrentHashMap<>();

    // --- 1. WEBSOCKET RADAR ---
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        activeWebSocketConnections.incrementAndGet();
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        // Prevent the counter from dropping below zero during server restarts
        if (activeWebSocketConnections.get() > 0) {
            activeWebSocketConnections.decrementAndGet();
        }
    }

    public int getActiveWebSockets() {
        return activeWebSocketConnections.get();
    }

    // --- 2. WEBRTC TUNNEL RADAR ---
    // ( method from the PresenceController whenever a user changes their status!)
    public void updateUserStatus(String email, String status) {
        if ("OFFLINE".equals(status)) {
            userStatuses.remove(email);
        } else {
            userStatuses.put(email, status);
        }
    }

    public int getActiveEncryptedTunnels() {
        // TO Count how many users are currently marked as "BUSY"
        long busyUsers = userStatuses.values().stream()
                .filter(status -> "BUSY".equals(status))
                .count();

        // Since every private session requires exactly 2 people, we divide the count of busy users by 2 to get the number of active tunnels
        return (int) (busyUsers / 2);
    }
}