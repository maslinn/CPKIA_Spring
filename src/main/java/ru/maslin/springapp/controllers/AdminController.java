package ru.maslin.springapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.maslin.springapp.entity.*;
import ru.maslin.springapp.repository.*;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAnyAuthority('ADMIN')")
public class AdminController {

    private final ClientRepo clientRepo;
    private final CompanyRepo companyRepo;
    private final ThemeRepo themeRepo;
    private final QuestionRepo questionRepo;
    private final AnswerRepo answerRepo;

    @Autowired
    public AdminController(ClientRepo clientRepo, CompanyRepo companyRepo, ThemeRepo themeRepo, QuestionRepo questionRepo, AnswerRepo answerRepo) {
        this.clientRepo = clientRepo;
        this.companyRepo = companyRepo;
        this.themeRepo = themeRepo;
        this.questionRepo = questionRepo;
        this.answerRepo = answerRepo;
    }

    @GetMapping
    public String firstAdminPage(Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        Client client = (Client) authentication.getPrincipal();
        model.addAttribute("client", client);
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
    public String deleteAdmin(@PathVariable("id") Long id) {
        clientRepo.removeClientById(id);
        return "redirect:/admin/addNew";
    }

    @GetMapping("/table_new")
    public String getTableWithNewCompanies(Model model) {
        List<Company> companyList = new ArrayList<>(companyRepo.findByStatus(1));//добавляем в лист заявки со статусом неоплачено
        model.addAttribute("companies", companyList);
        return "admin_table_new";
    }

    @GetMapping("/table_payed")
    public String getTableWithPayedCompany(Model model) {
        List<Company> companyList = new ArrayList<>(companyRepo.findByStatus(2));//добавляем в лист заявки со статусом оплачено
        model.addAttribute("companies", companyList);
        return "admin_table_payed";
    }

    @GetMapping("/table_closed")
    public String getTableWithClosedCompanies(Model model) {
        List<Company> companyList = new ArrayList<>(companyRepo.findByStatus(3));//добавляем в лист заявки со статусом закрыто
        model.addAttribute("companies", companyList);
        return "admin_table_closed";
    }

    @GetMapping("/payed/{company_id}")
    public String payedCompanyRequest(@PathVariable Long company_id) {
        Company company = companyRepo.findAllById(company_id);
        company.setStatus(2);//устанавливаем статус 2 - оплачено
        company.getClients().forEach(client -> client.setActive(true));//активируем тесты у пользователей
        companyRepo.save(company);
        return "redirect:/admin/table_new";
    }

    @GetMapping("/payed_back/{company_id}")
    public String payedBackCompanyRequest(@PathVariable Long company_id) {
        Company company = companyRepo.findAllById(company_id);
        company.setStatus(1);//устанавливаем статус 1 - не оплачено
        company.getClients().forEach(client -> client.setActive(false));//деактивируем тесты у пользователей
        companyRepo.save(company);
        return "redirect:/admin/table_payed";
    }

    @GetMapping("/delete_company/{company_id}")
    @Transactional
    public String deleteCompany(@PathVariable Long company_id) {
        companyRepo.deleteById(company_id);
        return "redirect:/admin/table_new";
    }

    @GetMapping("/edit_company/{company_id}")
    public String editCompany(@PathVariable Long company_id, Model model) {
        Company company = companyRepo.findAllById(company_id);
        model.addAttribute("company", company);
        return "admin_company_redactor";
    }

    @GetMapping("/closed_company/{company_id}")
    public String closedCompanyRequest(@PathVariable Long company_id) {
        Company company = companyRepo.findAllById(company_id);
        company.setStatus(1);//устанавливаем статус 3 - закрыто
        company.getClients().forEach(client -> client.setActive(false));//деактивируем тесты у пользователей
        companyRepo.save(company);
        return "redirect:/admin/table_closed";
    }

    //меняем изменяемые поля
    @PostMapping("/edit_company")
    public String editCompanyPost(Company company) {
        Company companyById = companyRepo.findAllById(company.getId());
        companyById.setName(company.getName());
        companyById.setFullname(companyById.getFullname());
        companyById.setEmail(company.getEmail());
        companyById.setDirector(company.getDirector());
        companyById.setOsnovanie(company.getOsnovanie());
        companyById.setPhone(company.getPhone());
        companyById.setAdressUr(company.getAdressUr());
        companyById.setAdressPocht(company.getAdressPocht());
        companyById.setKorSchet(company.getKorSchet());
        companyById.setInn_kpp(company.getInn_kpp());
        companyById.setRaschSchet(company.getRaschSchet());
        companyById.setBik(company.getBik());
        companyById.setBank(company.getBank());
        companyRepo.save(companyById);
        return "redirect:/admin/table_new";
    }

    @GetMapping("/dopusk/{idCompany}")
    public String dopusk(@PathVariable Long idCompany, Model model) {
        Company companyInRepo = companyRepo.findAllById(idCompany);
        List<Client> clients = new LinkedList<>(companyInRepo.getClients());
        model.addAttribute("clients", clients);
        return "dopusk";
    }

    @GetMapping("/edit_client/{client_id}")
    public String editClient(@PathVariable Long client_id, Model model) {
        Client clientById = clientRepo.findAllById(client_id);
        List<Theme> themes = themeRepo.findAll();
        model.addAttribute("themes", themes);
        model.addAttribute("client", clientById);
        return "admin_client_redactor";
    }

    @PostMapping("edit_client")
    public String editClientPost(Client client) {
        Client clientById = clientRepo.findAllById(client.getId());
        clientById.setName(client.getName());
        clientById.setEmail(client.getEmail());
        clientById.setPassword(client.getPassword());
        clientById.setSnils(client.getSnils());
        clientById.setTheme(client.getTheme());
        clientRepo.save(clientById);
        return "redirect:/admin/table_new";
    }

    @GetMapping("/redactor_theme")
    public String redactorOfTheme(Model model) {
        model.addAttribute("theme", new Theme());
        model.addAttribute("themes", themeRepo.findAll());
        return "redactor_of_theme";
    }

    @PostMapping("/save_theme")
    public String saveTheme(Theme theme) {
        themeRepo.save(theme);
        return "redirect:/admin/redactor_theme";
    }

    @GetMapping("/delete_theme/{id}")
    public String deleteTheme(@PathVariable Long id) {
        themeRepo.deleteById(id);
        return "redirect:/admin/redactor_theme";
    }

    @GetMapping("/get_question_redactor/{theme_id}")
    public String getQuestions(@PathVariable Long theme_id, Model model) {
        Theme theme = themeRepo.findAllById(theme_id);
        model.addAttribute("theme", theme);
        Question question = new Question();         //хз как это работает, но если передавать тему вопроса
        question.setTheme(theme);                    //здесь и в срытом лэйбле в шаблоне html,
        model.addAttribute("question", question);//то все передается нормально
        if (!theme.getQuestions().isEmpty()) {
            model.addAttribute("questions", theme.getQuestions());
        }
        return "redactor_of_question";
    }

    @PostMapping("save_question")
    public String saveQuestion(Question question) {
        Question savedQuestion = questionRepo.save(question);
        Long themeId = savedQuestion.getTheme().getId();
        return "redirect:/admin/get_question_redactor/" + themeId;
    }

    @GetMapping("/delete_question/{id}")
    public String deleteQuestion(@PathVariable Long id) {
        Question question = questionRepo.findAllById(id);
        questionRepo.delete(question);
        return "redirect:/admin/get_question_redactor/" + question.getTheme().getId();
    }

    @GetMapping("/get_answer_redactor/{question_id}")
    public String getAnswers(@PathVariable Long question_id, Model model) {
        Question question = questionRepo.findAllById(question_id);
        model.addAttribute("question", question);
        model.addAttribute("answers", question.getAnswers());
        Answer answer = new Answer();
        answer.setQuestion(question);
        model.addAttribute("answer", answer);
        return "redactor_of_answer";
    }

    @PostMapping("/save_answer")
    public String saveAnswer(Answer answer) {
        Answer savedAnswer = answerRepo.save(answer);
        Long questionId = savedAnswer.getQuestion().getId();
        return "redirect:/admin/get_answer_redactor/" + questionId;
    }

    @GetMapping("/delete_answer/{answer_id}")
    public String deleteAnswer(@PathVariable Long answer_id) {
        Answer answer = answerRepo.findAllById(answer_id);
        answerRepo.delete(answer);
        return "redirect:/admin/get_answer_redactor/" + answer.getQuestion().getId();
    }

    @GetMapping("/set_answer_value/{answer_id}")
    public String setValueAnswer(@PathVariable Long answer_id) {
        Answer answer = answerRepo.findAllById(answer_id);
        Long questionId = answer.getQuestion().getId();
        answer.setReversValue();
        answerRepo.save(answer);
        return "redirect:/admin/get_answer_redactor/" + questionId;
    }

    @GetMapping("tests_results")
    public String getTestResults(Model model) {
        List<Client> clientsByDateOfExamExistsAndActiveFalse = clientRepo.findAllByDateOfExamNotNull();
        model.addAttribute("clients", clientsByDateOfExamExistsAndActiveFalse);
        return "admin_tests_result";
    }

    @GetMapping("template_akt")
    public String getTemplateAkt(Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        Client client = (Client) authentication.getPrincipal();
        model.addAttribute("client", client);
        return "template_akt";
    }

    @GetMapping("template_schet")
    public String getTemplateSchet(Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        Client client = (Client) authentication.getPrincipal();
        model.addAttribute("client", client);
        return "template_schet";
    }

    @GetMapping("template_dogovor")
    public String getTemplateDogovor(Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        Client client = (Client) authentication.getPrincipal();
        model.addAttribute("client", client);
        return "template_dogovor";
    }

}
