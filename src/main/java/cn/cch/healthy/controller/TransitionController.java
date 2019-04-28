package cn.cch.healthy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TransitionController {
    @RequestMapping("toLauncher")
    public String toLauncher ()
    {
        return "data_launcher";
    }
}
