package com.eshoppingzone.userProfileService.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/oauth2")
public class OAuth2Controller {

    @GetMapping("/github")
    public String githubLogin() {
        return "redirect:/oauth2/authorize/github";
    }
}