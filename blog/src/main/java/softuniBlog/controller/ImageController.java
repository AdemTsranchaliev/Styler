package softuniBlog.controller;

import org.codehaus.groovy.runtime.ReverseListIterator;
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
import org.thymeleaf.expression.Lists;
import softuniBlog.bindingModel.ImageBindingModel;
import softuniBlog.bindingModel.MessageBindingModel;
import softuniBlog.entity.*;
import softuniBlog.repository.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Controller
public class ImageController {

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    HashTagRepository hashTagRepository;
    @Autowired
    MessageRepository messageRepository;

    @GetMapping("/profile/images/{id}")
    @PreAuthorize("isAuthenticated()")
    public String openImage(Model model, @PathVariable Integer id) {
        try {
            if (!this.imageRepository.exists(id)) {
                return "redirect:/profile";
            }
            Images image = this.imageRepository.findOne(id);
            int countImages = image.getUser().getImages().size();
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
            if (user.getFollowingUser().contains(image.getUser())) {
                model.addAttribute("isItFollowed", true);
            } else {
                model.addAttribute("isItFollowed", false);
            }
            if (image.getUserLikes().contains(user)) {
                model.addAttribute("isItLiked", true);
            } else {
                model.addAttribute("isItLiked", false);
            }
            Set<Hashtags> hashtags = image.getHashtags();
            int likes = image.getUserLikes().size();
            int userFollowersCount = image.getUser().getUserFollowers().size();
            int userFollowingCount = image.getUser().getFollowingUser().size();
            model.addAttribute("likeCount", likes);
            model.addAttribute("hashtags", hashtags);
            model.addAttribute("userFollowersCount", userFollowersCount);
            model.addAttribute("userFollowingCount", userFollowingCount);
            model.addAttribute("numberOfImages", countImages);
            model.addAttribute("image", image);
            model.addAttribute("user", user);
            model.addAttribute("view", "Photos/shot");
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

    @GetMapping("/еditPhoto/{id}")
    @PreAuthorize("isAuthenticated()")
    public String edit(Model model, @PathVariable Integer id) {
        try {
            if (!this.imageRepository.exists(id)) {
                return "redirect:/profile";
            }
            Images image = this.imageRepository.findOne(id);
            int countImages = image.getUser().getImages().size();
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            User user = this.userRepository.findByEmail(principal.getUsername());
            String roles = "";
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
            if (image.getUser() != user && roles.equals("ROLE_USER")) {
                return "redirect:/profile/images/" + Integer.toString(id);
            }
            String hashtags = "";
            for (Hashtags hash : image.getHashtags()) {
                hashtags += hash.getHashTag() + " ";
            }
            model.addAttribute("hashtags", hashtags);
            model.addAttribute("image", image);
            model.addAttribute("user", user);
            model.addAttribute("view", "Photos/edit-photo");
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

    @PostMapping("/еditPhoto/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editPost(Model model, @PathVariable Integer id, @RequestParam("description") String description, @RequestParam("hashTag") String hashTag) {
        try {
            if (!this.imageRepository.exists(id)) {
                return "redirect:/profile";
            }
            Images image = this.imageRepository.findOne(id);

            image.setDescription(description);
            for (Hashtags hash : image.getHashtags()) {
                Hashtags has = this.hashTagRepository.findByHashTag(hash.getHashTag());
                has.getImages().remove(image);
                this.hashTagRepository.saveAndFlush(has);
            }
            String[] hashTags = hashTag.trim().split(" ");
            List<Hashtags> tagsRepo = this.hashTagRepository.findAll();
            List<Hashtags> newHashTags = new LinkedList<>();
            for (String a : hashTags) {
                boolean isUnique = true;
                for (Hashtags hash : tagsRepo) {

                    if (hash.getHashTag().equals(a.toLowerCase())) {
                        isUnique = false;
                        break;
                    }
                }
                if (isUnique) {
                    Hashtags newOneHash = new Hashtags();
                    newOneHash.setHashTag(a.toLowerCase());
                    newOneHash.setImages(new HashSet<Images>());
                    this.hashTagRepository.save(newOneHash);
                }
            }
            this.hashTagRepository.flush();

            image.setHashtags(new HashSet<>());
            this.imageRepository.saveAndFlush(image);
            for (String b : hashTags) {
                Hashtags hash = this.hashTagRepository.findByHashTag(b);

                hash.addImage(image);

                this.hashTagRepository.save(hash);
            }

            //mnogo baven metod!!!
            this.hashTagRepository.flush();


            return "redirect:/profile/images/" + id;
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

    @GetMapping("/report/{id}")
    @PreAuthorize("isAuthenticated()")
    public String report(Model model, @PathVariable Integer id) {
        try {
            if (!this.imageRepository.exists(id)) {
                return "redirect:/profile";
            }
            Images image = this.imageRepository.findOne(id);
            ;
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            User user = this.userRepository.findByEmail(principal.getUsername());
            String roles = "";
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


            model.addAttribute("image", image);
            model.addAttribute("user", user);
            model.addAttribute("view", "Photos/report");
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

    @PostMapping("/report/{id}")
    @PreAuthorize("isAuthenticated()")
    public String reportPost(Model model, @PathVariable Integer id, @RequestParam("message") String message) {
        try {
            if (!this.imageRepository.exists(id)) {
                return "redirect:/profile";
            }
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            User user = this.userRepository.findByEmail(principal.getUsername());

            Messages messages = new Messages();
            messages.setEmail(user.getEmail());
            messages.setMessage(message);
            this.messageRepository.saveAndFlush(messages);
            return "redirect:/profile/images/" + id;
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

    @GetMapping("/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String delete(Model model, @PathVariable Integer id) {
        try {
            if (!this.imageRepository.exists(id)) {
                return "redirect:/profile";
            }
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            User user = this.userRepository.findByEmail(principal.getUsername());

            model.addAttribute("user", user);
            model.addAttribute("image", this.imageRepository.findOne(id));
            model.addAttribute("view", "Photos/delete");
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

    @PostMapping("/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String deletePost(Model model, @PathVariable Integer id) {
        try {
            if (!this.imageRepository.exists(id)) {
                return "redirect:/profile";
            }
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            User user = this.userRepository.findByEmail(principal.getUsername());

            Images image = this.imageRepository.findOne(id);
            for (Hashtags hash : image.getHashtags()) {
                Hashtags hashtags = this.hashTagRepository.findOne(hash.getId());
                hashtags.getImages().remove(image);
                this.hashTagRepository.saveAndFlush(hashtags);
            }
            image.setUserLikes(new HashSet<User>());
            this.imageRepository.saveAndFlush(image);
            this.imageRepository.delete(image);

            return "redirect:/profile/images/" + id;
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

    @GetMapping("/find")
    public String find(Model model) {
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
            List<Images> images = this.imageRepository.findAll();
            if (images.size() > 50) {
                List<Images> imgs = new LinkedList<Images>();
                for (int i = 0; i < 50; i++)
                    imgs.add(images.get(i));
                    model.addAttribute("images",  imgs);

            } else {
                model.addAttribute("images", (images));
            }
            model.addAttribute("view", "home/find");
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
