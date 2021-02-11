package com.adalsolutions.controllers;

import com.adalsolutions.models.ConfirmationToken;
import com.adalsolutions.models.Role;
import com.adalsolutions.models.User;
import com.adalsolutions.payload.ForgotPasswordRequest;
import com.adalsolutions.payload.ResetPasswordRequest;
import com.adalsolutions.payload.SignUpRequest;
import com.adalsolutions.repositories.ConfirmationTokenRepository;
import com.adalsolutions.repositories.RoleRepository;
import com.adalsolutions.repositories.UserRepository;
import com.adalsolutions.services.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Controller
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistration(Model model) {
        model.addAttribute("signUpRequest", new SignUpRequest());
        return "register";
    }

    @GetMapping("/forgot_password")
    public String showForgotPassword(Model model) {
        model.addAttribute("forgotPasswordRequest", new ForgotPasswordRequest());
        return "forgot_password";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("signUpRequest") SignUpRequest signUpRequest, BindingResult bindingResult) {
        if (!signUpRequest.getConfirmPassword().equals(signUpRequest.getPassword())) {
            bindingResult.addError(new FieldError("signUpRequest", "confirmPassword", "passwords should match."));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            bindingResult.addError(new FieldError("signUpRequest", "email", "Email address already in use."));
        }

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            bindingResult.addError(new FieldError("signUpRequest", "username", "Username already in use."));
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }

        // Creating user's account
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
//        user.setEnabled(true);
        user.setPassword(signUpRequest.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));


        Optional<Role> optionalRole = roleRepository.findRoleByName("ROLE_USER");
        optionalRole.ifPresent(role -> user.getRoles().add(role));

        userRepository.save(user);

        ConfirmationToken confirmationToken = new ConfirmationToken(user);

        confirmationTokenRepository.save(confirmationToken);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setFrom("bizbedu@gmail.com");
        mailMessage.setText("To confirm your account, please click here : "
                + "http://localhost:8080/confirm-account?token=" + confirmationToken.getConfirmationToken());

        emailSenderService.sendEmail(mailMessage);

        return "redirect:/register?register_success";
    }

    @RequestMapping(value = "/confirm-account", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView confirmUserAccount(ModelAndView modelAndView, @RequestParam("token") String confirmationToken) {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

        if (token != null) {
            Optional<User> optionalUser = userRepository.findUserByEmail(token.getUser().getEmail());
            if (optionalUser.isPresent()) {
                optionalUser.get().setEnabled(true);
                userRepository.save(optionalUser.get());
                modelAndView.setViewName("accountVerified");
            } else {
                modelAndView.addObject("message", "The link is invalid or broken!");
                modelAndView.setViewName("accountVerifyError");
            }

        } else {
            modelAndView.addObject("message", "The link is invalid or broken!");
            modelAndView.setViewName("accountVerifyError");
        }

        return modelAndView;
    }

    @PostMapping("/forgot_password")
    public String forgotPassword(@Valid @ModelAttribute("forgotPasswordRequest") ForgotPasswordRequest forgotPasswordRequest, BindingResult bindingResult) {
        Optional<User> optionalUser = userRepository.findUserByEmail(forgotPasswordRequest.getEmail());
        if (optionalUser.isEmpty()) {
            bindingResult.addError(new FieldError("forgotPasswordRequest", "email", "This email address does not exist!"));
        }
        if (bindingResult.hasErrors()) {
            return "forgot_password";
        }

        User existingUser = optionalUser.get();

        // Create token
        ConfirmationToken confirmationToken = new ConfirmationToken(existingUser);

        // Save it
        confirmationTokenRepository.save(confirmationToken);

        // Create the email
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(existingUser.getEmail());
        mailMessage.setSubject("Complete Password Reset!");
        mailMessage.setFrom("bizbedu@gmail.com");
        mailMessage.setText("To complete the password reset process, please click here: "
                + "http://localhost:8080/confirm-reset?token=" + confirmationToken.getConfirmationToken());

        // Send the email
        emailSenderService.sendEmail(mailMessage);

        return "redirect:/forgot_password?password_recover_success";
    }

    // Endpoint to confirm the token
    @RequestMapping(value = "/confirm-reset", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView validateResetToken(ModelAndView modelAndView, @RequestParam("token") String confirmationToken) {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

        if (token != null) {
            Optional<User> optionalUser = userRepository.findUserByEmail(token.getUser().getEmail());
            User user = optionalUser.get();
            user.setEnabled(true);
            userRepository.save(user);
            modelAndView.addObject("resetPasswordRequest", new ResetPasswordRequest(user.getEmail()));
            modelAndView.addObject("email", user.getEmail());
            modelAndView.setViewName("resetPassword");
        } else {
            modelAndView.setViewName("accountVerifyError");
        }
        return modelAndView;
    }

    // Endpoint to update a user's password
    @RequestMapping(value = "/reset_password", method = RequestMethod.POST)
    public String resetUserPassword(@Valid @ModelAttribute("resetPasswordRequest") ResetPasswordRequest resetPasswordRequest, BindingResult bindingResult, Model model) {
        if (!resetPasswordRequest.getConfirmNewPassword().equals(resetPasswordRequest.getNewPassword())) {
            bindingResult.addError(new FieldError("resetPasswordRequest", "confirmNewPassword", "passwords should match."));
        }

        if (bindingResult.hasErrors()) {
            return "resetPassword";
        }

        // Use email to find user
        Optional<User> optionalUser = userRepository.findUserByEmail(resetPasswordRequest.getEmail());
        User tokenUser = optionalUser.get();
        tokenUser.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        userRepository.save(tokenUser);
        model.addAttribute("message", "Password successfully reset. You can now log in with the new credentials.");
        return "successResetPassword";
    }
}
