package softuniBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import softuniBlog.entity.Hashtags;
import softuniBlog.entity.Images;
import softuniBlog.entity.Role;
import softuniBlog.entity.User;
import softuniBlog.repository.HashTagRepository;
import softuniBlog.repository.ImageRepository;
import softuniBlog.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class Try {

    @Autowired
    ImageRepository imageRepository;
    @Autowired
    HashTagRepository hashTagRepository;
    @Autowired
    UserRepository userRepository;

    @GetMapping("/try")
    public String termsAndConditions(Model model) {
        try{
            UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User userEntity = this.userRepository.findByEmail(user.getUsername());
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                    .getRequest();

            String ip = request.getRemoteAddr();
            Images a = this.imageRepository.findOne(1);
            a.addLike(userEntity);
            this.imageRepository.saveAndFlush(a);

            model.addAttribute("view", "terms-and-conditions/terms-and-conditions");
            model.addAttribute("user", userEntity);
            return "base-layout";
        }
        catch (Exception a)
        {
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
}
