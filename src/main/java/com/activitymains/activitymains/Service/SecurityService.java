package com.activitymains.activitymains.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface SecurityService {
    
    boolean Login(String username,String password,HttpServletRequest request,HttpServletResponse respons);
}
