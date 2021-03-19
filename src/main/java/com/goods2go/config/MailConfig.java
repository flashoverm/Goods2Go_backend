package com.goods2go.config;

import java.util.Properties;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class MailConfig {
	
	public static final int MAIL_VERIFICATION_VALIDITY_MIN = 60 * 24;	//24 hours

	public static String  SERVERADDRESS;
	
	public static String WEBSERVERADDRESS;

	public static String VERIFICATION_PATH;
		
	public static String VERIFICATION_URL;
	
	//public static String VERIFICATION_LINK = SERVERADDRESS + VERIFICATION_URL;
	
	public static String VERIFICATION_REQUEST_PATH;
	
	public static String VERIFICATION_REQUEST_URL;
	
	public static String SENDERADDRESS;
	
	

	public static JavaMailSender getJavaMailSender() {
	    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
	    
	    mailSender.setHost("host33.checkdomain.de");
	    mailSender.setPort(587);
	    mailSender.setUsername("thral6");
	    mailSender.setPassword("goods2go");
	     
	    Properties props = mailSender.getJavaMailProperties();
	    props.put("mail.transport.protocol", "smtp");
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.starttls.enable", "true");
	    props.put("mail.debug", "true");
	     
	    return mailSender;
	}
	
}
