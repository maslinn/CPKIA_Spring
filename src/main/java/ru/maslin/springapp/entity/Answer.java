package ru.maslin.springapp.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String text;

    private boolean isAnswer;

    @ManyToOne(fetch = FetchType.EAGER)
    private Question question;

    public void setReversValue() {
        this.isAnswer = !this.isAnswer;
    }

}
