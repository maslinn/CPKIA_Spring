package ru.maslin.springapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.SessionScope;
import ru.maslin.springapp.entity.Client;
import ru.maslin.springapp.entity.Company;
import ru.maslin.springapp.entity.Theme;
import ru.maslin.springapp.repository.ClientRepo;
import ru.maslin.springapp.repository.CompanyRepo;
import ru.maslin.springapp.repository.ThemeRepo;

@Controller
@RequestMapping("/client")
@SessionScope
public class ClientController {

    private final ClientRepo clientRepository;
    private final ThemeRepo themeRepo;
    private final CompanyRepo companyRepo;

    @Autowired
    public ClientController(ClientRepo clientRepository, ThemeRepo themeRepo, CompanyRepo companyRepo) {
        this.clientRepository = clientRepository;
        this.themeRepo = themeRepo;
        this.companyRepo = companyRepo;
    }

    @GetMapping()
    public String physicalClientApplication(Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        Client client = (Client) authentication.getPrincipal();
        Company companyById = companyRepo.findAllById(client.getCompany().getId());
        Theme themeById = themeRepo.findAllById(client.getTheme().getId());
        model.addAttribute("client", client);
        model.addAttribute("company", companyById);
        model.addAttribute("theme", themeById);
        return "client_page";
    }

    @PostMapping("/saveClient")
    public String saveClient(Client client) {
        clientRepository.save(client);
        return "successfully_add";
    }

}
