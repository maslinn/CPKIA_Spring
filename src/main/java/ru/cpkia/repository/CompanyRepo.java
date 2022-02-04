package ru.cpkia.repository;

import org.springframework.data.repository.CrudRepository;
import ru.cpkia.entity.Company;

import java.util.List;

public interface CompanyRepo extends CrudRepository<Company, Integer> {
    List<Company> findAll();

    Company findAllById(Long id);

    void deleteById(Long id);

    //@Query("select List<Company> from Company where status = 1")
    List<Company> findByStatus(Integer status);

    List<Company> findByStatusAndRegion(Integer status, String region);
}
