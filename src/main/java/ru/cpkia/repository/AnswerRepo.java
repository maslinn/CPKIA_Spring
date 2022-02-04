package ru.cpkia.repository;

import org.springframework.data.repository.CrudRepository;
import ru.cpkia.entity.Answer;

public interface AnswerRepo extends CrudRepository<Answer, Long> {
    Answer findAllById(Long id);
}
