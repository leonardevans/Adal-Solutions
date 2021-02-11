package com.adalsolutions.controllers;

import com.adalsolutions.models.User;
import com.adalsolutions.repositories.RoleRepository;
import com.adalsolutions.repositories.UserRepository;
import com.adalsolutions.security.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class UserController {
    @Autowired
    AuthUtil authUtil;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @GetMapping({"/admin/users", "/admin/users/**"})
    public String showAdminUsers(Model model){
        model.addAttribute("users", userRepository.findAll());
        return "admin/users";
    }

    @GetMapping("/admin/edit/user/{id}")
    public String showEditUser(@PathVariable("id") int id, Model model){
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()){
            return "redirect:/admin/users?not_found";
        }

        model.addAttribute("user", optionalUser.get());
        model.addAttribute("allRoles", roleRepository.findAll());

        return "admin/edit_user";
    }

    @PostMapping("/admin/update/user")
    public String updateUser(@ModelAttribute("user") User user){
        Optional<User> optionalUser = userRepository.findById(user.getId());
        if (optionalUser.isEmpty()){
            return "redirect:/admin/users?not_found";
        }

        User userToSave = optionalUser.get();
        userToSave.setEnabled(user.isEnabled());
        userToSave.setRoles(user.getRoles());

        userRepository.save(userToSave);
        return "redirect:/admin/users?update_success";
    }

    @GetMapping("/admin/delete/user/{id}")
    public String deleteUser(@PathVariable("id") int id){
        if (!userRepository.existsById(id)){
            return "redirect:/admin/users?not_found";
        }
        User user = authUtil.getLoggedInUser();
        if (user.getId() == id){
            return "redirect:/admin/users?cannot_delete_self";
        }

        userRepository.deleteById(id);
        return "redirect:/admin/users?delete_success";
    }
}
