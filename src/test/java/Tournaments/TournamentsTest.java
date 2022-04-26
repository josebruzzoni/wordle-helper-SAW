package Tournaments;

import Utils.TournamentFactory;
import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static java.time.LocalDate.of;
import static org.junit.jupiter.api.Assertions.*;

public class TournamentsTest {
    Tournament tournament;
    LocalDate startDate = of(2016, 6, 10);
    LocalDate endDate = of(2016, 6, 12);

    @BeforeEach
    public void fixture(){
        tournament = TournamentFactory.tournamentBetweenDates(startDate, endDate);
    }

    @Test
    public void validateInmutability(){
        assertNotEquals(endDate, endDate.plusDays(1));
    }

    @Test
    public void tournamentIsNotEndedAtLastDay(){
        assertFalse(tournament.endedToDate(endDate));
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
    public void daysPassedUntilDate(){
        assertEquals(0, tournament.daysPassedToDate(startDate));
        assertEquals(1, tournament.daysPassedToDate(startDate.plusDays(1)));
        assertEquals(2, tournament.daysPassedToDate(endDate));
        assertEquals(3, tournament.daysPassedToDate(endDate.plusDays(1)));
    }

    @Test
    public void tournamentStartedInDaysBeforeIsAlreadyStarted(){
        assertTrue(tournament.startedToDate(startDate.plusDays(1)));
    }
}
