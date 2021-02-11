package com.adalsolutions.controllers;

import com.adalsolutions.models.Address;
import com.adalsolutions.models.User;
import com.adalsolutions.payload.PasswordRequest;
import com.adalsolutions.repositories.AddressRepository;
import com.adalsolutions.repositories.UserRepository;
import com.adalsolutions.security.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class MyAccountController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthUtil authUtil;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AddressRepository addressRepository;

    @GetMapping({"/account", "/account/**"})
    public String showAccount(Model model){
        User loggedInUser = authUtil.getLoggedInUser();
        model.addAttribute("user", loggedInUser);
        model.addAttribute("passwordRequest", new PasswordRequest());
        model.addAttribute("addressRequest", new Address());
        model.addAttribute("addressToEdit", null);
        return "account";
    }

    @PostMapping("/account/update/password")
    public String updatePassword(@Valid @ModelAttribute("passwordRequest") PasswordRequest passwordRequest, BindingResult bindingResult, Model model){
        User loggedInUser = authUtil.getLoggedInUser();
        if (loggedInUser == null){ return "/login"; }
        model.addAttribute("user", loggedInUser);
        model.addAttribute("passwordRequest", passwordRequest);
        model.addAttribute("addressToEdit", null);

        if (bindingResult.hasErrors()){
            return "account";
        }

        if (!passwordEncoder.matches(passwordRequest.getCurrentPassword(), loggedInUser.getPassword())){
            bindingResult.addError(new FieldError("passwordRequest", "currentPassword", "Current password is wrong"));
            return "account";
        }

        if(!passwordRequest.getConfirmNewPassword().equals(passwordRequest.getNewPassword())){
            bindingResult.addError(new FieldError("passwordRequest", "confirmNewPassword", "New passwords should match."));
            return "account";
        }

        loggedInUser.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));

        return "redirect:/account?password_update_success";
    }

    @PostMapping("/account/add/address")
    public String addUserAddress(@Valid @ModelAttribute("addressRequest") Address addressRequest, BindingResult bindingResult, Model model){
        User loggedInUser = authUtil.getLoggedInUser();
        if (loggedInUser == null){ return "/login"; }
        model.addAttribute("user", loggedInUser);
        model.addAttribute("passwordRequest", new PasswordRequest());
        model.addAttribute("addressToEdit", null);

        if (bindingResult.hasErrors()){
            return "account";
        }

        addressRequest.setUser(loggedInUser);
        addressRepository.save(addressRequest);

        return "redirect:/account?add_address_success";
    }

    @GetMapping("/account/edit/address/{id}")
    public String editAddress(@PathVariable("id") int id, Model model){
        User loggedInUser = authUtil.getLoggedInUser();
        if (loggedInUser == null){ return "/login"; }
        model.addAttribute("user", loggedInUser);
        model.addAttribute("passwordRequest", new PasswordRequest());
        model.addAttribute("addressRequest", new Address());

        Optional<Address> optionalAddress = addressRepository.findById(id);
        if (optionalAddress.isEmpty()){
            return "redirect:/account?address_not_found";
        }

        model.addAttribute("addressToEdit", optionalAddress.get());
        return "account";
    }

    @PostMapping("/account/update/address")
    public String updateAddress(@Valid @ModelAttribute("addressToEdit") Address addressToUpdate, BindingResult bindingResult, Model model){
        User loggedInUser = authUtil.getLoggedInUser();
        if (loggedInUser == null){ return "/login"; }
        model.addAttribute("user", loggedInUser);
        model.addAttribute("passwordRequest", new PasswordRequest());
        model.addAttribute("addressRequest", new Address());

        if (bindingResult.hasErrors()){
            return "account";
        }

        addressToUpdate.setUser(loggedInUser);;

        addressRepository.save(addressToUpdate);
        return "redirect:/account?update_address_success";
    }

    @GetMapping("/account/delete/address/{id}")
    public String deleteAddress(@PathVariable("id") int id){
        User loggedInUser = authUtil.getLoggedInUser();
        if (loggedInUser == null){ return "/login"; }

        Optional<Address> optionalAddress = addressRepository.findById(id);
        if (optionalAddress.isEmpty()){
            return "redirect:/account?address_not_found";
        }

        if (optionalAddress.get().getUser().getId() != loggedInUser.getId()){
            return "redirect:/account?address_not_found";
        }

        addressRepository.deleteById(id);

        return "redirect:/account?delete_address_success";
    }


}
