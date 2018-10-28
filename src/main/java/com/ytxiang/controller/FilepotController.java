package com.ytxiang.controller;

import com.ytxiang.bean.UserBean;
import com.ytxiang.dto.UserDTO;
import com.ytxiang.service.FilepotService;

import javax.xml.bind.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;

@Controller
public class FilepotController {
	
	@Autowired
	FilepotService filepotService;
	
    @RequestMapping(method = RequestMethod.GET)
    public String init(Model model) {
        model.addAttribute("msg", "Please Enter Your Login Details");
        return "login";
    }

	@RequestMapping(value = "home", method = RequestMethod.POST)
	public String login(Model model, @ModelAttribute("userBean") UserBean userBean) {
		if (userBean != null && userBean.getUserName() != null && userBean.getPassword() != null) {
			UserDTO u = filepotService.login(userBean.getUserName(), userBean.getPassword());
			if (u != null) {
				model.addAttribute("msg", "You have successfully logged in.");
				model.addAttribute("username", u.getUserName());
				model.addAttribute("displayname", u.getFullName());
				return "home";
			} else {
				model.addAttribute("error", "Invalid Details");
				return "login";
			}
		} else {
			model.addAttribute("error", "Please enter Details");
			return "login";
		}
	}

	@PostMapping(value = "upload") 
	public String fileUpload(Model model,
			@RequestParam(value = "file", required = true) MultipartFile file,
			@RequestParam(value = "notes", required = false) String notes,
			@RequestParam(value = "username", required = true) String userName
	                              /* RedirectAttributes redirectAttributes */) {
	    if (file.isEmpty()) {
	        //redirectAttributes.addFlashAttribute("error", "Please select a file to upload");
	        return "redirect:/login";
	    }
	    
	    try {
	        // Get the file and try to save it to S3
	    	filepotService.upload(file, notes, userName);
		    model.addAttribute("msg",
		                "You successfully uploaded '" + file.getOriginalFilename() + "'");
		    model.addAttribute("username", userName);
	    } catch (ValidationException e) {
		    model.addAttribute("error", e.getMessage());
	    	return "home";
	    } catch (Exception e) {
	    	return "redirect:/login";
	    }
	    return "home";
	}
}