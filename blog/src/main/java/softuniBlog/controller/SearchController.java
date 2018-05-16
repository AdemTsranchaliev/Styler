package softuniBlog.controller;

import groovy.transform.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import softuniBlog.bindingModel.ImageBindingModel;
import softuniBlog.entity.Hashtags;
import softuniBlog.entity.Images;
import softuniBlog.entity.Role;
import softuniBlog.entity.User;
import softuniBlog.repository.HashTagRepository;
import softuniBlog.repository.ImageRepository;
import softuniBlog.repository.UserRepository;
import softuniBlog.storage.StorageFileNotFoundException;
import softuniBlog.storage.StorageService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Controller
public class SearchController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    HashTagRepository hashTagRepository;


    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public String search(Model model) {
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
            model.addAttribute("view", "search/search-form");
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

    @PostMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public String searchAction(Model model, @RequestParam("search") String forSearch, @RequestParam("choice") String choise) {
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
            if (choise.equals("user")) {
                List<User> allUsers = this.userRepository.findAll();
                List<User> resultUsers = new ArrayList<User>();
                for (User currentUser : allUsers) {
                    if (currentUser.getFullName().contains(forSearch)) {
                        resultUsers.add(currentUser);
                    }
                }
                int countResult = resultUsers.size();
                model.addAttribute("countResult", countResult);
                model.addAttribute("foundUsers", resultUsers);
                model.addAttribute("view", "search/search-result-users");
                return "base-layout";
            } else {
                List<Hashtags> allHashTags = this.hashTagRepository.findAll();
                Set<Images> resultHashtags = new HashSet<Images>();
                String[] searching = forSearch.split(" ");
                for (Hashtags hashTag : allHashTags) {
                    for (String search : searching) {
                        search = search.trim();
                        if (hashTag.getHashTag().contains(search)) {
                            for (Images image : hashTag.getImages()) {
                                if (!resultHashtags.contains(image))
                                    resultHashtags.add(image);

                            }
                        }
                    }

                }
                int countResult = resultHashtags.size();
                model.addAttribute("countResult", countResult);
                model.addAttribute("resultHashtags", resultHashtags);
                model.addAttribute("view", "search/search-result-hashtags");
                return "base-layout";
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
}
