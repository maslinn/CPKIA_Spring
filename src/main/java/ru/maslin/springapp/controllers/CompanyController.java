package ru.maslin.springapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.SessionScope;
import ru.maslin.springapp.entity.Client;
import ru.maslin.springapp.entity.Company;
import ru.maslin.springapp.entity.Roles;
import ru.maslin.springapp.entity.local.LocalCompany;
import ru.maslin.springapp.repository.ClientRepo;
import ru.maslin.springapp.repository.CompanyRepo;
import ru.maslin.springapp.securityAtribute.PasswordGenerator;

import java.util.Collections;

@Controller
@RequestMapping("/company")
@SessionScope
public class CompanyController {

    private final CompanyRepo companyRepo;
    private final ClientRepo clientRepo;
    private LocalCompany localCompany;
    private PasswordGenerator passwordGenerator;

    @Autowired
    public CompanyController(CompanyRepo companyRepo, ClientRepo clientRepo, LocalCompany localCompany, PasswordGenerator passwordGenerator) {
        this.companyRepo = companyRepo;
        this.clientRepo = clientRepo;
        this.localCompany = localCompany;
        this.passwordGenerator = passwordGenerator;
    }

    //Форма заявки компании
    @GetMapping("/add")
    public String createApplication(Model model) {
        model.addAttribute("company", localCompany);
        return "add";
    }

    //Добавление компании
    @PostMapping("/add")
    public String addCompany(LocalCompany localCompany) {
        this.localCompany = localCompany;
        return "redirect:/company/client/add";
    }

    //добавление в заявку слушателя от компании
    //для каждой сессии создается новый лист слушателей
    @PostMapping("/client/add")
    public String saveClient(Client client) {
        localCompany.setClient(client);
        return "redirect:/company/client/add";
    }

    @GetMapping("/client/add")
    public String addClient(Model model) {
        model.addAttribute("client", new Client());
        model.addAttribute("company", localCompany);
        return "addClient";
    }

    @GetMapping("/client/delete/{snils}")
    public String deleteClient(@PathVariable("snils") String snils) {
        localCompany.deleteClientFromSnils(snils);
        return "redirect:/company/client/add";
    }

    //сохранение заявки от компании
    @PostMapping("/save")
    public String saveCompany() {
        Company company = new Company(localCompany);
        Company savedCompany = companyRepo.save(company);


        for (Client client : localCompany.getClients()) {
            System.out.println(client.getName());

            Client clientFromDb = clientRepo.findClientByEmail(client.getEmail());

            if (clientFromDb != null) {
                System.out.println("client is added");
                return "redirect:/company/client/add";
            }

            String password = passwordGenerator.generatePassword(10);
            System.out.println(password);

            client.setPassword(password);
            client.setActive(false);
            client.setRoles(Collections.singleton(Roles.CLIENT));

            client.setCompany(savedCompany);
            clientRepo.save(client);
        }

        return "redirect:/company/add";
    }
}
