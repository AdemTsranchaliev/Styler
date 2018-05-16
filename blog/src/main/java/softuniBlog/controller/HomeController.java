package softuniBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import softuniBlog.entity.Images;
import softuniBlog.entity.Role;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import softuniBlog.entity.User;
import softuniBlog.repository.ImageRepository;
import softuniBlog.repository.UserRepository;

import java.awt.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ImageRepository imageRepository;

    @GetMapping("/")
    public String index(Model model) {
        try {
            if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated() && !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
                UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal();
                User userDetails = this.userRepository.findByEmail(principal.getUsername());
                for (Role role : userDetails.getRoles()) {
                    if (role.getName().equals("ROLE_ADMIN")) {
                        model.addAttribute("userRole", "ROLE_ADMIN");
                    } else if(role.getName().equals("ROLE_MODERATOR")) {
                        model.addAttribute("userRole", "ROLE_MODERATOR");
                    }
                    else {
                        model.addAttribute("userRole", "ROLE_USER");
                    }

                }
                model.addAttribute("user", userDetails);

            }
          
            List<User> allUsers = this.userRepository.findAll();
            List<User> famousUser = new LinkedList<>();
            while (famousUser.size() != 4) {
                int max = 0;
                User userrr = new User();
                for (User user : allUsers) {
                    if (user.getUserFollowers().size() >= max) {
                        max = user.getUserFollowers().size();
                        userrr = user;
                    }
                }
                famousUser.add(userrr);
                allUsers.remove(userrr);
            }
            int countAllLikes = 0;
            for (Images img : this.imageRepository.findAll()) {
                countAllLikes += img.getUserLikes().size();
            }

            List<Images> allImages = this.imageRepository.findAll();
            List<Images> images = new LinkedList<>();
            while (images.size() != 6) {
                int max = 0;
                Images img = new Images();
                for (Images image : allImages) {
                    if (image.getUserLikes().size() >= max) {
                        max = image.getUserLikes().size();
                        img = image;
                    }
                }

                images.add(img);
                allImages.remove(img);
            }
            for (Images img : this.imageRepository.findAll()) {
                countAllLikes += img.getUserLikes().size();
            }
            int countAllUsers = 0;
            int countAllImages = 0;
            countAllUsers = this.userRepository.findAll().size();
            countAllImages = this.imageRepository.findAll().size();
            model.addAttribute("images", images);
            model.addAttribute("countAllLikes", countAllLikes);
            model.addAttribute("countAllUsers", countAllUsers);
            model.addAttribute("countAllImages", countAllImages);
            model.addAttribute("famousUsers", famousUser);
            model.addAttribute("view", "home/index");
            return "base-layout";
        }catch (Exception a) {
            if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated() && !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
                UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal();
                User userDetails = this.userRepository.findByEmail(principal.getUsername());
                for (Role role : userDetails.getRoles()) {
                    if (role.getName().equals("ROLE_ADMIN")) {
                        model.addAttribute("userRole", "ROLE_ADMIN");
                    } else if(role.getName().equals("ROLE_MODERATOR")) {
                        model.addAttribute("userRole", "ROLE_MODERATOR");
                    }
                    else {
                        model.addAttribute("userRole", "ROLE_USER");
                    }
                }
                model.addAttribute("user", userDetails);
            }
            model.addAttribute("view", "common/error");
            return "base-layout";
        }
    }

}
