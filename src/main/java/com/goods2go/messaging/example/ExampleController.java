package com.goods2go.messaging.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.goods2go.models.util.DateTime;

@RestController
public class ExampleController {

    @Autowired
    private SimpMessagingTemplate template;

	
	/**
	 * a client sends a greeting message to destination /app/notification. 
	 * The message is routed to NotificationController, 
	 * which enriches the greeting with a timestamp 
	 * and sends a new message to the broker with destination /topic/notification. 
	 * The broker then broadcasts the message to all subscribed, connected clients.
	 */
	@MessageMapping("/chat/{userid}")
	public OutputPushMessage recieve(PushMessage message) throws Exception {
		System.out.println("Message: \n" + message.getText());
		return new OutputPushMessage(message, DateTime.getCurrentDateTime().toString());
	}

	/**
	 *	Forwards message to a specific target of the broker: /topic/notification/{userid} 
	 *	Gets variable from destination path and 
	 */
	@MessageMapping("/chat")
	@SendTo("/topic/messages")
	public OutputPushMessage recieveAndRoute(PushMessage message) throws Exception {
		System.out.println("Message: \n" + message.getText());
		return new OutputPushMessage(message, DateTime.getCurrentDateTime().toString());
	}
	
	/** Doesn't work!!!
	 * 
	 * An @SubscribeMapping annotation can also be used to map subscription requests to @Controller methods. 
	 * It is supported on the method level, but can also be combined with a type level @MessageMapping annotation 
	 * that expresses shared mappings across all message handling methods within the same controller.
	 * 
	 * By default the return value from an @SubscribeMapping method is sent as a message directly back to the connected client and 
	 * does not pass through the broker. This is useful for implementing request-reply message interactions; 
	 * for example, to fetch application data when the application UI is being initialized. Or alternatively 
	 * an @SubscribeMapping method can be annotated with @SendTo in which case the resulting message is sent to the 
	 * "brokerChannel" using the specified target destination.
	 */
	@SubscribeMapping("/chat")
	@SendTo("/topic/messages")
	public String subscribe(@Payload String message) throws Exception {
		System.out.println("Subscribe Message: \n" + message);
		return "";
	}
	
	/**
	 * Any application component can send messages to the "brokerChannel". 
	 * The easist way to do that is to have a SimpMessagingTemplate injected, 
	 * and use it to send messages. 
	 * Typically it should be easy to have it injected by type, for example:
	 */
    @RequestMapping(value="/notification/send", method=RequestMethod.GET)
    public void greet(PushMessage message) {
    	OutputPushMessage newMessage = new OutputPushMessage(message, DateTime.getCurrentDateTime().toString());
        
    	this.template.convertAndSend("/topic/messages", newMessage);
    }
	
}

