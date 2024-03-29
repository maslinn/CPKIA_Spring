package ru.cpkia.entity;

import org.springframework.stereotype.Component;
import ru.cpkia.entity.local.LocalCompany;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Set;


@Entity
@Component
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "company_generator")
    @SequenceGenerator(name = "company_generator", sequenceName = "company_seq", allocationSize = 1, initialValue = 3000)
    private Long id;

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
    private Instant createAt;//дата создания
    private Instant openedAt;//дата открытия договора, считается от счет фактуры

    private Integer status;// 1 - не оплачен, 2 - оплачен, 3 - закрыт

    private String region;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Client> clients;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Schet> schets;

    public Set<Client> getClients() {
        return this.clients;
    }

    public void setClients(Set<Client> clients) {
        this.clients = clients;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Company(String name, String fullname, String email, String director, String osnovanie, String phone, String adressUr, String adressPocht, String korSchet, String inn_kpp, String raschSchet, String bik, String bank, Instant createAt, Integer status, Set<Client> clients, Set<Schet> schets) {
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
        this.createAt = createAt;
        this.status = status;
        this.clients = clients;
        this.schets = schets;
    }

    public Instant getCreateAt() {
        return createAt;
    }


    public String getCreateAtToString() {
        if (this.createAt == null) {
            return "";
        } else {
            Date myDate = Date.from(this.createAt);
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
            return formatter.format(myDate);
        }
    }

    public String getOpenedAtToString() {
        if (this.openedAt == null) {
            return "";
        } else {
            Date myDate = Date.from(this.openedAt);
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
            return formatter.format(myDate);
        }
    }

    public void setCreateAt(Instant createAt) {
        this.createAt = createAt;
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
        //this.clients = localCompany.getClients();
    }

    public Company() {
    }

    public long getCountOfSuccessClients() {
        return this.getClients().stream().filter(client -> Objects.nonNull(client.getDateOfExam())).count();
    }

    public boolean isPayed() {
        return this.status == 2;
    }

    public void setClient(Client client) {
        this.clients.add(client);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Set<Schet> getSchets() {
        return schets;
    }

    public void setSchets(Set<Schet> schets) {
        this.schets = schets;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Instant getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(Instant openedAt) {
        this.openedAt = openedAt;
    }

}
