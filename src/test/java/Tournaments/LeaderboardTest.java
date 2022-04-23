package Tournaments;

import Utils.TournamentFactory;
import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.tournaments.Scoreboard;
import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.user.Result;
import com.tacs2022.wordlehelper.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.CoreMatchers.*;


import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.time.LocalDate.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LeaderboardTest {

    Tournament tournament;
    User user1;
    LocalDate startDate = of(2016, 6, 10);
    LocalDate endDate = of(2016, 6, 12);

    @BeforeEach
    public void fixture(){ //TODO: Repartir en varios tests
        user1 = new User("felipe", null, null);
            user1.addResult(new Result(2, Language.ES, startDate));
            user1.addResult(new Result(3, Language.ES, startDate));

        tournament = TournamentFactory.tournamentBetweenDates(startDate, endDate);
            tournament.setLanguages(List.of(Language.ES));
            tournament.addParticipant(user1);
    }

    @Test
    public void scoreCalculatesApropiadly() {
        assertEquals(5, tournament.getUserScoreboard(user1).getScore());
    }

    @Test
    public void scoreIgnoresResultsOfExcludedLanguages(){
        user1.addResult(new Result(3, Language.EN, startDate));//En idioma no permitido no se considera
        assertEquals(5, tournament.getUserScoreboard(user1).getScore());
    }

    @Test
    public void scoreIgnoresResultsOutOfTournamentsPeriod(){
        user1.addResult(new Result(1, Language.ES, startDate.minusDays(1)));//En idioma no permitido no se considera
        user1.addResult(new Result(0, Language.ES, endDate.plusDays(1)));//En idioma no permitido no se considera

        assertEquals(5, tournament.getUserScoreboard(user1).getScore());
    }

    @Test
    public void leaderboardGeneratesCorrectly(){

        User user2 = new User("miguelito", null, null);
             user2.addResult(new Result(4, Language.ES, startDate));
             user2.addResult(new Result(5, Language.ES, startDate));

        tournament.addParticipant(user2);

        assertEquals(1, tournament.getUserScoreboard(user2).getScore());
        List<Scoreboard> leaderboard = tournament.generateLeaderboard();

        assertThat(getFromLeaderBoard(leaderboard, Scoreboard::getUser), is(List.of(user1, user2)));
        assertThat(getFromLeaderBoard(leaderboard, Scoreboard::getScore), is(List.of(5, 1)));
        assertThat(getFromLeaderBoard(leaderboard, Scoreboard::getFailedAttempts), is(List.of(5, 9)));
    }

    private <T> List<T> getFromLeaderBoard(List<Scoreboard> leaderboard, Function<Scoreboard, T> getter){
        return leaderboard.stream().map(getter).collect(Collectors.toList());
    }
}
