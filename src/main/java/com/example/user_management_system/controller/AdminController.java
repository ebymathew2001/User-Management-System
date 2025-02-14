package com.example.user_management_system.controller;



import com.example.user_management_system.entity.User;
import com.example.user_management_system.repository.UserRepository;
import com.example.user_management_system.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.user_management_system.security.SecurityConfig;

import java.util.List;

@Controller

public class AdminController {

    @Autowired
    private UserService userService;
    private UserRepository userRepository;
    private Long id;
    private Model model;


    @GetMapping("/adminhome")
    public String Showadminhome()
    {
        return "adminhome";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model, HttpSession session){
        if(session.getAttribute("admin")==null){
            return "redirect:/login";
        }
        List<User> users=userService.getAllUsers();
        model.addAttribute("users",users);
        return "admindashboard";
    }

    @GetMapping("/admin/search")
    public String searchUsers(@RequestParam String keyword,Model model){
        List<User> users =userService.searchUsersByName(keyword);
        model.addAttribute("users",users);
        return "admindashboard";
    }
    
    @GetMapping("/admin/adduser")
    public String addUserView(Model model){
        System.out.println("Inside addUserView method");
        model.addAttribute("user",new User());
        return "adduser";
    }
    @PostMapping("/admin/adduser")
    public String saveUser(@ModelAttribute("user") User user){
        userService.saveUser(user);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/admin/edit/{id}")
    public String ShowEditForm(@PathVariable("id")Long id, Model model){
        this.id = id;
        this.model = model;
        User user =userService.getUserById(id);
        model.addAttribute("user",user);
        return "edituser";
    }

    @PostMapping("/admin/update-user")
    public String updateUser(@ModelAttribute("user") User user){
        userService.updateUser(user);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/admin/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/dashboard";
    }





}
