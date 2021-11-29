package ru.maslin.springapp.repository;

import org.springframework.data.repository.CrudRepository;
import ru.maslin.springapp.entity.Company;

import java.util.List;

public interface CompanyRepo extends CrudRepository<Company, Integer> {
    List<Company> findAll();

    Company findAllById(Long id);

    void deleteById(Long id);

    //@Query("select List<Company> from Company where status = 1")
    List<Company> findByStatus(Integer status);
}
