package ru.cpkia.controllers;

import lombok.extern.slf4j.Slf4j;
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
import ru.cpkia.entity.Answer;
import ru.cpkia.entity.Client;
import ru.cpkia.entity.Company;
import ru.cpkia.entity.Question;
import ru.cpkia.entity.Roles;
import ru.cpkia.entity.Schet;
import ru.cpkia.entity.Theme;
import ru.cpkia.repository.AnswerRepo;
import ru.cpkia.repository.ClientRepo;
import ru.cpkia.repository.CompanyRepo;
import ru.cpkia.repository.QuestionRepo;
import ru.cpkia.repository.SchetRepo;
import ru.cpkia.repository.ThemeRepo;
import ru.cpkia.securityAtribute.MoneyInWords;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAnyAuthority('ADMIN')")
@Slf4j
public class AdminController {

    private final ClientRepo clientRepo;
    private final CompanyRepo companyRepo;
    private final ThemeRepo themeRepo;
    private final QuestionRepo questionRepo;
    private final AnswerRepo answerRepo;
    private final SchetRepo schetRepo;

    @Autowired
    public AdminController(ClientRepo clientRepo, CompanyRepo companyRepo, ThemeRepo themeRepo, QuestionRepo questionRepo, AnswerRepo answerRepo, SchetRepo schetRepo) {
        this.clientRepo = clientRepo;
        this.companyRepo = companyRepo;
        this.themeRepo = themeRepo;
        this.questionRepo = questionRepo;
        this.answerRepo = answerRepo;
        this.schetRepo = schetRepo;
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
        model.addAttribute("managers", clientRepo.findClientsByName("manager"));
        return "add_admin";
    }

    @PostMapping("/save")
    public String saveNewAdmin(Model model, Client client) {
        if (!client.getSnils().equals(client.getPassword())) {
            model.addAttribute("admin", client);
            model.addAttribute("problem", "Пароли не совпадают");
            return "add_admin";
        }

        client.setName("manager");
        client.setRoles(Collections.singleton(Roles.MANAGER));// ставим роль менеджера

        Client savedClient = clientRepo.save(client);
        log.info("Saved manager: {}", savedClient);
        return "redirect:/admin/addNew";
    }

    @GetMapping("/delete/{id}")
    @Transactional //помечаем метод, тк он обращается к транзакционному
    public String deleteAdmin(@PathVariable("id") Long id) {
        clientRepo.removeClientById(id);
        return "redirect:/admin/addNew";
    }

    @GetMapping("/table_new")
    public String getTableWithNewCompanies(Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Client client = (Client) authentication.getPrincipal();
        model.addAttribute("client", client);

        List<Company> companyList = new ArrayList<>(companyRepo.findByStatus(1));//добавляем в лист заявки со статусом неоплачено
        model.addAttribute("companies", companyList);
        model.addAttribute("client", client);
        return "admin_table_new";
    }

    @GetMapping("/table_payed")
    public String getTableWithPayedCompany(Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Client client = (Client) authentication.getPrincipal();
        model.addAttribute("client", client);

        List<Company> companyList = new ArrayList<>(companyRepo.findByStatus(2));//добавляем в лист заявки со статусом оплачено
        model.addAttribute("companies", companyList);
        return "admin_table_payed";
    }

    @GetMapping("/table_closed")
    public String getTableWithClosedCompanies(Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Client client = (Client) authentication.getPrincipal();
        model.addAttribute("client", client);

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
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Client contextClient = (Client) authentication.getPrincipal();
        model.addAttribute("contextClient", contextClient);

        Company company = companyRepo.findAllById(company_id);
        List<Client> managers = clientRepo.findClientsByName("manager");
        List<String> regions = managers.stream().map(Client::getDateOfBirth).collect(Collectors.toList());

        model.addAttribute("company", company);
        model.addAttribute("regions", regions);
        return "admin_company_redactor";
    }

    @GetMapping("/closed_company/{company_id}")
    public String closedCompanyRequest(@PathVariable Long company_id) {
        Company company = companyRepo.findAllById(company_id);
        company.setStatus(3);//устанавливаем статус 3 - закрыто
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
        companyById.setOpenedAt(company.getOpenedAt());
        companyById.setRegion(company.getRegion());
        companyRepo.save(companyById);
        return "redirect:/admin/table_new";
    }

    @GetMapping("/dopusk/{idCompany}")
    public String dopusk(@PathVariable Long idCompany, Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Client contextClient = (Client) authentication.getPrincipal();
        model.addAttribute("contextClient", contextClient);

        Company companyInRepo = companyRepo.findAllById(idCompany);
        List<Client> clients = new LinkedList<>(companyInRepo.getClients());
        model.addAttribute("clients", clients);
        return "dopusk";
    }

    @GetMapping("/akt/{idCompany}")
    public String akt(@PathVariable Long idCompany, Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Client contextClient = (Client) authentication.getPrincipal();
        model.addAttribute("contextClient", contextClient);

        Company companyInRepo = companyRepo.findAllById(idCompany);

        String schetFactMinus14Days = companyInRepo.getOpenedAtToString();
        if (Boolean.FALSE.equals(companyInRepo.getSchets().isEmpty())
                && Objects.isNull(companyInRepo.getOpenedAt())) {
            schetFactMinus14Days = companyInRepo.getSchets()
                    .stream()
                    .findFirst()
                    .orElse(new Schet())
                    .getDateSchetMinus14DayInEuropeFormat();
        }
        String schetFact = companyInRepo.getSchets()
                .stream()
                .findFirst()
                .orElse(new Schet())
                .getDateSchetInEuropeFormat();
        model.addAttribute("dateSchetFactMinus14Days", schetFactMinus14Days);
        model.addAttribute("dateSchetFact", schetFact);

        double price = companyInRepo.getClients().stream().mapToDouble(client -> client.getTheme().getPrice()).sum();
        model.addAttribute("company", companyInRepo);
        model.addAttribute("sumInWord", MoneyInWords.inwords(price));
        model.addAttribute("price", price);
        model.addAttribute("countOfClients", companyInRepo.getClients().size());
        return "akt";
    }

    @GetMapping("/edit_client/{client_id}")
    public String editClient(@PathVariable Long client_id, Model model) {
        Client clientById = clientRepo.findAllById(client_id);
        List<Theme> themes = themeRepo.findAll();
        model.addAttribute("themes", themes);
        model.addAttribute("client", clientById);
        return "admin_client_redactor";
    }

    @GetMapping("/new_client/{company_id}")
    public String newClient(@PathVariable Long company_id, Model model) {
        Company company = companyRepo.findAllById(company_id);

        Client client = new Client();
        client.setCompany(company);

        List<Theme> themes = themeRepo.findAll();
        model.addAttribute("themes", themes);
        model.addAttribute("client", client);
        return "admin_client_redactor";
    }

    @PostMapping("edit_client")
    public String editClientPost(Client client) {
        clientRepo.save(client);
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
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Client contextClient = (Client) authentication.getPrincipal();
        model.addAttribute("contextClient", contextClient);

        List<Client> clientsByDateOfExamExistsAndActiveFalse = clientRepo.findAllByDateOfExamNotNull();
        clientsByDateOfExamExistsAndActiveFalse.removeIf(client -> client.getDateOfExam().isEmpty());
        model.addAttribute("clients", clientsByDateOfExamExistsAndActiveFalse);
        return "admin_tests_result";
    }


    @GetMapping("uch_list/{idCompany}")
    public String uchList(@PathVariable Long idCompany, Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Client contextClient = (Client) authentication.getPrincipal();
        model.addAttribute("contextClient", contextClient);

        Company company = companyRepo.findAllById(idCompany);
        Set<Schet> schets = company.getSchets();

        model.addAttribute("schets", schets);
        model.addAttribute("company", company);

        return "admin_table_schet_faktur";
    }

    @GetMapping("/open_schet/{idSchet}")
    public String openUchList(@PathVariable Long idSchet, Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Client contextClient = (Client) authentication.getPrincipal();
        model.addAttribute("contextClient", contextClient);

        Schet schetById = schetRepo.findAllById(idSchet);
        schetById.setIsOpened(true);
        schetRepo.save(schetById);

        Company company = companyRepo.findAllById(schetById.getCompany().getId());
        Set<Schet> schets = company.getSchets();

        model.addAttribute("schets", schets);
        model.addAttribute("company", company);

        return "admin_table_schet_faktur";
    }

    @GetMapping("add_schet/{idCompany}")
    public String addSchet(@PathVariable Long idCompany, Model model) {
        Company company = companyRepo.findAllById(idCompany);
        double price = company.getClients().stream().mapToDouble(client -> client.getTheme().getPrice()).sum();

        ArrayList<Client> clientsWithoutSchet = new ArrayList<>(company.getClients());
        clientsWithoutSchet.removeIf(client -> client.getSchet() != null);

        Schet schet = new Schet();
        schet.setIsOpened(Boolean.FALSE);
        schet.setClients(clientsWithoutSchet);
        schet.setCompany(company);

        model.addAttribute("company", company);
        model.addAttribute("schet", schet);
        model.addAttribute("price", price);

        return "schet_faktura";
    }

    @PostMapping("/add_schet")
    public String saveSchet(Schet schet) {
        if (schet.getClients().isEmpty()) {
            return "redirect:/admin/uch_list/" + schet.getCompany().getId();
        }
        schet.getClients().removeIf(client -> client.getId() == null);
        if (schet.getClients().isEmpty()) {
            return "redirect:/admin/uch_list/" + schet.getCompany().getId();
        }

        for (int i = 0; i < schet.getClients().size(); i++) {
            schet.getClients().set(i, clientRepo.findAllById(schet.getClients().get(i).getId()));
        }

        schet.setIsOpened(false);
        Schet savedSchet = schetRepo.save(schet);

        for (Client client : schet.getClients()) {
            client.setSchet(savedSchet);
            clientRepo.save(client);
        }
        return "redirect:/admin/uch_list/" + schet.getCompany().getId();
    }

    @GetMapping("edit_schet/{schetId}")
    public String editSchet(@PathVariable Schet schetId, Model model) {
        model.addAttribute("schet", schetId);
        return "schet_faktura";
    }

    @GetMapping("delete_schet/{schet}")
    public String deleteSchet(@PathVariable Schet schet) {
        schetRepo.delete(schet);
        return "redirect:/admin/get_schet_table";
    }

    @GetMapping("get_schet_table")
    public String getSchetTable(Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Client contextClient = (Client) authentication.getPrincipal();
        model.addAttribute("contextClient", contextClient);

        List<Schet> schets = schetRepo.findAllByIsOpenedTrue();
        model.addAttribute("schets", schets);
        return "admin_table_schet_faktur";
    }

    @GetMapping("get_schet/{schet_id}")
    public String getSchet(@PathVariable Long schet_id, Model model) {
        Schet schet = schetRepo.findAllById(schet_id);

        model.addAttribute("schet", schet);
        return "static_schet_faktura";
    }

    /**
     * новые документы
     */

    @GetMapping("/dogovor/{idCompany}")
    public String dogovor(@PathVariable Long idCompany, Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Client contextClient = (Client) authentication.getPrincipal();
        model.addAttribute("contextClient", contextClient);

        Company companyInRepo = companyRepo.findAllById(idCompany);

        String schetFactMinus14Days = companyInRepo.getOpenedAtToString();
        if (Boolean.FALSE.equals(companyInRepo.getSchets().isEmpty())
                && Objects.isNull(companyInRepo.getOpenedAt())) {
            schetFactMinus14Days = companyInRepo.getSchets()
                    .stream()
                    .findFirst()
                    .orElse(new Schet())
                    .getDateSchetMinus14DayInEuropeFormat();
        }

        String schetFact = companyInRepo.getSchets()
                .stream()
                .findFirst()
                .orElse(new Schet())
                .getDateSchetInEuropeFormat();
        model.addAttribute("dateSchetFactMinus14Days", schetFactMinus14Days);
        model.addAttribute("dateSchetFact", schetFact);

        double price = companyInRepo.getClients().stream().mapToDouble(client -> client.getTheme().getPrice()).sum();
        model.addAttribute("company", companyInRepo);
        model.addAttribute("sumInWord", MoneyInWords.inwords(price));
        model.addAttribute("price", price);
        model.addAttribute("countOfClients", companyInRepo.getClients().size());
        return "dogovor";
    }

    @GetMapping("/prilojenie_1/{idCompany}")
    public String prolojenie1(@PathVariable Long idCompany, Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Client contextClient = (Client) authentication.getPrincipal();
        model.addAttribute("contextClient", contextClient);

        Company companyInRepo = companyRepo.findAllById(idCompany);

        String schetFactMinus14Days = companyInRepo.getOpenedAtToString();
        if (Boolean.FALSE.equals(companyInRepo.getSchets().isEmpty())
                && Objects.isNull(companyInRepo.getOpenedAt())) {
            schetFactMinus14Days = companyInRepo.getSchets()
                    .stream()
                    .findFirst()
                    .orElse(new Schet())
                    .getDateSchetMinus14DayInEuropeFormat();
        }

        String schetFact = companyInRepo.getSchets()
                .stream()
                .findFirst()
                .orElse(new Schet())
                .getDateSchetInEuropeFormat();
        model.addAttribute("dateSchetFactMinus14Days", schetFactMinus14Days);
        model.addAttribute("dateSchetFact", schetFact);

        double price = companyInRepo.getClients().stream().mapToDouble(client -> client.getTheme().getPrice()).sum();
        model.addAttribute("company", companyInRepo);
        model.addAttribute("sumInWord", MoneyInWords.inwords(price));
        model.addAttribute("price", price);
        model.addAttribute("countOfClients", companyInRepo.getClients().size());
        return "prilojenie_1";
    }

    @GetMapping("/prilojenie_2/{idCompany}")
    public String prilojenie2(@PathVariable Long idCompany, Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Client contextClient = (Client) authentication.getPrincipal();
        model.addAttribute("contextClient", contextClient);

        Company companyInRepo = companyRepo.findAllById(idCompany);

        String schetFactMinus14Days = companyInRepo.getOpenedAtToString();
        if (Boolean.FALSE.equals(companyInRepo.getSchets().isEmpty())
                && Objects.isNull(companyInRepo.getOpenedAt())) {
            schetFactMinus14Days = companyInRepo.getSchets()
                    .stream()
                    .findFirst()
                    .orElse(new Schet())
                    .getDateSchetMinus14DayInEuropeFormat();
        }

        String schetFact = companyInRepo.getSchets()
                .stream()
                .findFirst()
                .orElse(new Schet())
                .getDateSchetInEuropeFormat();
        model.addAttribute("dateSchetFactMinus14Days", schetFactMinus14Days);
        model.addAttribute("dateSchetFact", schetFact);

        double price = companyInRepo.getClients().stream().mapToDouble(client -> client.getTheme().getPrice()).sum();
        model.addAttribute("company", companyInRepo);
        model.addAttribute("sumInWord", MoneyInWords.inwords(price));
        model.addAttribute("price", price);
        model.addAttribute("countOfClients", companyInRepo.getClients().size());
        return "prilojenie_2";
    }

    @GetMapping("/schet/{idCompany}")
    public String schet(@PathVariable Long idCompany, Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Client contextClient = (Client) authentication.getPrincipal();
        model.addAttribute("contextClient", contextClient);

        Company companyInRepo = companyRepo.findAllById(idCompany);

        String schetFactMinus14Days = companyInRepo.getOpenedAtToString();
        if (Boolean.FALSE.equals(companyInRepo.getSchets().isEmpty())
                && Objects.isNull(companyInRepo.getOpenedAt())) {
            schetFactMinus14Days = companyInRepo.getSchets()
                    .stream()
                    .findFirst()
                    .orElse(new Schet())
                    .getDateSchetMinus14DayInEuropeFormat();
        }

        String schetFact = companyInRepo.getSchets()
                .stream()
                .findFirst()
                .orElse(new Schet())
                .getDateSchetInEuropeFormat();
        model.addAttribute("dateSchetFactMinus14Days", schetFactMinus14Days);
        model.addAttribute("dateSchetFact", schetFact);

        double price = companyInRepo.getClients().stream().mapToDouble(client -> client.getTheme().getPrice()).sum();
        model.addAttribute("company", companyInRepo);
        model.addAttribute("sumInWord", MoneyInWords.inwords(price));
        model.addAttribute("price", price);
        model.addAttribute("countOfClients", companyInRepo.getClients().size());
        return "schet";
    }

    @GetMapping("/vedomost/{idCompany}")
    public String vedomost(@PathVariable Long idCompany, Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Client contextClient = (Client) authentication.getPrincipal();
        model.addAttribute("contextClient", contextClient);

        Company companyInRepo = companyRepo.findAllById(idCompany);

        String schetFactMinus14Days = companyInRepo.getOpenedAtToString();
        if (Boolean.FALSE.equals(companyInRepo.getSchets().isEmpty())
                && Objects.isNull(companyInRepo.getOpenedAt())) {
            schetFactMinus14Days = companyInRepo.getSchets()
                    .stream()
                    .findFirst()
                    .orElse(new Schet())
                    .getDateSchetMinus14DayInEuropeFormat();
        }

        String schetFact = companyInRepo.getSchets()
                .stream()
                .findFirst()
                .orElse(new Schet())
                .getDateSchetInEuropeFormat();
        model.addAttribute("dateSchetFactMinus14Days", schetFactMinus14Days);
        model.addAttribute("dateSchetFact", schetFact);

        double price = companyInRepo.getClients().stream().mapToDouble(client -> client.getTheme().getPrice()).sum();
        model.addAttribute("company", companyInRepo);
        model.addAttribute("sumInWord", MoneyInWords.inwords(price));
        model.addAttribute("price", price);
        model.addAttribute("countOfClients", companyInRepo.getClients().size());
        return "vedomost";
    }


    @GetMapping("/prikaz_zach/{idCompany}")
    public String zach(@PathVariable Long idCompany, Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Client contextClient = (Client) authentication.getPrincipal();
        model.addAttribute("contextClient", contextClient);

        Company companyInRepo = companyRepo.findAllById(idCompany);

        String schetFactMinus14Days = companyInRepo.getOpenedAtToString();
        if (Boolean.FALSE.equals(companyInRepo.getSchets().isEmpty())
                && Objects.isNull(companyInRepo.getOpenedAt())) {
            schetFactMinus14Days = companyInRepo.getSchets()
                    .stream()
                    .findFirst()
                    .orElse(new Schet())
                    .getDateSchetMinus14DayInEuropeFormat();
        }

        String schetFact = companyInRepo.getSchets()
                .stream()
                .findFirst()
                .orElse(new Schet())
                .getDateSchetInEuropeFormat();
        model.addAttribute("dateSchetFactMinus14Days", schetFactMinus14Days);
        model.addAttribute("dateSchetFact", schetFact);

        double price = companyInRepo.getClients().stream().mapToDouble(client -> client.getTheme().getPrice()).sum();
        model.addAttribute("company", companyInRepo);
        model.addAttribute("sumInWord", MoneyInWords.inwords(price));
        model.addAttribute("price", price);
        model.addAttribute("countOfClients", companyInRepo.getClients().size());
        return "prikaz_zach";
    }

    @GetMapping("/prikaz_vypusk/{idCompany}")
    public String vypusk(@PathVariable Long idCompany, Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Client contextClient = (Client) authentication.getPrincipal();
        model.addAttribute("contextClient", contextClient);

        Company companyInRepo = companyRepo.findAllById(idCompany);

        String schetFactMinus14Days = companyInRepo.getOpenedAtToString();
        if (Boolean.FALSE.equals(companyInRepo.getSchets().isEmpty())
                && Objects.isNull(companyInRepo.getOpenedAt())) {
            schetFactMinus14Days = companyInRepo.getSchets()
                    .stream()
                    .findFirst()
                    .orElse(new Schet())
                    .getDateSchetMinus14DayInEuropeFormat();
        }

        String schetFact = companyInRepo.getSchets()
                .stream()
                .findFirst()
                .orElse(new Schet())
                .getDateSchetInEuropeFormat();
        model.addAttribute("dateSchetFactMinus14Days", schetFactMinus14Days);
        model.addAttribute("dateSchetFact", schetFact);

        double price = companyInRepo.getClients().stream().mapToDouble(client -> client.getTheme().getPrice()).sum();
        model.addAttribute("company", companyInRepo);
        model.addAttribute("sumInWord", MoneyInWords.inwords(price));
        model.addAttribute("price", price);
        model.addAttribute("countOfClients", companyInRepo.getClients().size());

        Date plusTwoWeek = Date.from(companyInRepo.getCreateAt().plusSeconds(604800 * 2));
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        model.addAttribute("createDatePlusTwoWeek", formatter.format(plusTwoWeek));
        return "prikaz_vypusk";
    }

}
