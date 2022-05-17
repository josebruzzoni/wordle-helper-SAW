package Tournaments;

import Utils.UserFactory;
import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.tournaments.Scoreboard;
import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.tournaments.TournamentStatus;
import com.tacs2022.wordlehelper.domain.tournaments.Visibility;
import com.tacs2022.wordlehelper.domain.user.Result;
import com.tacs2022.wordlehelper.domain.user.User;
import com.tacs2022.wordlehelper.service.SecurityService;
import org.hibernate.validator.constraints.Range;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

import static java.time.LocalDate.of;
import static org.junit.jupiter.api.Assertions.*;

class TournamentsTest {
	Tournament tournament;
	LocalDate startDate = of(2016, 6, 10);
	LocalDate endDate = of(2016, 6, 13);

	@BeforeEach
	public void fixture() {
		SecurityService ss = new SecurityService();
		byte[] salt = ss.getSalt();

		User julian = new User("Julian", ss.hash("1234", salt), salt);

		tournament = new Tournament("Luchemos por la vida", startDate, endDate,
				Visibility.PUBLIC, List.of(Language.EN), julian);
	}

    @Test
    public void tournamentIsNotEndedAtLastDay(){
        assertEquals(TournamentStatus.STARTED, tournament.getStatusByDate(endDate));
    }


	@Test
	public void tournamentFinishedInDaysBeforeIsAlreadyFinished() {
		LocalDate date = LocalDate.now();
		assertEquals(TournamentStatus.FINISHED, tournament.getStatusByDate(date));
	}

	@Test
	public void tournamentThatStartsInTheFutureIsNotStarted() {
		assertEquals(TournamentStatus.NOT_STARTED, tournament.getStatusByDate(startDate.minusDays(1)));
	}

	@Test
	public void tournamentThatEndsInTheFutureIsNotEnded() {
		assertNotEquals(TournamentStatus.FINISHED, tournament.getStatusByDate(endDate.minusDays(1)));
	}

	@Test
	public void tournamentStartedOnSameDayIsAlreadyStarted() {
		assertEquals(TournamentStatus.STARTED, tournament.getStatusByDate(startDate));
	}

	@Test
	public void tournamentStartedInDaysBeforeIsAlreadyStarted() {
		assertEquals(TournamentStatus.STARTED, tournament.getStatusByDate(startDate.plusDays(1)));
	}

}
