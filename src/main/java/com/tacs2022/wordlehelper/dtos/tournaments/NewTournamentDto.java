package com.tacs2022.wordlehelper.dtos.tournaments;

import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.tournaments.Visibility;
import com.tacs2022.wordlehelper.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewTournamentDto {
    @NotBlank(message = "name is mandatory")
    private String name;
    @NotNull(message = "startDate is mandatory")
    @FutureOrPresent(message = "startDate must be future or present")
    private LocalDate startDate;
    @NotNull(message = "endDate is mandatory")
    private LocalDate endDate;
    @NotNull(message = "visibility is mandatory")
    private Visibility visibility;

    @NotNull(message = "languages are mandatory")
    private List<Language> languages;


    public Tournament asTournamentWithOwner(User owner){
        if(startDate.isAfter(endDate)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "endDate must be the same or after startDate value");
        }

        return new Tournament(name, startDate, endDate, visibility, languages, owner);
    }

    public boolean hasValidDates(){
        return this.startDate != null && this.endDate != null && this.startDate.isBefore(this.endDate);
    }
}
