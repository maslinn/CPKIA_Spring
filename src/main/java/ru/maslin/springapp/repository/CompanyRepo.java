package ru.maslin.springapp.repository;

import org.springframework.data.repository.CrudRepository;
import ru.maslin.springapp.entity.Company;

import java.util.List;

public interface CompanyRepo extends CrudRepository<Company, Integer> {
    List<Company> findAll();
}
