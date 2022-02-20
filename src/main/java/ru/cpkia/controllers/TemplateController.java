package ru.cpkia.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TemplateController {

    @GetMapping("/")
    public String homePage() {
        return "home";
    }

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

    @GetMapping("/template_dogovor")
    public String getTemplateDogovor() {
        return "template_dogovor";
    }

    @GetMapping("/template_prilojenie_1")
    public String getTemplatePriloj1() {
        return "template_prilojenie_1";
    }

    @GetMapping("/template_prilojenie_2")
    public String getTemplatePriloj2() {
        return "template_prilojenie_2";
    }

    @GetMapping("/template_vedomost")
    public String getTemplateVedomost() {
        return "template_vedomost";
    }
}
