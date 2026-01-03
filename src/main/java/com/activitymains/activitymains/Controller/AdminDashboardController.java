package com.activitymains.activitymains.Controller;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.activitymains.activitymains.DTO.EventDto;
import com.activitymains.activitymains.Model.Event;
import com.activitymains.activitymains.Service.EventService;

@Controller
public class AdminDashboardController {

    @Autowired
    private EventService eventService;

    @GetMapping("/AdminDashboard")
    public String showAdminDashboard(Principal principal, Model model) throws SQLException {
        // Optional: Add current admin name to model
        if (principal != null) {
            model.addAttribute("adminName", principal.getName());
        }

        // Fetch all events
        List<Event> events = eventService.getAllEvents();

        // Convert to DTOs for image handling
        List<EventDto> eventDtos = events.stream().map(event -> {
            EventDto dto = new EventDto();
            dto.setId(event.getId());
            dto.setNameEvent(event.getNameEvent());
            dto.setExpDate(event.getExpDate());
            try {
                Blob imageBlob = event.getImage();
                if (imageBlob != null) {
                    byte[] imageBytes = imageBlob.getBytes(1, (int) imageBlob.length());
                    String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                    dto.setImage(base64Image);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return dto;
        }).collect(Collectors.toList());

        model.addAttribute("events", eventDtos);
        return "AdminDashboard"; // This should match your HTML/JSP file name
    }

@GetMapping("/AdminDashboard/deleteEvent/{id}")
public String deleteEvent(@PathVariable Long id) {
    eventService.deleteEvent(id);
    return "redirect:/AdminDashboard";
}

}

