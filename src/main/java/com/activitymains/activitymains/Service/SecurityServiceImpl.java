package com.activitymains.activitymains.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class SecurityServiceImpl implements SecurityService {

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    AuthenticationManager authenticationManager; 
    
    @Autowired
    SecurityContextRepository securityContextRepository;
    
    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Override
    public boolean Login(String username, String password, HttpServletRequest request, HttpServletResponse respons) {
        try {
            UserDetails userdetails = userDetailsService.loadUserByUsername(username);
            
            // Verify password using BCrypt
            if (!passwordEncoder.matches(password, userdetails.getPassword())) {
                System.out.println("Password mismatch for user: " + username);
                return false;
            }
            
            // Check if user has any roles
            if (userdetails.getAuthorities().isEmpty()) {
                System.out.println("User has no roles: " + username);
                return false;
            }
            
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userdetails, password, userdetails.getAuthorities());
            authenticationManager.authenticate(token);
            boolean result = token.isAuthenticated();
            if(result){
                SecurityContext context = SecurityContextHolder.getContext();
                context.setAuthentication(token); 
                securityContextRepository.saveContext(context, request, respons);
                System.out.println("User logged in successfully: " + username);
            }
            return result;
        } catch (Exception e) {
            System.out.println("Login failed for user " + username + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
}
