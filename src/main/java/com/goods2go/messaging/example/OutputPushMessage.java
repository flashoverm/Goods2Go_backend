package com.goods2go.messaging.example;

import java.io.Serializable;

import lombok.Data;

@Data
public class OutputPushMessage implements Serializable{

	private static final long serialVersionUID = 1L;
	
    private String from;
    private String text;
    private String timestamp;
    
	public OutputPushMessage() {
		
	}
	
	public OutputPushMessage(PushMessage message, String timestamp) {
		this.from = message.getFrom();
		this.text = message.getText();
		this.timestamp = timestamp;
	}
}
