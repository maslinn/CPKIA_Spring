package ru.maslin.springapp.repository;

import org.springframework.data.repository.CrudRepository;
import ru.maslin.springapp.entity.Answer;

public interface AnswerRepo extends CrudRepository<Answer, Long> {
    Answer findAllById(Long id);
}
