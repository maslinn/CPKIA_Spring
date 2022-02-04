package ru.cpkia.repository;

import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;
import ru.cpkia.entity.Schet;

import java.util.Set;

public interface SchetRepo extends CrudRepository<Schet, Long> {

    @NonNull
    Set<Schet> findAll();

    Schet findAllById(Long id);
}
