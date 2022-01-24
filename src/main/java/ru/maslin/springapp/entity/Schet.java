package ru.maslin.springapp.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Schet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "schet_generator")
    @SequenceGenerator(name = "schet_generator", sequenceName = "schet_seq", allocationSize = 1, initialValue = 12500)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private Company company;

    @OneToMany(mappedBy = "schet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Client> clients;

    private String numberDoc;

    private String dateDoc;

    private String dateSchet;

    public double getPrice() {
        if (clients.isEmpty()) {
            return .0;
        }
        return clients.stream().mapToDouble(client -> client.getTheme().getPrice()).sum();
    }

    public String getDateDocInEuropeFormat() {
        LocalDate datetime = LocalDate.parse(this.dateDoc, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return datetime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public String getDateSchetInEuropeFormat() {
        LocalDate datetime = LocalDate.parse(this.dateSchet, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return datetime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

}
