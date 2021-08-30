package com.myCodeBook.services;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailServices {
	
     public boolean sendEmail(String message, String subject, String from, String to) {
		
    	 boolean isSend = false;
    	 
		//variable for host
		String host ="smtp.gmail.com";
		
		//get the system properties
		Properties properties = System.getProperties();
		System.out.println("Properties"+properties);
		
		//setting important properties  
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable","true");
		properties.put("mail.smtp.auth", "true");
		
		
		//Step 1.to get the session object
		Session session = Session.getInstance(properties, new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				// TODO Auto-generated method stub
				return new PasswordAuthentication("krishnamurli447@gmail.com", "9555750822");
			}
			
		});
		session.setDebug(true);
		
		//Step 2. prepare the message....
	   MimeMessage mime =  new 	MimeMessage(session);
	   
	  try {
		  //from email
		  mime.setFrom(from);
		  //add recipient to message
		  mime.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		  //adding subject to message
		  mime.setSubject(subject);
		  //adding message 
		 // mime.setText(message);
		  mime.setContent(message, "text/html");
		  //Step 3. Send Message using transport class
		  Transport.send(mime);
		  
		  System.out.println("send success...........");
		  isSend =true;
	} catch (Exception e) {
		e.printStackTrace();
	}
     
	  return isSend;
	  
     }
     
}
     


