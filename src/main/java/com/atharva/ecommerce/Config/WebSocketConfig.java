package com.atharva.ecommerce.Config;

import com.atharva.ecommerce.Controller.GeminiWebSocketHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {


    private final GeminiWebSocketHandler geminiWebSocketHandler;



    public WebSocketConfig(GeminiWebSocketHandler geminiWebSocketHandler) {
        this.geminiWebSocketHandler = geminiWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(geminiWebSocketHandler, "/chat").setAllowedOrigins("*");
    }
}
