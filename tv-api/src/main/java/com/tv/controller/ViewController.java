package com.tv.controller;

import com.tv.tvpojo.Users;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manager")
public class ViewController extends BaseController {
    @GetMapping("/login.html")
    public String manager(Model model){
        model.addAttribute("user", new Users());
        return "/login";
    }

    @GetMapping("/center.html")
    public String center(){
        return "center";
    }

    @GetMapping("/center/bgmList")
    public String bgmList(){
        return "video/bgmList";
    }

    @GetMapping("/center/addBgm")
    public String addBgm(){
        return "video/addBgm";
    }
}
