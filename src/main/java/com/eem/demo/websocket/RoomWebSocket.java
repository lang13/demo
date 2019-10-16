package com.eem.demo.websocket;

import org.springframework.stereotype.Component;

import javax.websocket.server.ServerEndpoint;

/**
 * @author Administrator
 */
@ServerEndpoint("/websocket/room/{roomName}/{username}")
@Component
public class RoomWebSocket {
}
