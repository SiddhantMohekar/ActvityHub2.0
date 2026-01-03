package com.activitymains.activitymains.Controller;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.sql.rowset.serial.SerialException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.activitymains.activitymains.Model.Image;
import com.activitymains.activitymains.Model.Role;
import com.activitymains.activitymains.Repos.RoleRepo;
import com.activitymains.activitymains.Service.ImageService;

@Controller
public class ImageController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/add")
    public String addImage(@RequestParam("image") MultipartFile file, @RequestParam("username") String username,
    @RequestParam("password") String password,
    @RequestParam("email") String email,
    @RequestParam("phoneNo") String phoneNo) throws IOException, SerialException, SQLException{
        byte[] bytes = file.getBytes();
        Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);

        Image image = new Image();
        image.setImage(blob);
        image.setUsername(username);
        // Hash the password before saving
        image.setPassword(passwordEncoder.encode(password));
        image.setEmail(email);
        image.setPhoneNo(phoneNo);
        
        // Assign USER role to new users - automatically create if missing
        Role userRole = roleRepo.findByName("ROLE_USER");
        if (userRole == null) {
            // Create ROLE_USER if it doesn't exist
            userRole = new Role();
            userRole.setName("ROLE_USER");
            userRole = roleRepo.save(userRole);
        }
        
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        image.setRoles(roles);
        
        imageService.create(image);
        // After registration redirect to login page so unauthenticated user
        // doesn't get a 403 when rendering protected views like /index
        return "redirect:/";
    }

     @GetMapping("/display/{id}")
    public ResponseEntity<byte[]> displayImage(@PathVariable("id") long id) throws IOException, SQLException
    {
        Image image = imageService.viewByid(id);
        byte [] imageBytes = null;
        imageBytes = image.getImage().getBytes(1,(int) image.getImage().length());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
    }
    
    @GetMapping("/add")
    public String addGet() {
        // Redirect GET /add to the Register form (POST-only endpoint exists)
        return "redirect:/Register";
    }
    
}
