package ru.maslin.springapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.SessionScope;
import ru.maslin.springapp.entity.*;
import ru.maslin.springapp.entity.local.LocalQuestion;
import ru.maslin.springapp.repository.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/client")
@SessionScope
public class ClientController {

    private final ClientRepo clientRepository;
    private final ThemeRepo themeRepo;
    private final CompanyRepo companyRepo;
    private final AnswerRepo answerRepo;
    private final QuestionRepo questionRepo;

    @Autowired
    public ClientController(ClientRepo clientRepository, ThemeRepo themeRepo, CompanyRepo companyRepo, LocalQuestion localQuestions, AnswerRepo answerRepo, QuestionRepo questionRepo) {
        this.clientRepository = clientRepository;
        this.themeRepo = themeRepo;
        this.companyRepo = companyRepo;
        this.answerRepo = answerRepo;
        this.questionRepo = questionRepo;
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

    @GetMapping("/training")
    public String trainingPage(Client inputClient, Model model) {
        //if (inputClient.getId() == null) return "redirect:/client/check";
        Client client = clientRepository.findAllById(inputClient.getId());
        List<Question> questions = client.getTheme().getQuestions();

        LocalQuestion localQuestion = new LocalQuestion();
        for (int i = 0; i < questions.size(); i++) {
            // localQuestion.getClientMapAnswers().put(questions.get(i).getId(), new Answer());
            localQuestion.getClientListAnswers().add(null);
        }
        model.addAttribute("clientAnswers", localQuestion);
        model.addAttribute("questions", questions);
        return "training";
    }

    @PostMapping("/training")
    public String checkAnswers(LocalQuestion localQuestion, Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Client client = (Client) authentication.getPrincipal();
        int countOfRightAnswer = 0;
        List<String> materials = new ArrayList<>();
        String europeanDatePattern = "dd.MM.yyyy";

        for (Long answerId : localQuestion.getClientListAnswers()) {
            Answer allById = answerRepo.findAllById(answerId);
            if (allById.isAnswer()) {
                countOfRightAnswer += 1;//считаем количество правильных ответов
            } else {
                materials.add(allById.getQuestion().getMaterial());
            }
        }

        int countOfQuestions = questionRepo.countAllByTheme(client.getTheme());
        float percentSuccess =
                (float) countOfRightAnswer / countOfQuestions;//процент правильных вопросов к общему их числу


        if (percentSuccess > 0.7) {

            client.setAnswersId(localQuestion.getClientListAnswers());
            client.setActive(false);
            client.setDateOfExam(LocalDateTime.now().format(DateTimeFormatter.ofPattern(europeanDatePattern)));
            client.setPercentResult(percentSuccess);
            clientRepository.save(client);

            List<Question> questions = themeRepo.findAllById(client.getTheme().getId()).getQuestions();

        }

        model.addAttribute("materials", materials);
        model.addAttribute("rightAnswerPercent", percentSuccess * 100);

        return "training_results_page";
    }

    @GetMapping("results/{id}")
    public String printResults(@PathVariable Long id, Model model) {
        Client client = clientRepository.findAllById(id);
        List<Question> questions = themeRepo.findAllById(client.getTheme().getId()).getQuestions();
        List<Long> clientAnswersId = client.getAnswersId();
        List<Answer> clientAnswers = new ArrayList<>();

        for (int i = 0; i < questions.size(); i++) {
            if (clientAnswersId.size() > i) {
                Long answerId = clientAnswersId.get(i);
                clientAnswers.add(questions.get(i).getAnswerById(answerId));
            } else {
                clientAnswers.add(new Answer());//заглушка для просмотра результатов после добавления вопросов
            }
        }
        model.addAttribute("client", client);
        model.addAttribute("questions", questions);
        model.addAttribute("clientAnswers", clientAnswers);
        model.addAttribute("percentResult", client.getPercentResult() * 100);

        return "client_answers_result_to_pdf";
    }

}
