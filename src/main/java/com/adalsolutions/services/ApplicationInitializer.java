package com.adalsolutions.services;

import com.adalsolutions.models.Role;
import com.adalsolutions.models.User;
import com.adalsolutions.repositories.UserRepository;
import com.adalsolutions.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ApplicationInitializer  implements CommandLineRunner {
    @Autowired
    private RoleService roleService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        Optional<Role> optionalRole = roleService.getRoleByName("ROLE_USER");
        if(optionalRole.isEmpty()){
            roleService.saveRole(new Role("ROLE_USER"));
        }

        Optional<Role> optionalRole1 = roleService.getRoleByName("ROLE_ADMIN");
        if(optionalRole1.isEmpty()){
            roleService.saveRole(new Role("ROLE_ADMIN"));
        }

        Optional<Role> optionalRole2 = roleService.getRoleByName("ROLE_EDITOR");
        if(optionalRole2.isEmpty()){
            roleService.saveRole(new Role("ROLE_EDITOR"));
        }

        try
        {
            Thread.sleep(3000);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }

        if (!userRepository.existsByUsername("admin")){
            User admin = new User("admin", "admin@mail.com", passwordEncoder.encode("adaladmin"));
            admin.setEnabled(true);
            Optional<Role> userRole = roleService.getRoleByName("ROLE_USER");
            Optional<Role> adminRole = roleService.getRoleByName("ROLE_ADMIN");
            userRole.ifPresent(role -> admin.getRoles().add(role));
            adminRole.ifPresent(role -> admin.getRoles().add(role));
            userRepository.save(admin);
        }

    }
}
