package com.myCodeBook.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="ContactsTable")
public class Contact {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int cid;
	private String name;
	private String secondName;
	private String phone;
	private String email;
	private String work;
	private String image;
	private String description;
	
	@ManyToOne
	@JsonIgnore
	private User user;
	public int getCid() {
		return cid;
	}
	public Contact(int cid, String name, String secondName, String phone, String email, String work, String image,
			String description, User user) {
		super();
		this.cid = cid;
		this.name = name;
		this.secondName = secondName;
		this.phone = phone;
		this.email = email;
		this.work = work;
		this.image = image;
		this.description = description;
		this.user = user;
	}
	public Contact() {
		super();
		// TODO Auto-generated constructor stub
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public void setCid(int cid) {
		this.cid = cid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSecondName() {
		return secondName;
	}
	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getWork() {
		return work;
	}
	public void setWork(String work) {
		this.work = work;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public String toString() {
		return "Contact [cid=" + cid + ", name=" + name + ", secondName=" + secondName + ", phone=" + phone + ", email="
				+ email + ", work=" + work + ", image=" + image + ", description=" + description + ", user=" + user
				+ "]";
	}

	

}
