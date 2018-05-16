package softuniBlog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class TermAndConditionsController {

    @GetMapping("/termsAndConditions")
    public String termsAndConditions(Model model) {
        model.addAttribute("view", "terms-and-conditions/terms-and-conditions");
        return "base-layout";
    }
}
