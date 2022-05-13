package com.tacs2022.wordlehelper;

import static java.time.LocalDate.of;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.tournaments.Visibility;
import com.tacs2022.wordlehelper.domain.user.User;
import com.tacs2022.wordlehelper.exceptions.ForbiddenException;
import com.tacs2022.wordlehelper.repos.TournamentRepository;
import com.tacs2022.wordlehelper.service.SecurityService;
import com.tacs2022.wordlehelper.service.TournamentService;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class TournamentServiceTest {

	@MockBean
	TournamentRepository tournamentRepoMock;
	
	@Autowired
	TournamentService tournamentService;
	
	Tournament tournament;
	LocalDate startDate = of(2016, 6, 10);
	LocalDate endDate = of(2016, 6, 12);
	
	User julian;
	User agus;
	
	@BeforeEach
	public void fixture() throws NoSuchAlgorithmException, InvalidKeySpecException {
		SecurityService ss = new SecurityService();
		byte[] salt = ss.getSalt();

		julian = new User("Julian", ss.hash("1234", salt), salt);
		tournament = new Tournament("Luchemos por la vida", startDate, endDate,
				Visibility.PRIVATE, List.of(Language.EN, Language.ES), julian);
		
		agus = new User("Julian", ss.hash("1234", salt), salt);
	}
	
	@Test
	public void aTest() {
		Mockito.when(tournamentRepoMock.findById(tournament.getId())).thenReturn(Optional.of(tournament));
		Assertions.assertThatThrownBy(() -> {
			tournamentService.addParticipant(tournament.getId(), julian, agus);
		})
		.isInstanceOf(ForbiddenException.class)
		.hasMessage("Participants cannot be added to this tournament once it has started or finished");
	}
}
