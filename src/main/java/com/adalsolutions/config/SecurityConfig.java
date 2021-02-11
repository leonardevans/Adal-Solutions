package com.adalsolutions.config;

import com.adalsolutions.services.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Bean
    public UserDetailsService userDetailsService(){
        return new UserDetailsServiceImpl();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()
                .and()
                .csrf().disable()
                .httpBasic().disable()
                .exceptionHandling()
                .and()
                .authorizeRequests()
                .antMatchers("/", "/confirm-**","/home", "/view/**","/about", "/reset_password**", "/contact**","/search", "/products/**","/blog/**", "/register**", "/forgot_password", "/error", "/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpeg", "/**/*.jpg", "/**/*.ico", "/**/*.html" , "/**/*.gif", "/**/*.svg").permitAll()
                .antMatchers("/admin/**").hasAnyRole("EDITOR", "ADMIN")
                .antMatchers("/admin/delete/**","/admin/users**", "/admin/edit/user/**", "/admin/update/user/**" ).hasRole( "ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/login").defaultSuccessUrl("/").permitAll()
                .and()
                .logout().invalidateHttpSession(true).clearAuthentication(true).logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/login?logout").permitAll();
    }
}
