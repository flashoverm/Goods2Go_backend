package com.goods2go.models.util;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.goods2go.config.MailConfig;
import com.goods2go.models.User;
import com.goods2go.models.VerificationToken;

public class Mail {
	
	static String emailtemplateBeginning = "<!DOCTYPE html><html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\"><head>  <title></title>  <!--[if !mso]><!-- -->  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">  <!--<![endif]--><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><style type=\"text/css\">  #outlook a { padding: 0; }  .ReadMsgBody { width: 100%; }  .ExternalClass { width: 100%; }  .ExternalClass * { line-height:100%; }  body { margin: 0; padding: 0; -webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; }  table, td { border-collapse:collapse; mso-table-lspace: 0pt; mso-table-rspace: 0pt; }  img { border: 0; height: auto; line-height: 100%; outline: none; text-decoration: none; -ms-interpolation-mode: bicubic; }  p { display: block; margin: 13px 0; }</style><!--[if !mso]><!--><style type=\"text/css\">  @media only screen and (max-width:480px) {    @-ms-viewport { width:320px; }    @viewport { width:320px; }  }</style><!--<![endif]--><!--[if mso]><xml>  <o:OfficeDocumentSettings>    <o:AllowPNG/>    <o:PixelsPerInch>96</o:PixelsPerInch>  </o:OfficeDocumentSettings></xml><![endif]--><!--[if lte mso 11]><style type=\"text/css\">  .outlook-group-fix {    width:100% !important;  }</style><![endif]--><!--[if !mso]><!-->    <link href=\"https://fonts.googleapis.com/css?family=Lato\" rel=\"stylesheet\" type=\"text/css\">    <style type=\"text/css\">        @import url(https://fonts.googleapis.com/css?family=Lato);    </style>  <!--<![endif]--><style type=\"text/css\">  @media only screen and (min-width:480px) {    .mj-column-per-100 { width:100%!important; }  }</style></head><body style=\"background: #FFFFFF;\">    <div class=\"mj-container\" style=\"background-color:#FFFFFF;\"><!--[if mso | IE]>      <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" align=\"center\" style=\"width:600px;\">        <tr>          <td style=\"line-height:0px;font-size:0px;mso-line-height-rule:exactly;\">      <![endif]--><table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" style=\"background:#49a6e8;font-size:0px;width:100%;\" border=\"0\"><tbody><tr><td><div style=\"margin:0px auto;max-width:600px;\"><table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-size:0px;width:100%;\" align=\"center\" border=\"0\"><tbody><tr><td style=\"text-align:center;vertical-align:top;direction:ltr;font-size:0px;padding:0px 0px 0px 0px;\"><!--[if mso | IE]>      <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">        <tr>          <td style=\"vertical-align:top;width:600px;\">      <![endif]--><div class=\"mj-column-per-100 outlook-group-fix\" style=\"vertical-align:top;display:inline-block;direction:ltr;font-size:13px;text-align:left;width:100%;\"><table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\"><tbody><tr><td style=\"word-wrap:break-word;font-size:0px;\"><div style=\"font-size:1px;line-height:50px;white-space:nowrap;\">&#xA0;</div></td></tr><tr><td style=\"word-wrap:break-word;font-size:0px;padding:0px 0px 0px 0px;\" align=\"center\"><table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse:collapse;border-spacing:0px;\" align=\"center\" border=\"0\"><tbody><tr><td style=\"width:162px;\"><img alt=\"\" title=\"\" height=\"auto\" src=\"https://topolio.s3-eu-west-1.amazonaws.com/uploads/5b65877e6e128/1533384149.jpg\" style=\"border:none;border-radius:0px;display:block;font-size:13px;outline:none;text-decoration:none;width:100%;height:auto;\" width=\"162\"></td></tr></tbody></table></td></tr><tr><td style=\"word-wrap:break-word;font-size:0px;padding:0px 20px 0px 20px;\" align=\"center\"><div style=\"cursor:auto;color:#FFFFFF;font-family:Lato, Tahoma, sans-serif;font-size:14px;line-height:22px;text-align:center;\"><h1 style=\"font-family: &apos;Cabin&apos;, sans-serif; color: #FFFFFF; font-size: 32px; line-height: 100%;\"><span style=\"color:#ffffff;\">Goods2Go</span></h1></div></td></tr><tr><td style=\"word-wrap:break-word;font-size:0px;\"><div style=\"font-size:1px;line-height:50px;white-space:nowrap;\">&#xA0;</div></td></tr></tbody></table></div><!--[if mso | IE]>      </td></tr></table>      <![endif]--></td></tr></tbody></table></div></td></tr></tbody></table><!--[if mso | IE]>      </td></tr></table>      <![endif]-->      <!--[if mso | IE]>      <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" align=\"center\" style=\"width:600px;\">        <tr>          <td style=\"line-height:0px;font-size:0px;mso-line-height-rule:exactly;\">      <![endif]--><table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-size:0px;width:100%;\" border=\"0\"><tbody><tr><td><div style=\"margin:0px auto;max-width:600px;\"><table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-size:0px;width:100%;\" align=\"center\" border=\"0\"><tbody><tr><td style=\"text-align:center;vertical-align:top;direction:ltr;font-size:0px;padding:9px 0px 9px 0px;\"><!--[if mso | IE]>      <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">        <tr>          <td style=\"vertical-align:top;width:600px;\">      <![endif]--><div class=\"mj-column-per-100 outlook-group-fix\" style=\"vertical-align:top;display:inline-block;direction:ltr;font-size:13px;text-align:left;width:100%;\"><table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\"><tbody><tr><td style=\"word-wrap:break-word;font-size:0px;padding:0px 20px 0px 20px;\" align=\"center\"><div style=\"cursor:auto;color:#000000;font-family:Arial, sans-serif;font-size:11px;line-height:22px;text-align:center;\"><p><span style=\"font-size: 14px;\">";
	static String emailtemplateEnd = "</span></p></div></td></tr><tr><td style=\"word-wrap:break-word;font-size:0px;padding:10px 25px;padding-top:10px;padding-bottom:10px;padding-right:54px;padding-left:54px;\"><p style=\"font-size:1px;margin:0px auto;border-top:1px solid #868686;width:100%;\"></p><!--[if mso | IE]><table role=\"presentation\" align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-size:1px;margin:0px auto;border-top:1px solid #868686;width:100%;\" width=\"600\"><tr><td style=\"height:0;line-height:0;\"> </td></tr></table><![endif]--></td></tr><tr><td style=\"word-wrap:break-word;font-size:0px;padding:0px 20px 0px 20px;\" align=\"center\"><div style=\"cursor:auto;color:#949494;font-family:Lato, Tahoma, sans-serif;font-size:14px;line-height:22px;text-align:center;\"><p><span style=\"font-size:16px;\"><strong>Goods2Go</strong></span></p><p><span style=\"font-size:14px;\">Next Level Shipping</span></p></div></td></tr><tr><td style=\"word-wrap:break-word;font-size:0px;\"><div style=\"font-size:1px;line-height:29px;white-space:nowrap;\">&#xA0;</div></td></tr></tbody></table></div><!--[if mso | IE]>      </td></tr></table>      <![endif]--></td></tr></tbody></table></div></td></tr></tbody></table><!--[if mso | IE]>      </td></tr></table>      <![endif]--></div></body></html>";
	
    private static String getVerificationLink(VerificationToken token) {
    	return MailConfig.SERVERADDRESS+ MailConfig.VERIFICATION_URL + token.getToken();
    }
			
    @SuppressWarnings("unused")
	private static void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage(); 
        message.setFrom(MailConfig.SENDERADDRESS);
        message.setTo(to); 
        message.setSubject(subject); 
        message.setText(text);
        MailConfig.getJavaMailSender().send(message);
    }
    
    public static void sendCustomVerificationMail(VerificationToken token, User user) {
    	
    	String html = ""
    			+ emailtemplateBeginning
    			+ "<h2>Goods2Go Email Confirmation</h2>"
    			+ "<p>Hi " + user.getEmail() + ",</p>"
    			+ "<p>Please click on the link below to confirm that you are the owner of this email address.</p>"
    			+ "<a href=\"" +  getVerificationLink(token) + "\">"
    			+ "Confirm Email"
    			+ "</a>"
    			+ "<p>If you have not signed up at Goods2Go you can ignore this mail </p>"
    			+ emailtemplateEnd;
    	
    	try {
			Mail.sendHtmlMessage(user.getEmail(), "Goods2Go Email Confirmation", html);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
    }
    
    public static void sendDelivererVerifiedMail(User user) {
    	
    	String html = ""
    			+ emailtemplateBeginning
    			+ "<h2>Goods2Go Deliverer Verification</h2>"
    			+ "<p>Hi " + user.getEmail() + ",</p>"
    			+ "<p>your identification is checked and accepted.</p>"
    			+ "<p>You are a deliverer now. Congratulations.</p>"
    			+ emailtemplateEnd;
    	
    	try {
			Mail.sendHtmlMessage(user.getEmail(), "Goods2Go Deliverer Verification", html);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
    }
    
    public static void sendDelivererNotVerifiedMail(User user) {
    	
    	String html = ""
    			+ emailtemplateBeginning
    			+ "<h2>Goods2Go Deliverer Verification</h2>"
    			+ "<p>Hi " + user.getEmail() + ",</p>"
    			+ "<p>Unfortunatley we could not verify you as a deliverer. </p>"
    			+ "<p>You can still use the service as a sender.</p>"
    			+ emailtemplateEnd;
    	
    	try {
			Mail.sendHtmlMessage(user.getEmail(), "Goods2Go Deliverer Verification", html);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
    }
    
    public static void sendUserUnblockedMail(User user) {
    	
    	String html = ""
    			+ emailtemplateBeginning
    			+ "<h2>Goods2Go User Management</h2>"
    			+ "<p>Hi " + user.getEmail() + ",</p>"
    			+ "<p>your account was unblocked by our administrators.</p>"
    			+ "<p>You can now login again.</p>"
    			+ emailtemplateEnd;
    	
    	try {
			Mail.sendHtmlMessage(user.getEmail(), "Goods2Go User Management", html);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
    }
    
    public static void sendUserBlockedMail(User user) {
    	
    	String html = ""
    			+ emailtemplateBeginning
    			+ "<h2>Goods2Go User Management</h2>"
    			+ "<p>Hi " + user.getEmail() + ",</p>"
    			+ "<p>your account was blocked by our administrators.</p>"
    			+ "<p>You are now not able to log in.</p>"
    			+ "<p>Please conntact our team for further informations.</p>"
    			+ emailtemplateEnd;
    	
    	try {
			Mail.sendHtmlMessage(user.getEmail(), "Goods2Go User Management", html);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
    }

    private static void sendHtmlMessage(String to, String subject, String htmlContent) throws MessagingException {
    	JavaMailSender sender = MailConfig.getJavaMailSender();
    	MimeMessage mimeMsg = sender.createMimeMessage();
    	
    	MimeMessageHelper msg = new MimeMessageHelper(mimeMsg, false, "utf-8");
        msg.setSubject(subject);
        msg.setFrom(MailConfig.SENDERADDRESS);
        msg.setTo(to);
        
        mimeMsg.setContent(htmlContent, "text/html; charset=utf-8");
        
        sender.send(mimeMsg);
    }

}
