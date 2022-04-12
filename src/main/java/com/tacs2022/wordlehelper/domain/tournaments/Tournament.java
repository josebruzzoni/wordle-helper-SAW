package com.tacs2022.wordlehelper.domain.tournaments;

import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tournament {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Language language;
    private Visibility visibility;
    @ManyToMany
    private List<User> participants = new ArrayList<>();

    public Tournament(String name, LocalDate startDate, LocalDate endDate, Language language, Visibility visibility) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.language = language;
        this.visibility = visibility;
    }

    public void addParticipant(User newParticipant) {
        this.participants.add(newParticipant);
    }
}
