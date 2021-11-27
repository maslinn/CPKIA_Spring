package ru.maslin.springapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;

@Controller
@RequestMapping("/admin")
//@PreAuthorize("hasAnyAuthority('ADMIN')")
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

}
