package ru.maslin.springapp.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.maslin.springapp.entity.Client;
import ru.maslin.springapp.entity.Roles;
import ru.maslin.springapp.repository.ClientRepo;

import java.util.Collections;

@Scope("singleton")
@Component
@Slf4j
public class InsertData {

    private final ClientRepo clientRepo;


    public InsertData(ClientRepo clientRepo) {
        this.clientRepo = clientRepo;
    }

    public void insert() {
        if (clientRepo.existsByName("admin")) {
            log.info("В бд есть пользователь с ролью админ");
        } else {
            Client admin = new Client();
            admin.setId(1L);
            admin.setEmail("admin");
            admin.setName("admin");
            admin.setPassword("adminCpkia");
            admin.setRoles(Collections.singleton(Roles.ADMIN));
            Client savedAdmin = clientRepo.save(admin);
            log.info("Админ создан и добавлен {}", savedAdmin.getEmail());
        }
        if (clientRepo.existsByName("manager")) {
            log.info("В бд есть пользователь с ролью менеджер");
        } else {
            Client manager = new Client();
            manager.setId(2L);
            manager.setEmail("saratov");
            manager.setName("manager");
            manager.setDateOfBirth("саратов");
            manager.setPassword("saratov");
            manager.setRoles(Collections.singleton(Roles.MANAGER));
            Client savedAdmin = clientRepo.save(manager);
            log.info("Менеджер создан и добавлен {}", manager.getEmail());
        }
    }
}
