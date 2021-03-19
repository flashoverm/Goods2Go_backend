package com.goods2go.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Configuration
@EnableWebSocketMessageBroker
public class MessageBrokerConfig extends AbstractWebSocketMessageBrokerConfigurer {
	
	public static final int NOTIFICATION_TIMEOUT_MIN = 60 * 24 * 2; //48 hours
	public static final String MSG_TARGET_USER = "/queue/notification";
	
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app", "/user");
        config.setUserDestinationPrefix("/user"); 
    }
    
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/notifications").setAllowedOrigins("*");
        registry.addEndpoint("/notifications").setAllowedOrigins("*").withSockJS();
	}

    @EventListener
    public void handleConnectEvent(SessionConnectEvent event) {
		//StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
		System.out.println(event.getUser().getName() + " connected: " + event.getMessage());	
	}

    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
		//StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
		System.out.println(event.getUser().getName() + " subscribed: " + event.getMessage());	
    }
    
    @EventListener
    public void handleDisconnectEvent(SessionDisconnectEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
		String userHeader = headerAccessor.getHeader("simpUser").toString();
		String user = userHeader.split(";")[0].split(":")[2].replaceAll(" ", "");
		
		System.out.println(user + " disconnected: " + event.getMessage());	
	}
}
