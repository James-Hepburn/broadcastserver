package com.example.broadcastserver.server;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class BroadcastHandler extends TextWebSocketHandler {
    private Set <WebSocketSession> sessions = new CopyOnWriteArraySet <>();

    @Override
    public void afterConnectionEstablished (WebSocketSession session) throws Exception {
        this.sessions.add (session);
        System.out.println ("Client connected: " + session.getId ());
    }

    @Override
    protected void handleTextMessage (WebSocketSession session, TextMessage message) throws Exception {
        System.out.println ("Received message: " + message.getPayload () + " from " + session.getId ());
        broadcast (message);
    }

    @Override
    public void afterConnectionClosed (WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        this.sessions.remove (session);
        System.out.println ("Client disconnected: " + session.getId ());
    }

    public void broadcast (TextMessage message) throws IOException {
        for (WebSocketSession s : sessions) {
            if (s.isOpen ()) {
                s.sendMessage(message);
            }
        }
    }
}
