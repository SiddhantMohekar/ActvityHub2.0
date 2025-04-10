package com.activitymains.activitymains.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.activitymains.activitymains.Model.Image;
import com.activitymains.activitymains.Repos.ImageRepository;

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Override
    public Image create(Image image) {
        return imageRepository.save(image);
    }

    @Override
    public Image viewByid(long id) {
        return imageRepository.findById(id).get();
    }

    @Override
    public Image findByUsername(String username) {
        return imageRepository.findByEmail(username);
    }

    @Override
    public String getUserDetails() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

          if (principal instanceof UserDetails) {
              return ((UserDetails) principal).getUsername();
           } else {
              return principal.toString();
           }
          }
    
}
