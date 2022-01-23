package ru.maslin.springapp.repository;

import org.springframework.data.repository.CrudRepository;
import ru.maslin.springapp.entity.Schet;

public interface SchetRepo extends CrudRepository<Schet, Long> {
}
