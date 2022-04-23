package com.tacs2022.wordlehelper.domain.user;
import com.tacs2022.wordlehelper.domain.Language;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class Result {
    @Id @GeneratedValue
    private Long id;
    private Integer failedAttempts;
    private Language language;
    private LocalDate date;

    public Result(Integer failedAttempts, Language language, LocalDate date) {
        this.failedAttempts = failedAttempts;
        this.language = language;
        this.date = date;
    }

    public int getScore() {
        int attemptsByDay = 6;
        return 10*(attemptsByDay - failedAttempts);
    }
}


