package com.niit.controllers;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.niit.dao.ProfilePictureDao;
import com.niit.model.ErrorClazz;
import com.niit.model.ProfilePicture;

@Controller
public class ProfilePictureController {
	@Autowired
	private ProfilePictureDao profilePictureDao;
	@RequestMapping(value="/uploadprofilepic",method=RequestMethod.POST)
	public ResponseEntity<?> uploadProfilePicture(@RequestParam CommonsMultipartFile image,HttpSession session){
		//<input type="file" name="image" >
	String email=(String)session.getAttribute("loginId");
		if(email==null){
			ErrorClazz error=new ErrorClazz(6,"Unauthorized access");
			return new ResponseEntity<ErrorClazz>(error,HttpStatus.UNAUTHORIZED);
		}
		
		ProfilePicture profilePicture=new ProfilePicture();
		profilePicture.setEmail(email);
		profilePicture.setImage(image.getBytes());
		profilePictureDao.uploadProfilePicture(profilePicture);
		return new ResponseEntity<ProfilePicture>(profilePicture,HttpStatus.OK);
		
	}
	//<img src="[data]">
	// blogdetails.html [to display the profile pic of author of blogpost]
	//Friend [to display profile pic of users]
	//index.html page [profile pic of loogedin user]
	//logged in user - john.smith@xyz.com'
	// <img src="http://......./getimage/jhon.smith@xyz.com">
	// blogdetails.html -> postedBy smith.j@abc.com
	// <img src="http://......./getimage/jhon.smith@xyz.com">
	// friend  <img src="http://......./getimage/jhon.smith@xyz.com">
	@RequestMapping(value="/getimage/{email:.+}",method=RequestMethod.GET)
	public @ResponseBody byte[] getImage(@PathVariable String email,HttpSession session){
		System.out.println(email);
		String auth=(String)session.getAttribute("loginId");
		if(auth==null){
			return null;//<img src="null" alt="No image">
		}
		// auth requests profilepic of email
		ProfilePicture profilePicture=profilePictureDao.getImage(email);
		if(profilePicture==null){
			return null;
	}
	else
		return profilePicture.getImage();
	
	}
}
	
