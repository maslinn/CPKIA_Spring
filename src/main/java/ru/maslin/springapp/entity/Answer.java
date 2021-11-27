package ru.maslin.springapp.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String text;

    private boolean isAnswer;

    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;

    public void setReversValue() {
        this.isAnswer = !this.isAnswer;
    }

}
