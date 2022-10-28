package com.hpugs.shiro.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.jws.WebParam;

@Slf4j
@Controller
public class MyController {

    @RequestMapping({"/", "index"})
    public String toIndex(Model model){
        model.addAttribute("msg", "Hello,Shiro");
        return "index";
    }

}
