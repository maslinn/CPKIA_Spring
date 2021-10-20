package ru.maslin.springapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.SessionScope;
import ru.maslin.springapp.entity.Client;
import ru.maslin.springapp.repository.ClientRepo;

@Controller
@RequestMapping("/client")
@SessionScope
public class ClientController {

    private final ClientRepo clientRepository;

    @Autowired
    public ClientController(ClientRepo clientRepository) {
        this.clientRepository = clientRepository;
    }

    @GetMapping("/add")
    public String physicalClientApplication(Model model) {
        model.addAttribute("client", new Client());
        return "phisicalClient";
    }

    @PostMapping("/saveClient")
    public String saveClient(Client client) {
        clientRepository.save(client);
        return "successfully_add";
    }

}
