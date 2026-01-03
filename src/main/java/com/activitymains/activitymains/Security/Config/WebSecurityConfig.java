package com.activitymains.activitymains.Security.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import com.activitymains.activitymains.Service.ImageService;

@Configuration
public class WebSecurityConfig {

    @Autowired
    ImageService imageService;

    @Autowired
    UserDetailsService userDetailsService;

    @Bean
    SecurityContextRepository securityContextRepository(){
        return new DelegatingSecurityContextRepository(new RequestAttributeSecurityContextRepository(),new HttpSessionSecurityContextRepository());
    }
    
    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(bCryptPasswordEncoder());
        return new ProviderManager(provider);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
    
        http.authorizeHttpRequests(authorize->authorize
        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
        // allow anonymous POST to /add (registration submit)
        .requestMatchers(HttpMethod.POST, "/add").permitAll()
        .requestMatchers(HttpMethod.GET,"/Events","/index","/Profile","/login","/home","/AddDetails","/success","/AdminDashboard/deleteEvent/{id}").hasAnyRole("USER","ADMIN")
        .requestMatchers(HttpMethod.GET,"/AdminDashboard").hasRole("ADMIN")
        .requestMatchers(HttpMethod.POST, "/addEvent","/addDetails","/success").hasRole("ADMIN")
        .requestMatchers("/","/login","/index","/user-name","/Register","/showReg").permitAll()
        );
        return http.build();
    }
}
