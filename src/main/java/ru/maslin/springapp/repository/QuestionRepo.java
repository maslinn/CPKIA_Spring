package ru.maslin.springapp.repository;

import org.springframework.data.repository.CrudRepository;
import ru.maslin.springapp.entity.Question;
import ru.maslin.springapp.entity.Theme;

public interface QuestionRepo extends CrudRepository<Question, Long> {
    Question findAllById(Long id);

    int countAllByTheme(Theme theme);
}
