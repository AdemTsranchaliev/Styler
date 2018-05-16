package softuniBlog.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import softuniBlog.entity.Role;
import softuniBlog.entity.User;
import softuniBlog.repository.ImageRepository;
import softuniBlog.repository.UserRepository;

import java.util.Set;


@Controller
public class FollowController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ImageRepository imageRepository;


    @PostMapping("/follow")
    @PreAuthorize("isAuthenticated()")
    public String follow(Model model, @RequestParam("requestToFollowUser") int followingUserRequest, @RequestParam("image") boolean image) {
        try {
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            User user = this.userRepository.findByEmail(principal.getUsername());
            if (image) {
                User folowUser = this.imageRepository.findOne(followingUserRequest).getUser();
                user.addFollow(folowUser);
                folowUser.addFollower(user);
                userRepository.saveAndFlush(user);
                userRepository.saveAndFlush(folowUser);
                return "redirect:/profile/images/" + Integer.toString(followingUserRequest);
            } else {
                User folowUser = this.userRepository.findOne(followingUserRequest);
                user.addFollow(folowUser);
                folowUser.addFollower(user);
                userRepository.saveAndFlush(user);
                userRepository.saveAndFlush(folowUser);
                return "redirect:/profile/" + Integer.toString(followingUserRequest);
            }

        } catch (Exception a) {
            if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated() && !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
                UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal();
                User userDetails = this.userRepository.findByEmail(principal.getUsername());
                for (Role role : userDetails.getRoles()) {
                    if (role.getName().equals("ROLE_ADMIN")) {
                        model.addAttribute("userRole", "ROLE_ADMIN");
                    } else {
                        model.addAttribute("userRole", "ROLE_USER");
                    }
                }
                model.addAttribute("user", userDetails);
            }
            model.addAttribute("view", "common/error");
            return "base-layout";
        }
    }

    @PostMapping("/unfollow")
    @PreAuthorize("isAuthenticated()")
    public String unfollow(Model model, @RequestParam("requestToFollowUser") int unfollowingUser, @RequestParam("image") boolean image) {
        try {
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            User user = this.userRepository.findByEmail(principal.getUsername());
            boolean isItFollowed = false;
            if (image) {
                isItFollowed = user.getFollowingUser().contains(this.imageRepository.findOne(unfollowingUser).getUser());
            } else {
                isItFollowed = user.getFollowingUser().contains(this.userRepository.findOne(unfollowingUser));
            }
            if (isItFollowed) {
                User visitedUser;
                if (image) {
                    visitedUser = this.imageRepository.findOne(unfollowingUser).getUser();
                } else {
                    visitedUser = this.userRepository.findOne(unfollowingUser);
                }
                Set<User> a = user.getFollowingUser();
                Set<User> b = visitedUser.getUserFollowers();

                a.remove(visitedUser);
                b.remove(user);
                user.setFollowingUser(a);
                visitedUser.setUserFollowers(b);
                this.userRepository.saveAndFlush(user);
                this.userRepository.saveAndFlush(visitedUser);
            }
            if (image) {
                return "redirect:/profile/images/" + Integer.toString(unfollowingUser);
            } else {

                return "redirect:/profile/" + Integer.toString(unfollowingUser);
            }
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

    @GetMapping("/profile/followers")
    @PreAuthorize("isAuthenticated()")
    public String profileFollowers(Model model) {
        try {


            UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            User user = this.userRepository.findByEmail(principal.getUsername());
            for (Role role : user.getRoles()) {
                if (role.getName().equals("ROLE_ADMIN")) {
                    model.addAttribute("userRole", "ROLE_ADMIN");
                } else {
                    model.addAttribute("userRole", "ROLE_USER");
                }

            }
            Set<User> userFollowers = user.getUserFollowers();
            int countImages = user.getImages().size();
            int countLikedImages = user.getLikeImages().size();
            int userFollowersCount = user.getUserFollowers().size();
            int userFollowingCount = user.getFollowingUser().size();
            model.addAttribute("userFollowersCount", userFollowersCount);
            model.addAttribute("userFollowingCount", userFollowingCount);
            model.addAttribute("numberOfImages", countImages);
            model.addAttribute("userFollowers", userFollowers);
            model.addAttribute("countLikedImages", countLikedImages);
            model.addAttribute("user", user);
            model.addAttribute("follower", true);
            model.addAttribute("view", "user/user-followers");
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

    @GetMapping("/profile/following")
    @PreAuthorize("isAuthenticated()")
    public String profileFollowing(Model model) {
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

            Set<User> userFollowing = user.getFollowingUser();
            int countImages = user.getImages().size();
            int countLikedImages = user.getLikeImages().size();
            int userFollowersCount = user.getUserFollowers().size();
            int userFollowingCount = user.getFollowingUser().size();

            model.addAttribute("userFollowersCount", userFollowersCount);
            model.addAttribute("userFollowingCount", userFollowingCount);
            model.addAttribute("numberOfImages", countImages);
            model.addAttribute("userFollowing", userFollowing);
            model.addAttribute("countLikedImages", countLikedImages);
            model.addAttribute("user", user);
            model.addAttribute("following", true);
            model.addAttribute("view", "user/user-followers");
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

    @GetMapping("/profile/{id}/followings")
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
                return "redirect:/profile/following";
            }
            User visitedUser = this.userRepository.findOne(id);

            int countImages = visitedUser.getImages().size();
            int userFollowersCount = visitedUser.getUserFollowers().size();
            int userFollowingCount = visitedUser.getFollowingUser().size();
            int countLikedImages = visitedUser.getLikeImages().size();
            Set<User> userFollowings = visitedUser.getFollowingUser();
            boolean isItFollowed = currentUser.getFollowingUser().contains(visitedUser);
            String visitedUserRole = "";
            String currentUserRole = "";
            for (Role a : visitedUser.getRoles()) {
                visitedUserRole = a.getName();

            }
            for (Role a : currentUser.getRoles()) {
                currentUserRole = a.getName();

            }
            model.addAttribute("currentUserRole", currentUserRole);
            model.addAttribute("visitedUserRole", visitedUserRole);
            model.addAttribute("userFollowersCount", userFollowersCount);
            model.addAttribute("userFollowingCount", userFollowingCount);
            model.addAttribute("isItFollowed", isItFollowed);
            model.addAttribute("countLikedImages", countLikedImages);
            model.addAttribute("visitedUser", visitedUser);
            model.addAttribute("numberOfImages", countImages);
            model.addAttribute("userFollowings", userFollowings);
            model.addAttribute("following", true);
            model.addAttribute("user", currentUser);
            model.addAttribute("view", "user/profile-visit-followers");
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

    @GetMapping("/profile/{id}/followers")
    @PreAuthorize("isAuthenticated()")
    public String profileFollowers(Model model, @PathVariable Integer id) {
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
                return "redirect:/profile/followers";
            }

            User visitedUser = this.userRepository.findOne(id);
            String visitedUserRole = "";
            String currentUserRole = "";
            for (Role a : visitedUser.getRoles()) {
                visitedUserRole = a.getName();

            }
            for (Role a : currentUser.getRoles()) {
                currentUserRole = a.getName();

            }
            model.addAttribute("currentUserRole", currentUserRole);
            model.addAttribute("visitedUserRole", visitedUserRole);
            int countLikedImages = visitedUser.getLikeImages().size();
            int countImages = visitedUser.getImages().size();
            int userFollowersCount = visitedUser.getUserFollowers().size();
            int userFollowingCount = visitedUser.getFollowingUser().size();
            Set<User> userFollowers = visitedUser.getUserFollowers();
            boolean isItFollowed = currentUser.getFollowingUser().contains(visitedUser);
            model.addAttribute("userFollowersCount", userFollowersCount);
            model.addAttribute("userFollowingCount", userFollowingCount);
            model.addAttribute("isItFollowed", isItFollowed);
            model.addAttribute("visitedUser", visitedUser);
            model.addAttribute("countLikedImages", countLikedImages);
            model.addAttribute("numberOfImages", countImages);
            model.addAttribute("follower", true);
            model.addAttribute("userFollowers", userFollowers);
            model.addAttribute("user", currentUser);
            model.addAttribute("view", "user/profile-visit-followers");
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
