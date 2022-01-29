package ru.maslin.springapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TemplateController {

    /**
     * Шаблоны
     **/
    @GetMapping("/template_akt")
    public String getTemplateAkt() {
        return "template_akt";
    }

    @GetMapping("/template_schet")
    public String getTemplateSchet() {
        return "/template_schet";
    }

    @GetMapping("template_dogovor")
    public String getTemplateDogovor() {
        return "template_dogovor";
    }

}
