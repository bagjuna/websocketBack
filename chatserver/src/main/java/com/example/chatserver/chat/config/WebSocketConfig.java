package com.example.chatserver.chat.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.socket.config.annotation.EnableWebSocket;
//import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
//import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
//
//@Configuration
//@EnableWebSocket
//public class WebSocketConfig implements WebSocketConfigurer {
//
//    private final SimpleWebSocketHandler simpleWebSocketHandler;
//
//    public WebSocketConfig(SimpleWebSocketHandler simpleWebSocketHandler) {
//        this.simpleWebSocketHandler = simpleWebSocketHandler;
//    }
//
//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry.addHandler(simpleWebSocketHandler, "/connect")
//                // securityconfig에서의 cors는 http요청에 대한 예외, 따라서 websocket프롵
//                .setAllowedOrigins("http://localhost:5173");
//
//    }
//
//
//}
