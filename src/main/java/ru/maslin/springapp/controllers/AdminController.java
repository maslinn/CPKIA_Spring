package ru.maslin.springapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.maslin.springapp.entity.Client;
import ru.maslin.springapp.entity.Company;
import ru.maslin.springapp.entity.Roles;
import ru.maslin.springapp.repository.ClientRepo;
import ru.maslin.springapp.repository.CompanyRepo;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/admin")
//@PreAuthorize("hasAnyAuthority('ADMIN')")
public class AdminController {

    private final ClientRepo clientRepo;
    private final CompanyRepo companyRepo;

    @Autowired
    public AdminController(ClientRepo clientRepo, CompanyRepo companyRepo) {
        this.clientRepo = clientRepo;
        this.companyRepo = companyRepo;
    }

    @GetMapping
    public String firstAdminPage() {
        return "admin_page";
    }

    @GetMapping("/addNew")
    public String addNewAdmin(Model model) {
        model.addAttribute("admin", new Client());
        model.addAttribute("problem", null);
        model.addAttribute("admins", clientRepo.findClientsByEmail("admin"));
        return "add_admin";
    }

    @PostMapping("/save")
    public String saveNewAdmin(Model model, Client client) {
        System.out.println(client.getEmail().equals(client.getPassword()));
        if (!client.getEmail().equals(client.getPassword())) {
            model.addAttribute("admin", client);
            model.addAttribute("problem", "Пароли не совпадают");
            return "add_admin";
        }

        client.setEmail("admin");
        client.setRoles(Collections.singleton(Roles.ADMIN));// ставим роль админа

        clientRepo.save(client);
        return "redirect:/admin";
    }

    @GetMapping("/delete/{id}")
    @Transactional //помечаем метод, тк он обращается к транзакционному
    public String deleteAdmin(@PathVariable("id") Integer id) {
        clientRepo.removeClientById(id);
        return "redirect:/admin/addNew";
    }

    @GetMapping("/table_new")
    public String getTableWithNew(Model model) {
        List<Company> companyList = new ArrayList<>(companyRepo.findAll());
        model.addAttribute("companies", companyList);
        return "admin_table_new";
    }
}
