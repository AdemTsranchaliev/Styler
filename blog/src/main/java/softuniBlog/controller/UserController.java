package softuniBlog.controller;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import softuniBlog.bindingModel.EditBindingModel;
import softuniBlog.bindingModel.SecurityEditBindingModel;
import softuniBlog.bindingModel.SocialNetworksBindingModel;
import softuniBlog.bindingModel.UserBindingModel;
import softuniBlog.entity.Hashtags;
import softuniBlog.entity.Images;
import softuniBlog.entity.Role;
import softuniBlog.entity.User;
import softuniBlog.repository.ImageRepository;
import softuniBlog.repository.RoleRepository;
import softuniBlog.repository.UserRepository;
import softuniBlog.storage.StorageService;

import javax.jws.soap.SOAPBinding;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Array;
import java.util.*;

@Controller
public class UserController {

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ImageRepository imageRepository;


    @GetMapping("/register")
    public String register(Model model) {
        try {
            model.addAttribute("view", "user/register");
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

    @PostMapping("/register")
    public String registerProcess(UserBindingModel userBindingModel, Model model) {
        try {
            if (!userBindingModel.getPassword().equals(userBindingModel.getConfirmPassword())) {
                model.addAttribute("errorPassword", true);
                model.addAttribute("view", "user/register");
                return "base-layout";
            }
            List<User> users = userRepository.findAll();
            for (User users1 : users) {
                if (users1.getEmail().equals(userBindingModel.getEmail())) {
                    model.addAttribute("errorEmail", true);
                    model.addAttribute("view", "user/register");
                    return "base-layout";
                }
            }

            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

            User user = new User(
                    userBindingModel.getEmail(),
                    userBindingModel.getFullName(),
                    bCryptPasswordEncoder.encode(userBindingModel.getPassword())

            );
            if (this.roleRepository.findAll().size() == 0) {
                Role a = new Role();
                a.setName("ROLE_USER");
                this.roleRepository.saveAndFlush(a);
                Role b = new Role();
                b.setName("ROLE_ADMIN");
                Role c = new Role();
                c.setName("ROLE_MODERATOR");
                this.roleRepository.saveAndFlush(b);
            }
            user.setInstagram("");
            user.setFacebook("");
            user.setTwitter("");
            user.setOwnWeb("");
            user.setMotto("");
            user.setLocation("");
            if (this.userRepository.findAll().size() == 0) {
                Role userRole = this.roleRepository.findByName("ROLE_ADMIN");
                user.addRole(userRole);
            } else {
                Role userRole = this.roleRepository.findByName("ROLE_USER");
                user.addRole(userRole);
            }

            Random rnd = new Random();
            int randomPrfPicture = rnd.nextInt(5);
            user.setProfilePicture("DefaultProfilePictures/avatar-" + Integer.toString(randomPrfPicture) + ".png");

            this.userRepository.saveAndFlush(user);

            return "redirect:/login";
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

    @GetMapping("/login")
    public String login(Model model) {
        try {
            model.addAttribute("view", "user/login");
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

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        return "redirect:/login?logout";

    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public String profilePage(Model model) {
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
            Set<Images> userImages = user.getImages();
            int countImages = userImages.size();
            int countLikedImages = user.getLikeImages().size();
            int userFollowersCount = user.getUserFollowers().size();
            int userFollowingCount = user.getFollowingUser().size();
            model.addAttribute("userFollowersCount", userFollowersCount);
            model.addAttribute("userFollowingCount", userFollowingCount);
            model.addAttribute("numberOfImages", countImages);
            model.addAttribute("countLikedImages", countLikedImages);
            model.addAttribute("images", userImages);
            model.addAttribute("user", user);
            model.addAttribute("view", "user/profile");
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

    @GetMapping("/editProfile")
    @PreAuthorize("isAuthenticated()")
    public String editProfile(Model model) {
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
            model.addAttribute("user", user);
            model.addAttribute("view", "user/edit");

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

    @PostMapping("/editProfile")
    @PreAuthorize("isAuthenticated()")
    public String editProfileAction(Model model, EditBindingModel editBindingModel) {
        try {
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();

            User user = this.userRepository.findByEmail(principal.getUsername());
            user.setFullName(editBindingModel.getFullName());
            user.setLocation(editBindingModel.getLocation());
            user.setMotto(editBindingModel.getMotto());
            this.userRepository.saveAndFlush(user);
            return "redirect:/profile";
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

    @GetMapping("/editProfileSecurity")
    @PreAuthorize("isAuthenticated()")
    public String editProfileSecurity(Model model) {
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
            model.addAttribute("user", user);
            model.addAttribute("view", "user/security");

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

    @PostMapping("/editProfileSecurity")
    @PreAuthorize("isAuthenticated()")
    public String editProfileSecurity(Model model, SecurityEditBindingModel securityEditBindingModel) {
        try {
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();

            User user = this.userRepository.findByEmail(principal.getUsername());

            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

            if (!bCryptPasswordEncoder.matches(securityEditBindingModel.getCurrentPassword(), user.getPassword())) {
                model.addAttribute("user", user);
                model.addAttribute("passwordMatchError", true);
                model.addAttribute("view", "user/security");
                return "base-layout";
            }
            if (!securityEditBindingModel.getNewPassword().equals(securityEditBindingModel.getConfimNewPassword())) {
                model.addAttribute("user", user);
                model.addAttribute("newPasswordConfimError", true);
                model.addAttribute("view", "user/security");
                return "base-layout";
            }
            user.setPassword(bCryptPasswordEncoder.encode(securityEditBindingModel.getNewPassword()));
            this.userRepository.saveAndFlush(user);
            return "redirect:/profile";
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

    @GetMapping("/editProfileSocialNetworks")
    @PreAuthorize("isAuthenticated()")
    public String editProfileSocialNetworks(Model model) {
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
            model.addAttribute("user", user);
            model.addAttribute("view", "user/social-networks");

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

    @PostMapping("/editProfileSocialNetworks")
    @PreAuthorize("isAuthenticated()")
    public String editProfileSocialNetworks(Model model, SocialNetworksBindingModel socialNetworksBindingModel) {
        try {
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();

            User user = this.userRepository.findByEmail(principal.getUsername());
            user.setOwnWeb(socialNetworksBindingModel.getOwnWeb());
            user.setTwitter(socialNetworksBindingModel.getTwitter());
            user.setFacebook(socialNetworksBindingModel.getFacebook());
            user.setInstagram(socialNetworksBindingModel.getInstagram());
            this.userRepository.saveAndFlush(user);
            return "redirect:/profile";
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


    @GetMapping("/profile/{id}")
    @PreAuthorize("isAuthenticated()")
    public String openUserAccount(Model model, @PathVariable Integer id) {
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
            int countLikedImages = visitedUser.getLikeImages().size();
            Set<Images> usersImages = visitedUser.getImages();
            int countImages = usersImages.size();

            int userFollowersCount = visitedUser.getUserFollowers().size();
            int userFollowingCount = visitedUser.getFollowingUser().size();
            String visitedUserRole = "";
            String currentUserRole = "";
            for (Role a : visitedUser.getRoles()) {
                visitedUserRole = a.getName();

            }
            for (Role a : currentUser.getRoles()) {
                currentUserRole = a.getName();

            }

            boolean isItFollowed = currentUser.getFollowingUser().contains(visitedUser);
            model.addAttribute("currentUserRole", currentUserRole);
            model.addAttribute("visitedUserRole", visitedUserRole);
            model.addAttribute("userFollowersCount", userFollowersCount);
            model.addAttribute("userFollowingCount", userFollowingCount);
            model.addAttribute("isItFollowed", isItFollowed);
            model.addAttribute("visitedUser", visitedUser);
            model.addAttribute("countLikedImages", countLikedImages);
            model.addAttribute("numberOfImages", countImages);
            model.addAttribute("images", usersImages);
            model.addAttribute("user", currentUser);
            model.addAttribute("view", "user/profile-visit");
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

