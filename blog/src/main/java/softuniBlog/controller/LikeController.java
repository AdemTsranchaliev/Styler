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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import softuniBlog.entity.Images;
import softuniBlog.entity.Role;
import softuniBlog.entity.User;
import softuniBlog.repository.ImageRepository;
import softuniBlog.repository.UserRepository;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

@Controller
public class LikeController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ImageRepository imageRepository;

    @PostMapping("/like")
    @PreAuthorize("isAuthenticated()")
    public String termsAndConditions(Model model, @RequestParam("likePhoto") int imageId) {
        try {
            UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User userEntity = this.userRepository.findByEmail(user.getUsername());
            Images image = this.imageRepository.findOne(imageId);
            if (image == null) {
                return "redirect:/";
            }
            image.addLike(userEntity);
            this.imageRepository.saveAndFlush(image);
            return "redirect:/profile/images/" + Integer.toString(imageId);
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

    @PostMapping("/dislike")
    @PreAuthorize("isAuthenticated()")
    public String dislike(Model model, @RequestParam("unlikePhoto") int imageId) {
        try {
            UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User userEntity = this.userRepository.findByEmail(user.getUsername());
            Images image = this.imageRepository.findOne(imageId);
            if (image == null) {
                return "redirect:/";
            }
            image.getUserLikes().remove(userEntity);
            this.imageRepository.saveAndFlush(image);
            return "redirect:/profile/images/" + Integer.toString(imageId);
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

    @GetMapping("/profile/likes")
    @PreAuthorize("isAuthenticated()")
    public String profileLikes(Model model) {
        try {
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            User user = this.userRepository.findByEmail(principal.getUsername());
            for (Role role : user.getRoles()) {
                if (role.getName().equals("ROLE_ADMIN")) {
                    model.addAttribute("userRole", "ROLE_ADMIN");
                } else if(role.getName().equals("ROLE_MODERATOR")) {
                    model.addAttribute("userRole", "ROLE_MODERATOR");
                }
                else {
                    model.addAttribute("userRole", "ROLE_USER");
                }

            }
            Set<Images> userLikes = user.getLikeImages();
            int countLikes = userLikes.size();
            int countImages = user.getImages().size();
            int userFollowersCount = user.getUserFollowers().size();
            int userFollowingCount = user.getFollowingUser().size();
            model.addAttribute("userFollowersCount", userFollowersCount);
            model.addAttribute("userFollowingCount", userFollowingCount);
            model.addAttribute("numberOfImages", countImages);
            model.addAttribute("countLikes", countLikes);
            model.addAttribute("user", user);
            model.addAttribute("userLikes", userLikes);
            model.addAttribute("view", "user/likes");
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

    @GetMapping("/profile/{id}/likes")
    @PreAuthorize("isAuthenticated()")
    public String profileFollowing(Model model, @PathVariable Integer id) {
        try {
            if (!this.userRepository.exists(id)) {
                return "redirect:/profile";
            }
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            User currentUser = this.userRepository.findByEmail(principal.getUsername());
            for (Role role : currentUser.getRoles()) {
                if (role.getName().equals("ROLE_ADMIN")) {
                    model.addAttribute("userRole", "ROLE_ADMIN");
                } else if(role.getName().equals("ROLE_MODERATOR")) {
                    model.addAttribute("userRole", "ROLE_MODERATOR");
                }
                else {
                    model.addAttribute("userRole", "ROLE_USER");
                }

            }
            if (id.equals(currentUser.getId())) {
                return "redirect:/profile";
            }
            User visitedUser = this.userRepository.findOne(id);
            Set<Images> userLikes = visitedUser.getLikeImages();
            String visitedUserRole = "";
            String currentUserRole = "";
            for (Role a : visitedUser.getRoles()) {
                visitedUserRole = a.getName();

            }
            for (Role a : currentUser.getRoles()) {
                currentUserRole = a.getName();

            }

            int countLikedImages = userLikes.size();
            int countImages = visitedUser.getImages().size();
            int userFollowersCount = visitedUser.getUserFollowers().size();
            int userFollowingCount = visitedUser.getFollowingUser().size();

            Set<User> userFollowings = visitedUser.getFollowingUser();
            boolean isItFollowed = currentUser.getFollowingUser().contains(visitedUser);
            model.addAttribute("currentUserRole", currentUserRole);
            model.addAttribute("visitedUserRole", visitedUserRole);
            model.addAttribute("userFollowersCount", userFollowersCount);
            model.addAttribute("userFollowingCount", userFollowingCount);
            model.addAttribute("isItFollowed", isItFollowed);
            model.addAttribute("visitedUser", visitedUser);
            model.addAttribute("countLikedImages", countLikedImages);
            model.addAttribute("numberOfImages", countImages);
            model.addAttribute("userLikes", userLikes);
            model.addAttribute("userFollowings", userFollowings);
            model.addAttribute("following", true);
            model.addAttribute("user", currentUser);
            model.addAttribute("view", "user/visited-user-likes");
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
