package com.activitymains.activitymains.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Override
    public boolean Login(String username, String password, HttpServletRequest request, HttpServletResponse respons) {
        UserDetails userdetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userdetails,password, userdetails.getAuthorities());
        authenticationManager.authenticate(token);
        boolean result = token.isAuthenticated();
        if(result){
            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(token); 
            securityContextRepository.saveContext(context, request, respons);
        }
        return result;
    }
    
}
