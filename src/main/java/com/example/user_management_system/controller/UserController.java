package com.example.user_management_system.controller;

import com.example.user_management_system.entity.User;
import com.example.user_management_system.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class UserController {
    @Autowired
    private UserService userService;


    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try{
            userService.registerUser(user);
            redirectAttributes.addFlashAttribute("success", "Registration successful! You can now log in.");
            return "redirect:/login";
        }
        catch(IllegalArgumentException e){
            redirectAttributes.addFlashAttribute("error",e.getMessage());// Pass the error message to the view
            return "redirect:/signup";
        }


    }

    @GetMapping("/login")
    public String showLoginPage(HttpSession session, @RequestParam(required = false) String error) {
        // If there's an error parameter, allow access to show the error
        if (error != null) {
            return "login";
        }
        // Check if user is already logged in
        if (session.getAttribute("admin") != null) {
            return "redirect:/adminhome";
        }
        if (session.getAttribute("user") != null) {
            return "redirect:/customerhome";
        }

        // Not logged in, show login page
        return "login";
    }



//    @PostMapping("/login")
//    public String login(@RequestParam String username, @RequestParam String password, Model model, HttpSession session) {
//
//        String userRole = userService.validateUser(username, password);
//
//
//        if ("ROLE_ADMIN".equals(userRole)) {
//            session.setAttribute("admin", username);
//            return "redirect:/adminhome";
//        } else if ("ROLE_USER".equals(userRole)) {
//            session.setAttribute("user", username);
//            return "redirect:/customerhome";
//        } else {
//            model.addAttribute("error", "invalid username or password");
//            return "login";
//        }
//
//    }


    @GetMapping("/customerhome")
    public String showCustomerHome(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("username", principal.getName());  // Get logged-in username
        }
        return "customerhome";
    }

}




