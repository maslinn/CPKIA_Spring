package ru.maslin.springapp.entity;

import org.springframework.stereotype.Component;
import ru.maslin.springapp.entity.local.LocalCompany;

import javax.persistence.*;
import java.util.Set;


@Entity
@Component
public class Company {
    @Id
    @GeneratedValue
    private int id;

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

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Client> clients;

    public Set<Client> getClients() {
        return this.clients;
    }

    public void setClients(Set<Client> clients) {
        this.clients = clients;
    }

    public Company(String name, String fullname, String email, String director, String osnovanie, String phone, String adressUr, String adressPocht, String korSchet, String inn_kpp, String raschSchet, String bik, String bank, Set<Client> clients) {
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

    public Company(LocalCompany localCompany) {
        this.name = localCompany.getName();
        this.fullname = localCompany.getFullname();
        this.email = localCompany.getEmail();
        this.director = localCompany.getDirector();
        this.osnovanie = localCompany.getOsnovanie();
        this.phone = localCompany.getPhone();
        this.adressUr = localCompany.getAdressUr();
        this.adressPocht = localCompany.getAdressPocht();
        this.korSchet = localCompany.getKorSchet();
        this.inn_kpp = localCompany.getInn_kpp();
        this.raschSchet = localCompany.getRaschSchet();
        this.bik = localCompany.getBik();
        this.bank = localCompany.getBank();
        this.clients = localCompany.getClients();
    }

    public Company() {
    }

    public void setClient(Client client) {
        this.clients.add(client);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getOsnovanie() {
        return osnovanie;
    }

    public void setOsnovanie(String osnovanie) {
        this.osnovanie = osnovanie;
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
