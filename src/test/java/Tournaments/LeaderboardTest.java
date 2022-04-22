package Tournaments;

import Utils.TournamentFactory;
import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.tournaments.Position;
import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.user.Result;
import com.tacs2022.wordlehelper.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.LocalDate.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LeaderboardTest { //TODO: Ver si queda score o filedAttempts. Idea: score = oportunities - failedAttempts, asi el que nunca jugo no tiene ventaja sobre el que si

    Tournament tournament;
    User user1;
    User user2;
    LocalDate startDate = of(2016, 6, 10);
    LocalDate endDate = of(2016, 6, 12);

    @BeforeEach
    public void fixture(){ //TODO: Repartir en varios tests
        user1 = new User("felipe", null, null);
            user1.addResult(new Result(5, Language.ES, startDate));
            user1.addResult(new Result(4, Language.ES, startDate));
            user1.addResult(new Result(5, Language.EN, startDate));//En idioma no permitido no se considera
            user1.addResult(new Result(5, Language.ES, startDate.minusDays(1)));//En idioma no permitido no se considera
            user1.addResult(new Result(5, Language.ES, endDate.plusDays(1)));//En idioma no permitido no se considera

        user2 = new User("miguelito", null, null);
            user2.addResult(new Result(4, Language.ES, startDate));
            user2.addResult(new Result(6, Language.ES, startDate));

        tournament = TournamentFactory.tournamentBetweenDates(startDate, endDate);

            tournament.addParticipant(user1);
            tournament.addParticipant(user2);

    }

    @Test
    public void scoreCalculatesApropiadly() {
        assertEquals(9, tournament.scoreForUser(user1));
        assertEquals(10, tournament.scoreForUser(user2));
    }

    @Test
    public void leaderboardGeneratesCorrectly(){
        List<Position> leaderboard = tournament.generateLeaderboard();
        assertEquals("["+user1.getUsername()+", "+user2.getUsername()+"]", leaderboard.stream().map(Position::getUsername).collect(Collectors.toList()).toString());
    }
}
