package com.myCodeBook.controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.myCodeBook.dao.ContactRepository;
import com.myCodeBook.dao.UserRepository;
import com.myCodeBook.entities.Contact;
import com.myCodeBook.entities.User;
import com.myCodeBook.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	
	@ModelAttribute
	public void commonData(Model model, Principal principal) {
		String username = principal.getName();
		User user = userRepository.getUserByUserName(username);
		model.addAttribute("user", user);
	}
	
	@RequestMapping("/index")
	public String index(Model model) {
		model.addAttribute("title", "Dashboad");
		return "normal/index";
	}
	
	
	@GetMapping("/add-contact")
	public String addContact(Model model) {
		model.addAttribute("title", "Add contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact";
	}
	
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact,@RequestParam("Profileimage") MultipartFile image, Principal principal , HttpSession session) {
	
		try {
		String username =  principal.getName();
		User user  =  this.userRepository.getUserByUserName(username);
		if(image.isEmpty()) {
			contact.setImage("default.png");
		}else {
			contact.setImage(image.getOriginalFilename());
			File file = new ClassPathResource("static/image").getFile();
			Path path = Paths.get(file.getAbsolutePath()+File.separator+image.getOriginalFilename());
			//System.out.println(path);
			Files.copy(image.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);

		}
		contact.setUser(user);
		user.getContacts().add(contact);
		this.userRepository.save(user);
		session.setAttribute("message", new Message("Successfully Added !","success"));
		System.out.println(contact);
		}
		catch(Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Message("Something went wrong !","danger"));
		}
		return "normal/add_contact";
		
	}

	
	@RequestMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page ,Model model, Principal principal) {
		model.addAttribute("title", "Show Contacts");
		String name = principal.getName();
		User userByUserName = this.userRepository.getUserByUserName(name);
		Pageable pageable = PageRequest.of(page, 5);
		Page<Contact> list = this.contactRepository.findContactsByUser(userByUserName.getId(),pageable);
		model.addAttribute("contacts", list);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPage", list.getTotalPages());
		return "normal/show_contacts";
	}
	
	@RequestMapping("/contact-profile/{cid}")
	public String showContactDetails(@PathVariable("cid") int cid, Model model, Principal principal) {
		Optional<Contact> optional = this.contactRepository.findById(cid);
		Contact contact = optional.get();
		String name = principal.getName();
		User user = this.userRepository.getUserByUserName(name);
		if(user.getId()==contact.getUser().getId()) {
			model.addAttribute("title", "contact-Details");
			model.addAttribute("contact", contact);
		}
		return "normal/contact_details";
	}
	
	@RequestMapping("{page}/delete-contact/{cid}")
	public String deleteContact(@PathVariable("cid") int cid, @PathVariable("page") int page, Model model, Principal principal) {
		String name = principal.getName();
		User user = this.userRepository.getUserByUserName(name);
		Contact contact = this.contactRepository.findById(cid).get();
		if(user.getId()==contact.getUser().getId()) {
			contact.setUser(null);
			this.contactRepository.delete(contact);
			model.addAttribute("title", "show-contacts");
		}
		return "redirect:/user/show-contacts/"+page;
	}
	
	@PostMapping("/update-contact/{cid}")
	public String updateContact(@PathVariable("cid") int cid, Model model) {
		model.addAttribute("title", "update-contact");
		Contact contact = this.contactRepository.findById(cid).get();
		model.addAttribute("contact", contact);
		return "normal/update_contact";
	}
	
	@PostMapping("/process-update")
	public String processUpdateData(@ModelAttribute Contact contact, @RequestParam("Profileimage") MultipartFile file, 
			Principal principal , HttpSession session, Model model) {
		
		Contact oldContact = this.contactRepository.findById(contact.getCid()).get();
		User userByUserName = this.userRepository.getUserByUserName(principal.getName());
		try {
			if(!file.isEmpty()) {
				
				if(!oldContact.getImage().equals("default.png")) {
					 File deleteFilePath = new ClassPathResource("static/image").getFile();
					File filer = new File(deleteFilePath,oldContact.getImage());
					filer.delete();
			}
				
				File saveFile = new ClassPathResource("static/image").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				//System.out.println(path);
				Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
			}
			else {
				contact.setImage(oldContact.getImage());
			}
			contact.setUser(userByUserName);
			this.contactRepository.save(contact);
			session.setAttribute("message", new Message("Successfully Updated...","success"));
		}catch(Exception e) {
			session.setAttribute("message", new Message("Something went wrong...","danger"));
			e.printStackTrace();
		}
		
		return "redirect:/user/contact-profile/"+contact.getCid();
	}
	
	@GetMapping("/user-profile")
	public String userProfile(Model model) {
		model.addAttribute("title", "user-profile");
		return "normal/user_profile";
	}
	
	@GetMapping("/settings")
	public String settings(Model model) {
		model.addAttribute("title", "settings");
		return "normal/settings";
	}
	
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword")String oldPassword, 
			@RequestParam("newPassword")String newPassword,
			Principal principal, HttpSession session) {
		
		
		User user = this.userRepository.getUserByUserName(principal.getName());
		if(bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
			user.setPassword(bCryptPasswordEncoder.encode(newPassword));
			this.userRepository.save(user);
			session.setAttribute("message", new Message("Password Changed Successfully","success"));
		}
		else {
			session.setAttribute("message", new Message("Old Password is incorrect ! Try Again","danger"));
			return "redirect:/user/settings";
		}
		
		return "redirect:/user/index";
	}
}
