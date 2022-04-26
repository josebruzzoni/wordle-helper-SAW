package com.tacs2022.wordlehelper.dtos.tournaments;

import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.tournaments.Visibility;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class NewTournamentDto {
    @NotBlank(message = "name is mandatory")
    private String name;
    @NotNull(message = "startDate is mandatory")
    private LocalDate startDate;
    @NotNull(message = "startDate is mandatory")
    private LocalDate endDate;
    @NotNull(message = "visibility is mandatory")
    private Visibility visibility;

    @NotNull(message = "languages are mandatory")
    private List<Language> languages;

    public Tournament fromDTO(){
        return new Tournament(name, startDate, endDate, languages, visibility);
    }
}
