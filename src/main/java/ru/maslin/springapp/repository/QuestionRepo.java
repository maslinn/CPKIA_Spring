package ru.maslin.springapp.repository;

import org.springframework.data.repository.CrudRepository;
import ru.maslin.springapp.entity.Question;

public interface QuestionRepo extends CrudRepository<Question, Long> {
    Question findAllById(Long id);
}
