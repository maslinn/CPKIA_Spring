package ru.cpkia.repository;

import org.springframework.data.repository.CrudRepository;
import ru.cpkia.entity.Client;

import java.util.List;

public interface ClientRepo extends CrudRepository<Client, Integer> {
    Client findClientByEmail(String email);

    Client findById(Long id);

    List<Client> findClientsByEmail(String email);

    List<Client> findClientsBySnils(String snils);

    List<Client> findClientsByName(String name);

    Client findAllById(Long id);

    void removeClientById(Long id);

    List<Client> findAllByDateOfExamNotNull();

    boolean existsByName(String name);

    List<Client> findAllByDateOfExamNotNullAndCompanyRegion(String companyRegion);
}
