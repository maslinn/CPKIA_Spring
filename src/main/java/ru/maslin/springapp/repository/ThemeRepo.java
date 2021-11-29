package ru.maslin.springapp.repository;

import org.springframework.data.repository.CrudRepository;
import ru.maslin.springapp.entity.Theme;

import java.util.List;

public interface ThemeRepo extends CrudRepository<Theme, Long> {

    void deleteById(Long id);

    List<Theme> findAll();

    Theme findAllById(Long id);

}
