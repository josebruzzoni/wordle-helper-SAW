package com.tacs2022.wordlehelper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.tacs2022.wordlehelper.exceptions.ExistingUserException;
import com.tacs2022.wordlehelper.exceptions.ResultAlreadyLoadedException;
import com.tacs2022.wordlehelper.repos.UserRepository;
import com.tacs2022.wordlehelper.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.tournaments.Scoreboard;
import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.tournaments.TournamentStatus;
import com.tacs2022.wordlehelper.domain.tournaments.Visibility;
import com.tacs2022.wordlehelper.domain.user.Result;
import com.tacs2022.wordlehelper.domain.user.User;
import com.tacs2022.wordlehelper.exceptions.ForbiddenException;
import com.tacs2022.wordlehelper.repos.TournamentRepository;
import com.tacs2022.wordlehelper.service.SecurityService;
import com.tacs2022.wordlehelper.service.TournamentService;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class UserTournamenTest {

	@MockBean
	TournamentRepository tournamentRepoMock;
	@MockBean
	UserRepository userRepoMock;
	
	@Mock
	Tournament tournamentMock;
	
	@Autowired
	TournamentService tournamentService;

	@Autowired
	UserService userService;

	@Autowired
	SecurityService securityService;
	
	Tournament privateTournament;
	Tournament publicTournament;
	
	LocalDate startDate;
	LocalDate endDate;
	
	User julian;
	User agus;
	
	@BeforeEach
	public void fixture() throws NoSuchAlgorithmException, InvalidKeySpecException {
		SecurityService ss = new SecurityService();
		byte[] salt = ss.getSalt();

		julian = new User("Julian", ss.hash("1234", salt), salt);
		agus = new User("Agus", ss.hash("password", salt), salt);
		
		startDate = LocalDate.now().plusWeeks(1);
		endDate = LocalDate.now().plusWeeks(2);
		
		julian.addResult(new Result(2, Language.ES, startDate));
		julian.addResult(new Result(3, Language.ES, startDate.plusDays(1)));
		julian.addResult(new Result(4, Language.ES, startDate.plusDays(2)));
		
		agus.addResult(new Result(1, Language.ES, startDate));
		agus.addResult(new Result(7, Language.ES, startDate.plusDays(1)));
		agus.addResult(new Result(4, Language.ES, startDate.plusDays(2)));
		
		privateTournament = new Tournament("Superliga", startDate, endDate,
				Visibility.PRIVATE, List.of(Language.EN, Language.ES), julian);
		publicTournament = new Tournament("Ligue 1", startDate, endDate,
				Visibility.PUBLIC, List.of(Language.EN, Language.ES), julian);
			
	}

	@Test
	public void userPasswordIsIncorrect() {
		Mockito.when(userRepoMock.findByUsername("Julian")).thenReturn(List.of(julian));
		String pass = "sarasa";
		assertFalse(userService.validatePassword("Julian", pass));
	}

	@Test
	public void userPasswordIsCorrect() {
		Mockito.when(userRepoMock.findByUsername("Julian")).thenReturn(List.of(julian));
		String pass = "1234";
		assertTrue(userService.validatePassword("Julian", pass));
	}

	@Test
	public void userAlreadyExists() {
		Mockito.when(userRepoMock.findByUsername("Julian")).thenReturn(List.of(julian));
		Assertions
				.assertThatThrownBy ( () -> { userService.save("Julian", "123456"); } )
				.isInstanceOf(ExistingUserException.class);
	}

	@Test
	public void youCannotAddParticipantsToTournamentsThatHaveAStartedStatus() {
		Mockito.when(tournamentRepoMock.findById(anyString())).thenReturn(Optional.of(tournamentMock));
		Mockito.when(tournamentMock.getStatus()).thenReturn(TournamentStatus.STARTED);
		Assertions
			.assertThatThrownBy ( () -> { tournamentService.addParticipant(anyString(), julian, agus); } )
			.isInstanceOf(ForbiddenException.class)
			.hasMessage("Participants cannot be added to this tournament once it has started or finished");
	}
	
	@Test
	public void youCannotAddParticipantsToTournamentsThatHaveAFinishedStatus() {
		Mockito.when(tournamentRepoMock.findById(anyString())).thenReturn(Optional.of(tournamentMock));
		Mockito.when(tournamentMock.getStatus()).thenReturn(TournamentStatus.FINISHED);
		Assertions
			.assertThatThrownBy ( () -> { tournamentService.addParticipant(anyString(), julian, agus); } )
			.isInstanceOf(ForbiddenException.class)
			.hasMessage("Participants cannot be added to this tournament once it has started or finished");
	}
	
	@Test
	public void userTriedToAddParticipantToPrivateTournamentWithoutBeingTheOwner() {
		Mockito.when(tournamentRepoMock.findById(anyString())).thenReturn(Optional.of(tournamentMock));
		Mockito.when(tournamentMock.getStatus()).thenReturn(TournamentStatus.NOT_STARTED);
		Mockito.when(tournamentMock.getVisibility()).thenReturn(Visibility.PRIVATE);
		Mockito.when(tournamentMock.userIsOwner(any(User.class))).thenReturn(false);
		Assertions
			.assertThatThrownBy ( () -> { tournamentService.addParticipant(anyString(), julian, agus); } )
			.isInstanceOf(ForbiddenException.class)
			.hasMessage("User cannot add participant to this private tournament without being the owner");
	}
	
	@Test
	public void userTriedToAddParticipantToPublicTournamentWithoutBeingTheOwnerCanOnlyAddSelf() {
		Mockito.when(tournamentRepoMock.findById(anyString())).thenReturn(Optional.of(tournamentMock));
		Mockito.when(tournamentMock.getStatus()).thenReturn(TournamentStatus.NOT_STARTED);
		Mockito.when(tournamentMock.getVisibility()).thenReturn(Visibility.PUBLIC);
		Mockito.when(tournamentMock.userIsOwner(any(User.class))).thenReturn(false);
		Assertions
			.assertThatThrownBy ( () -> { tournamentService.addParticipant(anyString(), agus, julian); } )
			.isInstanceOf(ForbiddenException.class)
			.hasMessage("User can only add another participant to public tournament if owner");
	}
	
	@Test
	public void asAUserIWantToBeAbleToAddAnotherUserToAPrivateTournamentThatICreated() {
		Mockito.when(tournamentRepoMock.findById(anyString())).thenReturn(Optional.of(privateTournament));
		Assertions.assertThatNoException()
			.isThrownBy(() -> { tournamentService.addParticipant(anyString(), julian, agus); });
	}
	
	@Test 
	public void asAUserIWantToBeAbleToAddAnotherUserToAPublicTournamentThatICreated() {
		Mockito.when(tournamentRepoMock.findById(anyString())).thenReturn(Optional.of(publicTournament));
		Assertions.assertThatNoException()
			.isThrownBy(() -> { tournamentService.addParticipant(anyString(), julian, agus); });
	}
	
	@Test
	public void asAUserIWantToBeAbleToJoinAPublicTournamentThatHasNotStartedYet(){
		Mockito.when(tournamentRepoMock.findById(anyString())).thenReturn(Optional.of(publicTournament));
		Assertions.assertThatNoException()
			.isThrownBy(() -> { tournamentService.addParticipant(anyString(), agus, agus); });
	}

	@Test
	public void asAUserIWantToSubmitResultsButOnlyOnceForLanguage(){
		Mockito.when(userRepoMock.findById(anyString())).thenReturn(Optional.of(agus));
		Result otherSpanishResult = new Result(3, Language.ES, startDate);
		Result englishResult = new Result(2, Language.EN, startDate);
		Assertions
				.assertThatNoException()
				.isThrownBy(() -> { userService.addResult(anyString(), englishResult); });
		Assertions
				.assertThatThrownBy ( () -> { userService.addResult(anyString(), otherSpanishResult); } )
				.isInstanceOf(ResultAlreadyLoadedException.class);
	}
	
	@Test
	public void theParticipantWithTheFewestAttemptsWins() {
		publicTournament.addParticipant(agus);
		Mockito.when(tournamentRepoMock.findById(anyString())).thenReturn(Optional.of(publicTournament));
		List<Scoreboard> leaderboard = tournamentService.getTournamentLeaderboard(anyString() , startDate.plusDays(2), julian);
		Scoreboard scoreboardOne = leaderboard.get(0);
		Scoreboard scoreboardTwo = leaderboard.get(1);
		assertTrue(scoreboardOne.getTotalAttempts() < scoreboardTwo.getTotalAttempts());
		assertEquals(julian.getUsername(), scoreboardOne.getUser().getUsername());
	}
	
	@Test
	public void publicTournamentsTheyAreVisibleToAll() {
		Mockito.when(tournamentRepoMock.findById(anyString())).thenReturn(Optional.of(publicTournament));
		Assertions.assertThatNoException()
		.isThrownBy(() -> { tournamentService.getByIdAndValidateVisibility(anyString(), agus); });
	}
	
	@Test
	public void privateTournamentsTheyAreVisibleOnlyByThePersonWhoCreatedThem() {
		Mockito.when(tournamentRepoMock.findById(anyString())).thenReturn(Optional.of(privateTournament));
		Assertions
			.assertThatThrownBy ( () -> { tournamentService.getByIdAndValidateVisibility(anyString(), agus); } )
			.isInstanceOf(ForbiddenException.class)
			.hasMessage("User does not have permissions to view this tournament");
	}
	
	@Test
	public void privateTournamentsTheyAreVisibleByThePersonWhoCreatedThem() {
		Mockito.when(tournamentRepoMock.findById(anyString())).thenReturn(Optional.of(privateTournament));
		Assertions.assertThatNoException()
			.isThrownBy(() -> { tournamentService.getByIdAndValidateVisibility(anyString(), julian); });
	}
	
	@Test
	public void privateTournamentsTheyAreVisibleByThoseWhoHaveJoined(){
		privateTournament.addParticipant(agus);
		Mockito.when(tournamentRepoMock.findById(anyString())).thenReturn(Optional.of(privateTournament));
		Assertions.assertThatNoException()
			.isThrownBy(() -> { tournamentService.getByIdAndValidateVisibility(anyString(), agus); });
	}
}
