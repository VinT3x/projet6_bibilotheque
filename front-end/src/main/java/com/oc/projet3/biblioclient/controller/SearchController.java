package com.oc.projet3.biblioclient.controller;

import com.oc.projet3.biblioclient.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes("user")
public class SearchController {


    @ModelAttribute("user")
    public User getUser() {
        return new User();

    }

    @GetMapping(value={"/home","/"})
    public String home(@ModelAttribute("user") User user, Model model){
        //connecté ? @MustAuthentified sinon voir si il faut pas installer spring security

        if (user.getEmail() != null){
            model.addAttribute("username", user.getEmail());
            return "home";
        }else{
            return "authentification/login";
        }
        //return "home";

    }
}
