package com.myCodeBook.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.myCodeBook.dao.UserRepository;
import com.myCodeBook.entities.User;

public class UserDetailServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository repository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userByUserName = repository.getUserByUserName(username);
        if(userByUserName==null) {
        	throw new UsernameNotFoundException("User is not found");
        }
        CustomUserDetails customUserDetails = new CustomUserDetails(userByUserName);
		return customUserDetails;
	}

}
