package ru.maslin.springapp.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String text;

    @ManyToOne(fetch = FetchType.EAGER)
    private Theme theme;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers;

    private String material;

    public Answer getAnswerById(Long answerId) {
        for (Answer answer : answers) {
            if (answer.getId().equals(answerId)) {
                return answer;
            }
        }
        return null;
    }
}
