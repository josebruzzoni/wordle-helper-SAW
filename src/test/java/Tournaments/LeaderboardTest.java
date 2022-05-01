package Tournaments;

import Utils.TournamentFactory;
import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.tournaments.Scoreboard;
import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.user.Result;
import com.tacs2022.wordlehelper.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.time.LocalDate;
import java.util.List;

import static java.time.LocalDate.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LeaderboardTest {

    Tournament tournament;
    User user1;
    int failedAttempts;
    LocalDate startDate = of(2016, 6, 10);
    LocalDate endDate = of(2016, 6, 12);

    @BeforeEach
    public void fixture(){
        user1 = new User("felipe", null, null);
            user1.addResult(new Result(2, Language.ES, startDate));
            user1.addResult(new Result(3, Language.ES, startDate.plusDays(1)));

        failedAttempts = 5;

        tournament = TournamentFactory.tournamentBetweenDates(startDate, endDate);
            tournament.setLanguages(List.of(Language.ES));
            tournament.addParticipant(user1);
    }

    @Test
    public void userPlaysAllDaysAndHisScoreEqualsToHisFailAttempts() {
        assertEquals(failedAttempts, new Scoreboard(user1, tournament).getScoreAtDate(endDate));
    }

    @Test
    public void scoreIgnoresResultsOfExcludedLanguages(){
        user1.addResult(new Result(3, Language.EN, startDate));
        assertEquals(5, new Scoreboard(user1, tournament).getScoreAtDate(endDate));
    }

    @Test
    public void scoreIgnoresResultsOutOfTournamentsPeriod(){
        user1.addResult(new Result(1, Language.ES, startDate.minusDays(1)));
        user1.addResult(new Result(0, Language.ES, endDate.plusDays(1)));
        assertEquals(5, new Scoreboard(user1, tournament).getScoreAtDate(endDate));
    }

    @Test
    public void daysNotPlayedAreCorrectlyPenalized(){
        LocalDate endDateModified = endDate.plusDays(2);
        tournament.setEndDate(endDateModified);

        assertEquals(19, new Scoreboard(user1, tournament).getScoreAtDate(endDateModified));
    }

    /*@Test
    public void leaderboardGeneratesCorrectly(){

        User user2 = new User("miguelito", null, null);
             user2.addResult(new Result(4, Language.ES, startDate));
             user2.addResult(new Result(5, Language.ES, startDate));

        tournament.addParticipant(user2);

        assertEquals(9, new Scoreboard(user2, tournament).getBadScoreToDate(endDate));
        List<Scoreboard> leaderboard = tournament.generateLeaderboardToDate(endDate);

        assertThat(getFromLeaderBoard(leaderboard, Scoreboard::getUser), is(List.of(user1, user2)));
        assertThat(getFromLeaderBoard(leaderboard, s->s.getBadScoreToDate(endDate)), is(List.of(5, 9)));
        assertThat(getFromLeaderBoard(leaderboard, Scoreboard::getFailedAttempts), is(List.of(5, 9)));
    }*/



   /* private <T> List<T> getFromLeaderBoard(List<Scoreboard> leaderboard, Function<Scoreboard, T> getter){
        return leaderboard.stream().map(getter).collect(Collectors.toList());
    }*/
}
