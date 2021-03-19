package com.goods2go.messaging.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import com.goods2go.models.util.DateTime;

/** Example usage with JavaScript: \src\main\webapp\resources
 * The user name is logged in console when user subscribes
 */
@Controller
public class NotificationController {

    @Autowired
	SimpMessageSendingOperations template;
	  
	@MessageMapping("/notification/broadcast")
	//@SendTo("/topic/broadcast")
    public void processMessageFromClient(@Payload PushMessage message, SimpMessageHeaderAccessor headerAccessor) throws Exception {
		String sessionId = headerAccessor.getSessionAttributes().get("sessionId").toString();
		System.out.println(sessionId);
		headerAccessor.setSessionId(sessionId);
		template.convertAndSend("/topic/broadcast", new OutputPushMessage(message, DateTime.getCurrentDateTime().toString()));
    }
	
	@MessageMapping("/notification/user/{to}")
    //@SendToUser(destinations="/queue/notification", broadcast=false)
    public void processMessageFromClientToUser(@DestinationVariable String to, @Payload PushMessage message, SimpMessageHeaderAccessor headerAccessor) throws Exception {
		//String sessionId = headerAccessor.getSessionAttributes().get("sessionId").toString();
		//headerAccessor.setSessionId(sessionId);
		template.convertAndSendToUser(to, "/queue/notification", new OutputPushMessage(message, DateTime.getCurrentDateTime().toString()));
    }
}
