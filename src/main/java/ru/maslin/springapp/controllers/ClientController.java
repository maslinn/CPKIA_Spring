package ru.maslin.springapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.maslin.springapp.entity.Client;
import ru.maslin.springapp.repository.ClientRepo;

public class ClientController {

    private final ClientRepo clientRepository;

    @Autowired
    public ClientController(ClientRepo clientRepository) {
        this.clientRepository = clientRepository;
    }

    @GetMapping("/physicalApplication")
    public String physicalClientApplication() {
        return "client/phisicalApplication";
    }

    @PostMapping("/saveClient")
    public String saveClient(Client client) {
        return "client/add";
    }

}
