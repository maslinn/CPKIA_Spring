package ru.maslin.springapp.repository;

import org.springframework.data.repository.CrudRepository;
import ru.maslin.springapp.entity.Client;

import java.util.List;

public interface ClientRepo extends CrudRepository<Client, Integer> {
    Client findClientByEmail(String email);

    Client findById(Long id);

    List<Client> findClientsByEmail(String admin);

    Client findAllById(Long id);

    void removeClientById(Long id);
}
