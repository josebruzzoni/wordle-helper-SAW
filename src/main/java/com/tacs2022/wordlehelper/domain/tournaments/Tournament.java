package com.tacs2022.wordlehelper.domain.tournaments;

import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.user.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Document("tournaments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tournament {
    @Id
    private String id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Visibility visibility;
    private List<Language> languages;
    private User owner;
    private List<User> participants = new LinkedList<>();

    public Tournament(String name, LocalDate startDate, LocalDate endDate, Visibility visibility, List<Language> languages, User owner) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.visibility = visibility;
        this.languages = languages;
        this.owner = owner;
        this.participants.add(owner);
    }

    public Boolean isAParticipant(User newParticipant) {
    	return this.participants.contains(newParticipant);
    }

    public void addParticipant(User newParticipant) {
        this.participants.add(newParticipant);
    }
    
    public Boolean userIsOwner(User user) {
    	return this.owner.equals(user);
    }

    public List<Scoreboard> generateLeaderboardAtDate(LocalDate date){
        return participants.stream()
                .map(u->new Scoreboard(u, this))
                .sorted(Comparator.comparing(s->s.getScoreAtDate(date)))
                .collect(Collectors.toList());
    }

    /**
     * Returns the amount of days that were played since the tournament started until
     * the given date.
     * If tournament has not started, returns 0.
     * @param date Date to count played days by.
     * @return Days played until date
     */
    public int getDaysPlayedUntilDate(LocalDate date){
        TournamentStatus status = getStatusByDate(date);

        if (status == TournamentStatus.NOT_STARTED)
            return 0;
        else if (status == TournamentStatus.STARTED)
            return (int) startDate.datesUntil(date).count();
        else
            return (int) (startDate.datesUntil(endDate).count() + 1);
    }

    public int getGamesPlayedUntilDate(LocalDate date){
        return getDaysPlayedUntilDate(date)*getLanguages().size();
    }

    public boolean supportsLanguage(Language language) {
        return languages.contains(language);
    }
    
    public TournamentStatus getStatus() {
    	return getStatusByDate(LocalDate.now());
    }

    public TournamentStatus getStatusByDate(LocalDate date){
        if (date.isBefore(startDate))
            return TournamentStatus.NOT_STARTED;
        else if (date.isAfter(endDate))
            return TournamentStatus.FINISHED;
        else
            return TournamentStatus.STARTED;
    }
}
