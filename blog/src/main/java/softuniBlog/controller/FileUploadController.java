package softuniBlog.controller;

import com.sun.org.apache.xpath.internal.operations.Mod;
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
import org.springframework.validation.BindingResult;
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

import javax.jws.WebParam;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
public class FileUploadController {

    private final StorageService storageService;

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @Autowired
    ImageRepository imageRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    HashTagRepository hashTagRepository;


    @GetMapping("/uploadPhoto")
    @PreAuthorize("isAuthenticated()")
    public String listUploadedFiles(Model model) throws IOException {
        try {
            UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User userEntity = this.userRepository.findByEmail(user.getUsername());
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
            model.addAttribute("view", "Photos/Add-photo");
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


    @PostMapping("/uploadPhoto")
    @PreAuthorize("isAuthenticated()")
    public String handleFileUpload(Model model, @Valid ImageBindingModel imageBindingModel, BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes) {
        try {
            UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User userEntity = this.userRepository.findByEmail(user.getUsername());
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
            if (bindingResult.hasErrors() || imageBindingModel.getFile().isEmpty()) {
                model.addAttribute("InputError", true);
                model.addAttribute("user", userEntity);
                model.addAttribute("view", "Photos/Add-photo");
                return "base-layout";
            }

            String extentionsPattern = ".+\\.jpg|.+\\.png|.+\\.jpeg";
            Pattern r = Pattern.compile(extentionsPattern);
            Matcher m = r.matcher(imageBindingModel.getFile().getOriginalFilename());
            Set<Images> images1 = userEntity.getImages();
            for (Images image : images1) {
                if (image.getTitle().equals(imageBindingModel.getTitle())) {
                    model.addAttribute("ErrorUniqueTitle", true);
                    model.addAttribute("user", userEntity);
                    model.addAttribute("view", "Photos/Add-photo");
                    return "base-layout";
                }
            }
            String hashtagPattern = "((\\#[A-Za-z1-9а-яА-Я]{2,})+)";
            Pattern hs = Pattern.compile(hashtagPattern);
            Matcher matcher = hs.matcher(imageBindingModel.getHashTag());
            if (!matcher.find()) {
                model.addAttribute("hashTagError", true);
                model.addAttribute("user", userEntity);
                model.addAttribute("view", "Photos/Add-photo");
                return "base-layout";
            }
            if (m.find()) {
                Images images = new Images(imageBindingModel.getTitle(), imageBindingModel.getDescription(), userEntity, new Date());
                String[] hashTags = imageBindingModel.getHashTag().trim().split(" ");
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
                this.imageRepository.saveAndFlush(images);

                Images a = new Images();
                a = images;

                for (String b : hashTags) {
                    Hashtags hash = this.hashTagRepository.findByHashTag(b);

                    hash.addImage(a);

                    this.hashTagRepository.save(hash);
                }


                this.hashTagRepository.flush();
                extentionsPattern = ".+\\.png";
                r = Pattern.compile(extentionsPattern);
                m = r.matcher(imageBindingModel.getFile().getOriginalFilename());
                if (m.find()) {
                    images.setPathToImage(Integer.toString(images.getId()) + ".png");
                    this.imageRepository.saveAndFlush(images);
                    storageService.store(imageBindingModel.getFile(), a.getId(), ".png");

                }
                extentionsPattern = ".+\\.jpeg";
                r = Pattern.compile(extentionsPattern);
                m = r.matcher(imageBindingModel.getFile().getOriginalFilename());
                if (m.find()) {
                    images.setPathToImage(Integer.toString(images.getId()) + ".jpeg");
                    this.imageRepository.saveAndFlush(images);
                    storageService.store(imageBindingModel.getFile(), a.getId(), ".jpeg");

                } else {
                    images.setPathToImage(Integer.toString(images.getId()) + ".jpg");
                    this.imageRepository.saveAndFlush(images);
                    storageService.store(imageBindingModel.getFile(), a.getId(), ".jpg");

                }
                return "redirect:/profile";

            } else {
                model.addAttribute("ErrorExtention", true);
                model.addAttribute("user", userEntity);
                model.addAttribute("view", "Photos/Add-photo");
                return "base-layout";
            }
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

    @PostMapping("/uploadProfilePicture")
    @PreAuthorize("isAuthenticated()")
    public String uploadProfilePicture(Model model, ImageBindingModel imageBindingModel,
                                       RedirectAttributes redirectAttributes) {
        try {
            UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User userEntity = this.userRepository.findByEmail(user.getUsername());
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
            if (imageBindingModel.getFile().isEmpty()) {
                model.addAttribute("inputError", true);
                model.addAttribute("user", userEntity);
                model.addAttribute("view", "user/edit");
                return "base-layout";
            }
            String pattern = ".+\\.jpg|.+\\.png|.+\\.jpeg";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(imageBindingModel.getFile().getOriginalFilename());
            if (m.find()) {
                pattern = ".+\\.png";
                r = Pattern.compile(pattern);
                m = r.matcher(imageBindingModel.getFile().getOriginalFilename());
                if (m.find()) {
                    userEntity.setProfilePicture("ProfilePictures/" + Integer.toString(userEntity.getId()) + ".png");
                    this.userRepository.saveAndFlush(userEntity);
                    storageService.storeProfilePicture(imageBindingModel.getFile(), userEntity.getId(), ".png");

                }
                pattern = ".+\\.jpeg";
                r = Pattern.compile(pattern);
                m = r.matcher(imageBindingModel.getFile().getOriginalFilename());
                if (m.find()) {
                    userEntity.setProfilePicture("ProfilePictures/" + Integer.toString(userEntity.getId()) + ".jpeg");
                    this.userRepository.saveAndFlush(userEntity);
                    storageService.storeProfilePicture(imageBindingModel.getFile(), userEntity.getId(), ".jpeg");

                } else {
                    userEntity.setProfilePicture("ProfilePictures/" + Integer.toString(userEntity.getId()) + ".jpg");
                    this.userRepository.saveAndFlush(userEntity);
                    storageService.storeProfilePicture(imageBindingModel.getFile(), userEntity.getId(), ".jpg");


                }
                return "redirect:/profile";
            } else {
                model.addAttribute("extentionError", true);
                model.addAttribute("user", userEntity);
                model.addAttribute("view", "user/edit");
                return "base-layout";
            }
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

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }


}
