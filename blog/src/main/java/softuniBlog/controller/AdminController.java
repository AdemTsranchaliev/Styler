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
import softuniBlog.bindingModel.AdminEditProfileBindingModel;
import softuniBlog.bindingModel.MessageBindingModel;
import softuniBlog.entity.Images;
import softuniBlog.entity.Messages;
import softuniBlog.entity.Role;
import softuniBlog.entity.User;
import softuniBlog.repository.ImageRepository;
import softuniBlog.repository.MessageRepository;
import softuniBlog.repository.RoleRepository;
import softuniBlog.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class AdminController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    MessageRepository messageRepository;

    @PostMapping("/makeAdmin")
    @PreAuthorize("isAuthenticated()")
    public String makeAdmin(Model model, @RequestParam("requestForChangeRole") int userId,@RequestParam("RequestFor") String requestFor) {
        try {
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            User userDetails = this.userRepository.findByEmail(principal.getUsername());


            User user = this.userRepository.findOne(userId);
            if (user == null) {
                return "redirect:/";
            }
            for (Role role : user.getRoles()) {
                if (requestFor.equals("Направи АДМИН"))
                {
                    user.setRoles(new HashSet<Role>());
                    Role rol = this.roleRepository.findByName("ROLE_ADMIN");
                    user.addRole(rol);
                    this.userRepository.saveAndFlush(user);

                }
                else if(requestFor.equals("Направи потребител"))
                {
                    user.setRoles(new HashSet<Role>());
                    Role rol = this.roleRepository.findByName("ROLE_USER");
                    user.addRole(rol);
                    this.userRepository.saveAndFlush(user);
                }
                else if(requestFor.equals("Направи МОДЕРАТОР"))
                {
                    user.setRoles(new HashSet<Role>());
                    Role rol = this.roleRepository.findByName("ROLE_MODERATOR");
                    user.addRole(rol);
                    this.userRepository.saveAndFlush(user);
                }
            }
            return "redirect:/profile/" + userId;
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

    @GetMapping("/allUsers")
    @PreAuthorize("isAuthenticated()")
    public String listAllUsers(Model model) {
        try {


            UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            User userDetails = this.userRepository.findByEmail(principal.getUsername());

            for (Role role : userDetails.getRoles()) {
                if (role.getName().equals("ROLE_USER")) {
                    return "redirect:/profile";
                } else {
                    model.addAttribute("allUsers", this.userRepository.findAll());
                }

            }

            model.addAttribute("userRole", "ROLE_ADMIN");
            model.addAttribute("user", userDetails);
            model.addAttribute("view", "admin/AllUsers");
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

    @GetMapping("/userSocialMedias/{id}")
    @PreAuthorize("isAuthenticated()")
    public String listAllSocialMedias(Model model, @PathVariable Integer id) {
        try {
            if (!this.userRepository.exists(id)) {
                return "redirect:/allUsers";
            }
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            User userDetails = this.userRepository.findByEmail(principal.getUsername());
            for (Role role : userDetails.getRoles()) {
                if (role.getName().equals("ROLE_USER")) {
                    return "redirect:/profile";
                } else {
                    model.addAttribute("visitUser", this.userRepository.findOne(id));
                }
            }

            model.addAttribute("userRole", "ROLE_ADMIN");
            model.addAttribute("user", userDetails);
            model.addAttribute("view", "admin/userSocialMedias");
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

    @GetMapping("/listAllPhotos/{id}")
    @PreAuthorize("isAuthenticated()")
    public String listAllPhotos(Model model, @PathVariable Integer id) {
        try {

            UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            User userDetails = this.userRepository.findByEmail(principal.getUsername());
            for (Role role : userDetails.getRoles()) {
                if (role.getName().equals("ROLE_USER")) {
                    return "redirect:/profile";
                } else {
                    model.addAttribute("visitUser", this.userRepository.findOne(id));
                }
            }
            if (!this.userRepository.exists(id)) {
                return "redirect:/allUsers";
            }
            Set<Images> images = this.userRepository.findOne(id).getImages();

            model.addAttribute("images", images);
            model.addAttribute("userRole", "ROLE_ADMIN");
            model.addAttribute("user", userDetails);
            model.addAttribute("view", "admin/photos");
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

    @GetMapping("/messages")
    @PreAuthorize("isAuthenticated()")
    public String messages(Model model) {
        try {
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            User userDetails = this.userRepository.findByEmail(principal.getUsername());
            for (Role role : userDetails.getRoles()) {
                if (role.getName().equals("ROLE_USER")) {
                    return "redirect:/profile";
                }
            }

            List<Messages> messages = this.messageRepository.findAll();

            model.addAttribute("messages", messages);
            model.addAttribute("userRole", "ROLE_ADMIN");
            model.addAttribute("user", userDetails);
            model.addAttribute("view", "admin/messages");
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

    @GetMapping("/message/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String messagesDelete(Model model, @PathVariable Integer id) {
        try {
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            User userDetails = this.userRepository.findByEmail(principal.getUsername());
            if (!this.messageRepository.exists(id)) {
                return "redirect:/messages";
            }
            this.messageRepository.delete(id);
            return "redirect:/messages";
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

    @GetMapping("/edit/profile/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editProfile(Model model, @PathVariable Integer id) {
        try {
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            User userDetails = this.userRepository.findByEmail(principal.getUsername());

            if (!this.userRepository.exists(id)) {
                return "redirect:/allUsers";
            }
            User user = this.userRepository.findOne(id);
            for (Role role : userDetails.getRoles()) {
                if (role.getName().equals("ROLE_USER")) {
                    return "redirect:/profile";
                }
            }

            model.addAttribute("currentUser", user);
            model.addAttribute("userRole", "ROLE_ADMIN");
            model.addAttribute("user", userDetails);
            model.addAttribute("view", "admin/edit-profile");
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

    @PostMapping("/edit/profile/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editProfilePost(Model model, @PathVariable Integer id, AdminEditProfileBindingModel adminEditProfileBindingModel) {
        try {
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            User userDetails = this.userRepository.findByEmail(principal.getUsername());
            if (!this.userRepository.exists(id)) {
                return "redirect:/allUsers";
            }
            User user = this.userRepository.findOne(id);
            for (Role role : userDetails.getRoles()) {
                if (role.getName().equals("ROLE_USER")) {
                    return "redirect:/profile";
                }
            }
            user.setFullName(adminEditProfileBindingModel.getFullName());
            user.setEmail(adminEditProfileBindingModel.getEmail());
            user.setMotto(adminEditProfileBindingModel.getMotto());
            user.setLocation(adminEditProfileBindingModel.getLocation());
            user.setInstagram(adminEditProfileBindingModel.getInstagram());
            user.setOwnWeb(adminEditProfileBindingModel.getOwnWeb());
            user.setFacebook(adminEditProfileBindingModel.getFacebook());
            user.setTwitter(adminEditProfileBindingModel.getTwitter());
            this.userRepository.saveAndFlush(user);
            return "redirect:/allUsers";
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
