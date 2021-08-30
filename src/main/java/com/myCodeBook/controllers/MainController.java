package com.myCodeBook.controllers;


import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.myCodeBook.dao.UserRepository;
import com.myCodeBook.entities.Contact;
import com.myCodeBook.entities.User;
import com.myCodeBook.helper.Message;

@Controller
public class MainController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder encoder;
	
@GetMapping("/")	
 public String home(Model model) {
	model.addAttribute("title", "Home");
	 return "home";
 }

@GetMapping("/about")	
public String about(Model model) {
	model.addAttribute("title", "about");
	 return "about";
}



@RequestMapping("/signup")	
public String signup(Model model) {
	model.addAttribute("title", "signup");
	model.addAttribute("user", new User());
	 return "signup";
}

@RequestMapping(value = "/do_register",method = RequestMethod.POST)
public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult,
		@RequestParam(value="agreement",defaultValue = "false") boolean agreement ,
		Model model,  HttpSession session) {
	
	try {
		
		if(!agreement) {
			throw new Exception("You have not agreed the term and conditions");
		}
		
		if(bindingResult.hasErrors()) {
			System.out.println(bindingResult);
			model.addAttribute("user", user);
			session.setAttribute("message", new Message(String.valueOf(bindingResult.getErrorCount())+" Errors Try Again ! ","alert-danger"));
			return "signup";
		}
		
		
		user.setRole("ROLE_USER");
		user.setEnabled(true);
		user.setImageUrl("default.png");
	    user.setPassword(encoder.encode(user.getPassword()));
	    userRepository.save(user);
	   model.addAttribute("user", new User());
	   session.setAttribute("message", new Message("Successfully Registered","alert-success"));
	   
	}catch(Exception e) {
		e.printStackTrace();
		model.addAttribute("user", user);
		session.setAttribute("message", new Message("Something went wrong !!" + e.getMessage(),"alert-danger"));
	}
	
	return "signup";
}

@GetMapping("/signIn")
public String login() {
	return "loginForm";
}
}
