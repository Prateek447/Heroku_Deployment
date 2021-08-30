package com.myCodeBook.controllers;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.myCodeBook.dao.UserRepository;
import com.myCodeBook.entities.User;
import com.myCodeBook.helper.Message;
import com.myCodeBook.services.EmailServices;

@Controller
public class ForgotController {
	
     Random random = 	new Random(1000);
     
     @Autowired
     private EmailServices emailServices;
     
     @Autowired
     private UserRepository repository;
     
     @Autowired
     private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@GetMapping("/forgot-password")
	public String forgotPassword() {
		return "forgot";
	}

	@PostMapping("/send-otp")
	public String veryifyOtp(@RequestParam("email") String email, HttpSession session, Model model) {
		int otp = random.nextInt(999999999);
		System.out.println("random-->"+otp);
		String subject = "OTP FROM SMART_CONTACT_MANAGER";
		String message = "<h1> OTP = "+otp+"</h1>";
		String to = email;
		String from = "krishnamurli447@gmail.com";
		
		User user = repository.getUserByUserName(email);
		if(user==null) {
			session.setAttribute("message", new Message("This email is not registered with us !", "danger"));
			return "forgot";
		}
		
		
	  boolean isSuccess = 	emailServices.sendEmail(message, subject,from, to);
	  
	  if(isSuccess) {
		  session.setAttribute("otp", otp);
		  session.setAttribute("email", email);
		  session.setAttribute("message", new Message("We have sent you an otp check your email !", "success"));
		  return "verify_otp";
	  }
	  else {
		  session.setAttribute("message", new Message("Otp is incorrect !","danger"));
	  }
		return "forgot";
	}
	
	@PostMapping("/check-otp")
	public String checkOtp(@RequestParam("enteredOtp")String enteredOtp, HttpSession session) {
		
		System.out.println("checking otp...........");
		
		int otp = (int) session.getAttribute("otp");
		
		String genOtp =  String.valueOf(otp);
		
		if(enteredOtp.equals(genOtp)) {
			System.out.println("Otp is matched..........");
			return "change_password";
		}
		System.out.println("Otp is not macthed.........."+enteredOtp+"<--en gen-->"+genOtp);
		session.setAttribute("message", new Message("Invalid Otp | Try again", "danger"));
		return "verify_otp";
	}
	
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newPassword") String newPassword, 
			@RequestParam("reEnterNewPassword") String reEnterPassword, HttpSession session) {
		
		if(newPassword.equals(reEnterPassword)) {
			String email = (String) session.getAttribute("email");
			User user = this.repository.getUserByUserName(email);
			user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.repository.save(user);
			session.setAttribute("message", new Message("Password changed ! Please Try to login", "success"));
			return "loginForm";
		}
		session.setAttribute("message", new Message("Password doesn't Match ! Try Again","danger"));
		return "change_password";
	}

}
