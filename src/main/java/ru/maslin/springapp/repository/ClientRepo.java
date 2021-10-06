package ru.maslin.springapp.repository;

import org.springframework.data.repository.CrudRepository;
import ru.maslin.springapp.entity.Client;

public interface ClientRepo extends CrudRepository<Client, Integer> {
}
