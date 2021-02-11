package com.adalsolutions.controllers;

import com.adalsolutions.models.User;
import com.adalsolutions.payload.ContactRequest;
import com.adalsolutions.security.AuthUtil;
import com.adalsolutions.services.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
public class MainController {
    @Autowired
    AuthUtil authUtil;

    @Autowired
    private EmailSenderService emailSenderService;

    @RequestMapping("/about")
    public String showAbout(){
        return "about-us";
    }

    @GetMapping("/contact")
    public String showContact(Model model){
        model.addAttribute("contactRequest", new ContactRequest());
        return "contact";
    }

    @RequestMapping("/cart")
    public String showCart(){
        return "cart";
    }

    @PostMapping("/contact")
    public String sendContactMessage(@Valid @ModelAttribute("contactRequest") ContactRequest contactRequest, BindingResult bindingResult){
        User loggedInUser = authUtil.getLoggedInUser();
        String name ;
        String email;

        if (loggedInUser == null){
            if (contactRequest.getName().isEmpty()){
                bindingResult.addError(new FieldError("contactRequest","name", "Please enter your name"));
            }

            if (contactRequest.getEmail().isEmpty()){
                bindingResult.addError(new FieldError("contactRequest","email", "Please enter your email"));
            }

            email = contactRequest.getEmail();
            name = contactRequest.getName();
        }else{
            name = loggedInUser.getUsername();
            email = loggedInUser.getEmail();
        }

        if (bindingResult.hasErrors()){
            return "contact";
        }

        // Create the email
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo("bizbedu@gmail.com");
        mailMessage.setSubject("Contact you");
        mailMessage.setFrom(email);
        mailMessage.setText( "You have received a message from: "+ name +"\n\n" + contactRequest.getMessage());

        // Create the email
        SimpleMailMessage mailMessage1 = new SimpleMailMessage();
        mailMessage1.setTo(email);
        mailMessage1.setSubject("Message Received>");
        mailMessage1.setFrom("bizbedu@gmail.com");
        mailMessage1.setText( "Hello "+ name +"\n\n We have received your message. We will get back to you ASAP!");

        // Send the email
        emailSenderService.sendEmail(mailMessage1);

        return "redirect:/contact?contact_success";
    }
}
