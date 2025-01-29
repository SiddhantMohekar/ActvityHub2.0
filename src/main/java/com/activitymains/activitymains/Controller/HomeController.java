package com.activitymains.activitymains.Controller;

import java.io.IOException;
import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.sql.rowset.serial.SerialException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.activitymains.activitymains.DTO.EventDto;
import com.activitymains.activitymains.Model.Event;
import com.activitymains.activitymains.Model.Image;
import com.activitymains.activitymains.Service.EventService;
import com.activitymains.activitymains.Service.ImageService;
import com.activitymains.activitymains.Service.SecurityService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class HomeController {

    @Autowired
    private EventService eventService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private SecurityService securityService;
    
    @GetMapping("/Register")
    public String Register(){
            return "Register";
    }
    @GetMapping("/Events")
   public String getEvents(Model model) throws SQLException {
        List<Event> events = eventService.getAllEvents();

        // Convert BLOB to base64 string
        List<EventDto> eventDtos = events.stream().map(event -> {
            EventDto dto = new EventDto();
            dto.setId(event.getId());
            dto.setNameEvent(event.getNameEvent());
            dto.setExpDate(event.getExpDate());
            try {
                Blob imageBlob = event.getImage();
                if (imageBlob != null) {
                    int blobLength = (int) imageBlob.length();
                    byte[] imageBytes = imageBlob.getBytes(1, blobLength);
                    String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                    dto.setImage(base64Image);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return dto;
        }).collect(Collectors.toList());


        model.addAttribute("events", eventDtos);
        return "events";
    }

    @PostMapping("/addEvent")
    public String addEventToDB(@RequestParam("image") MultipartFile file,
     @RequestParam("nameEvent") String nameEvent,
    @RequestParam("description") String description,
    @RequestParam("expDate") String expDate
    ) throws IOException, SerialException, SQLException {


         byte[] bytes = file.getBytes();
        Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);

        Event event = new Event();
        event.setNameEvent(nameEvent);
        event.setDescription(description);
        event.setExpDate(expDate);
        event.setImage(blob);

        eventService.addEvent(event);
    
        return "Success";
    }

    @GetMapping("/")
    public String HomePage(){
            return "login";
    }

    @GetMapping("/home")
    public String HomeP(){
            return "index";
    }

    @PostMapping("/login")
    public String login(String email,String password,HttpServletRequest request,HttpServletResponse response){
        boolean loginResponse = securityService.Login( email,password, request, response);
        if(loginResponse){
            return "index";
        }
        return "login";
    }

    // @GetMapping("/Profile")
    // public String Profile(Model model,Authentication authentication){
    //     String email = authentication.getName();
    //     Image image = imageService.findByUsername(email);
    //     model.addAttribute(email, image.getEmail());
    //     return "Profile";
    // }

    @GetMapping("/Profile")
    public String helloGfg(Principal principal, Authentication auth, Model model) {
        String userName = principal.getName();
        System.out.println("Current Logged in User is: " + userName);
    
        // Simulate fetching user details from a service
        Image user = imageService.findByUsername(userName);

        System.out.println("Current Logged in User is: " + user.getPhoneNo());
    
        // Add data to the model
        model.addAttribute("userName", user.getUsername());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("phoneNo", user.getPhoneNo());
        
    
        return "Profile";
    }
    
    @GetMapping("/Login")
    public String Login(){
            return "Login";
    }
    @GetMapping("/AdminLogin")
    public String AdminLogin(){
            return "AdminLogin";
    }
    @GetMapping("/AddDetails")
    public String AddDetails(){
            return "AddDetails";
    }

    @GetMapping("/user-name")
    public String getUserName(Principal principal){
            return principal.getName();
    }
}
