package Tournaments;

import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.tournaments.Visibility;
import com.tacs2022.wordlehelper.service.TournamentService;
import org.apache.tomcat.jni.Local;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static java.time.LocalDate.of;
import static org.junit.jupiter.api.Assertions.*;

public class TournamentsTest {
    Tournament tournament;
    LocalDate startDate = of(2016, 6, 10);
    LocalDate endDate = of(2016, 6, 12);

    @BeforeEach
    public void fixture(){
        tournament = new Tournament(
                "Luchemos por la vida"
                , startDate, endDate
                , List.of(Language.EN, Language.ES)
                , Visibility.PUBLIC
        );
    }

    @Test
    public void validateInmutability(){
        assertNotEquals(endDate, endDate.plusDays(1));
    }

    @Test
    public void tournamentFinishedOnSameDayIsAlreadyFinished(){
        assertTrue(tournament.endedToDate(endDate));
    }

    @Test
    public void tournamentFinishedInDaysBeforeIsAlreadyFinished(){
        LocalDate date = LocalDate.now();
        assertTrue(tournament.endedToDate(date));
    }

    @Test
    public void tournamentThatStartsInTheFutureIsNotStarted(){
        assertFalse(tournament.startedToDate(startDate.minusDays(1)));
    }

    @Test
    public void tournamentThatEndsInTheFutureIsNotEnded(){
        assertFalse(tournament.endedToDate(endDate.minusDays(1)));
    }

    @Test
    public void tournamentStartedOnSameDayIsAlreadyStarted(){
        assertTrue(tournament.startedToDate(startDate));
    }

    @Test
    public void tournamentStartedInDaysBeforeIsAlreadyStarted(){
        assertTrue(tournament.startedToDate(startDate.plusDays(1)));
    }
}
