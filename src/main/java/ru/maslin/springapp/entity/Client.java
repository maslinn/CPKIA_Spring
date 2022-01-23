package ru.maslin.springapp.entity;

import org.springframework.context.annotation.Scope;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Scope("prototype")
public class Client implements UserDetails {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String email;
    private String snils;
    private String dateOfBirth;

    private boolean isActive;//если открыт курс
    private String password;

    @ElementCollection(targetClass = Roles.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "client_role", joinColumns = @JoinColumn(name = "client_id"))
    @Enumerated(EnumType.STRING)
    private Set<Roles> roles;

    @ManyToOne(fetch = FetchType.EAGER)
    private Company company;

    @ManyToOne(fetch = FetchType.EAGER)
    private Theme theme;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "client_answers_id", joinColumns = @JoinColumn(name = "client_id"))
    private List<Long> answersId;

    @ManyToOne(fetch = FetchType.EAGER)
    private Schet schet;

    private float percentResult;

    private String dateOfExam;

    public Client(String name, String email, Company company, String snils, Theme theme, String dateOfBirth, List<Long> answersId, float percentResult, String dateOfExam) {
        this.name = name;
        this.email = email;
        this.company = company;
        this.snils = snils;
        this.theme = theme;
        this.dateOfBirth = dateOfBirth;
        this.answersId = answersId;
        this.percentResult = percentResult;
        this.dateOfExam = dateOfExam;
    }

    public Client(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public Client() {
    }

    public Schet getSchet() {
        return schet;
    }

    public void setSchet(Schet schet) {
        this.schet = schet;
    }

    public boolean isOpenedTraining() {
        return !this.isActive && this.getCompany().isPayed();
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getSnils() {
        return snils;
    }

    public void setSnils(String snils) {
        this.snils = snils;
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Set<Roles> getRoles() {
        return roles;
    }

    public void setRoles(Set<Roles> roles) {
        this.roles = roles;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public List<Long> getAnswersId() {
        return answersId;
    }

    public void setAnswersId(List<Long> answersId) {
        this.answersId = answersId;
    }

    public float getPercentResult() {
        return percentResult;
    }

    public void setPercentResult(float percentResult) {
        this.percentResult = percentResult;
    }

    public String getDateOfExam() {
        return dateOfExam;
    }

    public void setDateOfExam(String dateOfExam) {
        this.dateOfExam = dateOfExam;
    }
}
