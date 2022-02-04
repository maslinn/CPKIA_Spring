package ru.cpkia.entity.local;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import ru.cpkia.entity.Client;

import java.util.HashSet;
import java.util.Set;

//описывает локальные данные компании для сохранения в рамках одной сессии
@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class LocalCompany {

    private String name;
    private String fullname;
    private String email;
    private String director;//директор(статус, должность)
    private String osnovanie;//основание действия руководителя
    private String phone;
    private String adressUr;//юридический адрес
    private String adressPocht;//почтовый адрес
    private String korSchet;//кор счет
    private String inn_kpp;
    private String raschSchet;//расчетный счет
    private String bik;//бик
    private String bank;//банк
    private Set<Client> clients = new HashSet<>();


    public LocalCompany() {
    }

    public LocalCompany(String name, String fullname, String email, String director, String osnovanie, String phone,
                        String adressUr, String adressPocht, String korSchet, String inn_kpp, String raschSchet,
                        String bik, String bank, Set<Client> clients) {
        this.name = name;
        this.fullname = fullname;
        this.email = email;
        this.director = director;
        this.osnovanie = osnovanie;
        this.phone = phone;
        this.adressUr = adressUr;
        this.adressPocht = adressPocht;
        this.korSchet = korSchet;
        this.inn_kpp = inn_kpp;
        this.raschSchet = raschSchet;
        this.bik = bik;
        this.bank = bank;
        this.clients = clients;
    }

    public boolean containsEmailClient(String innEmail) {
        Client client = this.getClients().stream()
                .filter(c -> c.getEmail().equals(innEmail)).findAny().orElse(null);
        //если клиент с таким email уже существует, то возвращаем true
        return client != null;
    }

    public boolean containsSnilsClient(String snils) {
        Client client = this.getClients().stream()
                .filter(c -> c.getSnils().equals(snils)).findAny().orElse(null);
        //если клиент с таким СНИЛС уже существует, то возвращаем true
        return client != null;
    }

    public Set<Client> getClients() {
        return clients;
    }

    public void setClients(Set<Client> clients) {
        this.clients = clients;
    }

    public void setClient(Client client) {
        this.clients.add(client);
    }

    public void deleteClientFromSnils(String snils) {
        this.clients.removeIf(client -> client.getSnils().equals(snils));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getOsnovanie() {
        return osnovanie;
    }

    public void setOsnovanie(String osnovanie) {
        this.osnovanie = osnovanie;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAdressUr() {
        return adressUr;
    }

    public void setAdressUr(String adressUr) {
        this.adressUr = adressUr;
    }

    public String getAdressPocht() {
        return adressPocht;
    }

    public void setAdressPocht(String adressPocht) {
        this.adressPocht = adressPocht;
    }

    public String getKorSchet() {
        return korSchet;
    }

    public void setKorSchet(String korSchet) {
        this.korSchet = korSchet;
    }

    public String getInn_kpp() {
        return inn_kpp;
    }

    public void setInn_kpp(String inn_kpp) {
        this.inn_kpp = inn_kpp;
    }

    public String getRaschSchet() {
        return raschSchet;
    }

    public void setRaschSchet(String raschSchet) {
        this.raschSchet = raschSchet;
    }

    public String getBik() {
        return bik;
    }

    public void setBik(String bik) {
        this.bik = bik;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }
}
