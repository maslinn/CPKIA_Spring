package ru.maslin.springapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.SessionScope;
import ru.maslin.springapp.MailSender;
import ru.maslin.springapp.entity.Client;
import ru.maslin.springapp.entity.Company;
import ru.maslin.springapp.entity.Roles;
import ru.maslin.springapp.entity.local.LocalCompany;
import ru.maslin.springapp.repository.ClientRepo;
import ru.maslin.springapp.repository.CompanyRepo;
import ru.maslin.springapp.repository.ThemeRepo;
import ru.maslin.springapp.securityAtribute.PasswordGenerator;

import java.time.Instant;
import java.util.Collections;

@Controller
@RequestMapping("/company")
@SessionScope
public class CompanyController {

    private final CompanyRepo companyRepo;
    private final ClientRepo clientRepo;
    private LocalCompany localCompany;
    private final PasswordGenerator passwordGenerator;
    private final MailSender mailSender;
    private final ThemeRepo themeRepo;

    @Autowired
    public CompanyController(CompanyRepo companyRepo, ClientRepo clientRepo, LocalCompany localCompany, PasswordGenerator passwordGenerator, MailSender mailSender, ThemeRepo themeRepo) {
        this.companyRepo = companyRepo;
        this.clientRepo = clientRepo;
        this.localCompany = localCompany;
        this.passwordGenerator = passwordGenerator;
        this.mailSender = mailSender;
        this.themeRepo = themeRepo;
    }

    //Форма заявки компании
    @GetMapping("/add")
    public String createApplication(Model model) {
        model.addAttribute("company", localCompany);
        model.addAttribute("problem", null);
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
    public String saveClient(Model model, Client client) {

        //выводим сообщение пользователю, если добавлен повторяющийся email или снилс
        if (localCompany.containsEmailClient(client.getEmail()) ||
                localCompany.containsSnilsClient(client.getSnils())) {
            model.addAttribute("problem", "Невозможно добавить пользователей с одинаковым Email или СНИЛС");
            model.addAttribute("company", localCompany);
            model.addAttribute("themes", themeRepo.findAll());
            return "addClient";
        }
        localCompany.setClient(client);
        return "redirect:/company/client/add";
    }

    @GetMapping("/client/add")
    public String addClient(Model model) {
        model.addAttribute("client", new Client());
        model.addAttribute("company", localCompany);
        model.addAttribute("themes", themeRepo.findAll());
        return "addClient";
    }

    @GetMapping("/client/delete/{snils}")
    public String deleteClient(@PathVariable("snils") String snils) {
        localCompany.deleteClientFromSnils(snils);
        return "redirect:/company/client/add";
    }

    //сохранение заявки от компании
    @PostMapping("/save")
    public String saveCompany(Model model) {

        //валидируем добавляемых сотрудников
        for (Client client : localCompany.getClients()) {

            //ищем наличие клиента с этим email в бд
            Client clientFromDb = clientRepo.findClientByEmail(client.getEmail());

            //Если в бд есть сотрудник с таким email, выводим сообщение
            if (clientFromDb != null) {
                model.addAttribute("problem", "Сотрудник с Email: " + client.getEmail() + " уже обучается!");
                model.addAttribute("company", localCompany);
                model.addAttribute("client", new Client());
                model.addAttribute("themes", themeRepo.findAll());
                return "addClient";
            }
        }

        Company company = new Company(localCompany);
        company.setCreateAt(Instant.now());//включаем время добавления
        company.setStatus(1);//ставим статус на неоплачен
        company.setSchets(null);
        Company savedCompany = companyRepo.save(company);


        for (Client client : localCompany.getClients()) {

            String password = passwordGenerator.generatePassword(10);

            client.setPassword(password);
            client.setActive(false);
            client.setRoles(Collections.singleton(Roles.CLIENT));

            client.setCompany(savedCompany);
            clientRepo.save(client);

            //отправляем сотруднику сообщение с его email и паролем
            String message = String.format(
                    "Здравствуйте, %s! \n" +
                            "Вы были зарегистрированы на обучающем портале ЧУДПО ЦПК \n" +
                            "Используйте следующие логин и пароль для входа в обучающий портал\n \n" +
                            "Логин: %s\n" +
                            "Пароль: %s\n",
                    client.getName(),
                    client.getEmail(),
                    client.getPassword()
            );
            mailSender.send(client.getEmail(), "Регистрация на учебном портале ЦПК", message);
        }
        return "successfully_add";
    }
}
