package com.niit.controllers;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.niit.dao.UserDao;
import com.niit.model.ErrorClazz;
import com.niit.model.User;
@Controller
public class UserController {
	@Autowired
private UserDao userDao;
	public UserController(){
		System.out.println("UserController bean is created");
	}
	//? - Any Type[ErrorClazz   /User object]
	@RequestMapping(value="/registeruser",method=RequestMethod.POST)
	public ResponseEntity<?> registerUser(@RequestBody User user){// user is from frontend
		// check for duplicate email
		//if email is not unique, return ErrorClazz object
		//if email is unique, then call registerUser method
		System.out.println("registerUser in UserController" + user);//call toString method in User class
		if(!userDao.isEmailUnique(user.getEmail())){
			ErrorClazz error=new ErrorClazz(1,"Email already exist..please enter different email address");
					return new ResponseEntity<ErrorClazz>(error,HttpStatus.CONFLICT);
							
		}
		try{
		userDao.registerUser(user);//insert
		}
		catch(Exception e){
			ErrorClazz error=new ErrorClazz(2,"Some required fields may be empty.."+e.getMessage());
			return new ResponseEntity<ErrorClazz>(error,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<User>(user,HttpStatus.OK);
		}
	// login
	@RequestMapping(value="/login",method=RequestMethod.POST)
	public ResponseEntity<?> login(@RequestBody User user, HttpSession session){
		System.out.println(user);
	/*	System.out.println("We are inside user ctrl login........");*/
		User validUser=userDao.login(user);
		System.out.println(validUser);
		if(validUser==null){//invalid credentials
			ErrorClazz error=new ErrorClazz(5,"Login failed.. Invaild email/password..");//response.data
			return new ResponseEntity<ErrorClazz>(error,HttpStatus.UNAUTHORIZED);			
		}
		else{//valid credentials
			validUser.setOnline(true);
			userDao.update(validUser);
			session.setAttribute("loginId", user.getEmail());
			return new ResponseEntity<User>(validUser,HttpStatus.OK);
		}
	}
	@RequestMapping(value="/logout", method=RequestMethod.PUT)
	public ResponseEntity<?> logout(HttpSession session){
		String email=(String)session.getAttribute("loginId");
		if(email==null){
			ErrorClazz error=new ErrorClazz(4,"Please login...");
			return new ResponseEntity<ErrorClazz>(error,HttpStatus.UNAUTHORIZED);
		}
		User user=userDao.getUser(email);
		user.setOnline(false);
		userDao.update(user);//update onlinestatus to false
		session.removeAttribute("loginId");
		session.invalidate();
		return new ResponseEntity<User>(user,HttpStatus.OK);
	}
		@RequestMapping(value="/getuser",method=RequestMethod.GET)
		public ResponseEntity<?> getUser(HttpSession session){
			String email=(String)session.getAttribute("loginId");
			if(email==null){
				ErrorClazz error=new ErrorClazz(5,"Unauthorized access....");
				return new ResponseEntity<ErrorClazz>(error,HttpStatus.UNAUTHORIZED);
			}
			User user=userDao.getUser(email);
			return new ResponseEntity<User>(user,HttpStatus.OK);
		}
		@RequestMapping(value="/updateuser",method=RequestMethod.PUT)
		public ResponseEntity<?> updateUser(@RequestBody User user,HttpSession session){
			String email=(String)session.getAttribute("loginId");
			if(email==null){//not logged in,loginId returns null..
				ErrorClazz error=new ErrorClazz(5,"Unauthorized access....");
				return new ResponseEntity<ErrorClazz>(error,HttpStatus.UNAUTHORIZED);
			}
			try{
				userDao.update(user);
				return new ResponseEntity<User>(user,HttpStatus.OK);
			}catch(Exception e){
				ErrorClazz error=new ErrorClazz(5,"Unable to update user details.."+e.getMessage());
				return new ResponseEntity<ErrorClazz>(error,HttpStatus.INTERNAL_SERVER_ERROR);
				
			}
			
		}
		
	/*	@RequestMapping(value="/test")
		public String test(){
			
			return "this is testing method";
		}*/
}
