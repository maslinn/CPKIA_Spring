package ru.cpkia.repository;

import org.springframework.data.repository.CrudRepository;
import ru.cpkia.entity.Question;
import ru.cpkia.entity.Theme;

public interface QuestionRepo extends CrudRepository<Question, Long> {
    Question findAllById(Long id);

    int countAllByTheme(Theme theme);
}
