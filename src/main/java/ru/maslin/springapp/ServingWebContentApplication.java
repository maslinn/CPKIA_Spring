package ru.maslin.springapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.maslin.springapp.config.InsertData;

@SpringBootApplication
public class ServingWebContentApplication {

    private static InsertData insertData;

    @Autowired
    public ServingWebContentApplication(InsertData insertData) {
        ServingWebContentApplication.insertData = insertData;
    }

    public static void main(String[] args) {
        SpringApplication.run(ServingWebContentApplication.class, args);
        insertData.insert();
    }

}
