package ru.maslin.springapp.repository;

import org.springframework.data.repository.CrudRepository;
import ru.maslin.springapp.entity.Theme;

public interface ThemeRepo extends CrudRepository<Theme, Long> {

    void deleteById(Long id);

    Theme findAllById(Long id);

}
