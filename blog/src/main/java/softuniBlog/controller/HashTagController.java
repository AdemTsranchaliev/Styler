package softuniBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import softuniBlog.entity.Hashtags;
import softuniBlog.entity.Images;
import softuniBlog.entity.Role;
import softuniBlog.entity.User;
import softuniBlog.repository.HashTagRepository;
import softuniBlog.repository.ImageRepository;
import softuniBlog.repository.UserRepository;

import java.util.Set;

@Controller
public class HashTagController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    HashTagRepository hashTagRepository;
    @Autowired
    ImageRepository imageRepository;

    @GetMapping("/hashtags/{hash}")
    @PreAuthorize("isAuthenticated()")
    public String index(Model model, @PathVariable String hash) {
        try {
            UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User userEntity = this.userRepository.findByEmail(user.getUsername());
            Hashtags hashs = this.hashTagRepository.findByHashTag(hash);
            if (hash == null) {
                return "redirect:/";

            }
            Set<Images> images = hashs.getImages();
            for (Role role : userEntity.getRoles()) {
                if (role.getName().equals("ROLE_ADMIN")) {
                    model.addAttribute("userRole", "ROLE_ADMIN");
                } else if(role.getName().equals("ROLE_MODERATOR")) {
                    model.addAttribute("userRole", "ROLE_MODERATOR");
                }
                else {
                    model.addAttribute("userRole", "ROLE_USER");
                }

            }
            model.addAttribute("user", userEntity);
            model.addAttribute("images", images);
            model.addAttribute("view", "HashTags/hashTagsList");
            return "base-layout";
        } catch (Exception a) {
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
