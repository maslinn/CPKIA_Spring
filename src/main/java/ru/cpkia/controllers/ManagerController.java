package ru.cpkia.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.cpkia.entity.Client;
import ru.cpkia.entity.Company;
import ru.cpkia.repository.ClientRepo;
import ru.cpkia.repository.CompanyRepo;
import ru.cpkia.securityAtribute.MoneyInWords;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping("/manager")
@PreAuthorize("hasAnyAuthority('MANAGER')")
public class ManagerController {

    private final CompanyRepo companyRepo;
    private final ClientRepo clientRepo;

    @Autowired
    public ManagerController(CompanyRepo companyRepo, ClientRepo clientRepo) {
        this.companyRepo = companyRepo;
        this.clientRepo = clientRepo;
    }

    @GetMapping
    public String firstManagerPage(Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        Client client = (Client) authentication.getPrincipal();
        model.addAttribute("client", client);
        return "admin_page";
    }

    @GetMapping("/table_new")
    public String getTableWithNewCompanies(Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        Client client = (Client) authentication.getPrincipal();
        List<Company> companyList = new ArrayList<>(companyRepo.findByStatusAndRegion(1, client.getDateOfBirth()));//там регион лежит
        model.addAttribute("companies", companyList);
        model.addAttribute("client", client);
        return "admin_table_new";
    }

    @GetMapping("/table_payed")
    public String getTableWithPayedCompany(Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        Client client = (Client) authentication.getPrincipal();
        List<Company> companyList = new ArrayList<>(companyRepo.findByStatusAndRegion(2, client.getDateOfBirth()));//там регион лежит
        model.addAttribute("companies", companyList);
        model.addAttribute("client", client);
        return "admin_table_payed";
    }

    @GetMapping("/table_closed")
    public String getTableWithClosedCompanies(Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        Client client = (Client) authentication.getPrincipal();
        List<Company> companyList = new ArrayList<>(companyRepo.findByStatusAndRegion(3, client.getDateOfBirth()));//там регион лежит
        model.addAttribute("companies", companyList);
        model.addAttribute("client", client);
        return "admin_table_closed";
    }

    @GetMapping("/payed/{company_id}")
    public String payedCompanyRequest(@PathVariable Long company_id) {
        Company company = companyRepo.findAllById(company_id);
        company.setStatus(2);//устанавливаем статус 2 - оплачено
        company.getClients().forEach(client -> client.setActive(true));//активируем тесты у пользователей
        companyRepo.save(company);
        return "redirect:/manager/table_new";
    }

    @GetMapping("/payed_back/{company_id}")
    public String payedBackCompanyRequest(@PathVariable Long company_id) {
        Company company = companyRepo.findAllById(company_id);
        company.setStatus(1);//устанавливаем статус 1 - не оплачено
        company.getClients().forEach(client -> client.setActive(false));//деактивируем тесты у пользователей
        companyRepo.save(company);
        return "redirect:/manager/table_payed";
    }

    @GetMapping("/closed_company/{company_id}")
    public String closedCompanyRequest(@PathVariable Long company_id) {
        Company company = companyRepo.findAllById(company_id);
        company.setStatus(3);//устанавливаем статус 3 - закрыто
        company.getClients().forEach(client -> client.setActive(false));//деактивируем тесты у пользователей
        companyRepo.save(company);
        return "redirect:/manager/table_closed";
    }

    @GetMapping("tests_results")
    public String getTestResults(Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Client contextClient = (Client) authentication.getPrincipal();
        model.addAttribute("contextClient", contextClient);

        List<Client> clientsByDateOfExamExistsAndActiveFalse = clientRepo.findAllByDateOfExamNotNullAndCompanyRegion(contextClient.getDateOfBirth());
        clientsByDateOfExamExistsAndActiveFalse.removeIf(client -> client.getDateOfExam().isEmpty());
        model.addAttribute("clients", clientsByDateOfExamExistsAndActiveFalse);
        return "admin_tests_result";
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
        double price = companyInRepo.getClients().stream().mapToDouble(client -> client.getTheme().getPrice()).sum();
        model.addAttribute("company", companyInRepo);
        model.addAttribute("sumInWord", MoneyInWords.inwords(price));
        model.addAttribute("price", price);
        model.addAttribute("countOfClients", companyInRepo.getClients().size());
        return "akt";
    }

}
