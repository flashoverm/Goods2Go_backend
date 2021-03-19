package com.goods2go.messaging.example;

import java.io.Serializable;

import lombok.Data;

@Data
public class PushMessage implements Serializable{

	private static final long serialVersionUID = 1L;
	
    private String from;
    private String text;
    
	public PushMessage() {
		
	}
	
	public PushMessage(String from, String text) {
		this.from = from;
		this.text = text;
	}
	
}
