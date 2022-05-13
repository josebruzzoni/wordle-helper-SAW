package com.tacs2022.wordlehelper;

import static java.time.LocalDate.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.tournaments.TournamentStatus;
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
	
	@Mock
	Tournament tournamentMock;
	
	@Autowired
	TournamentService tournamentService;
	
	Tournament privateTournament;
	Tournament publicTournament;
	
	User julian;
	User agus;
	
	@BeforeEach
	public void fixture() throws NoSuchAlgorithmException, InvalidKeySpecException {
		SecurityService ss = new SecurityService();
		byte[] salt = ss.getSalt();

		julian = new User("Julian", ss.hash("1234", salt), salt);
		agus = new User("Agus", ss.hash("password", salt), salt);
		
		privateTournament = new Tournament("Superliga", LocalDate.now().plusWeeks(1), LocalDate.now().plusWeeks(2),
				Visibility.PRIVATE, List.of(Language.EN, Language.ES), julian);
		publicTournament = new Tournament("Ligue 1", LocalDate.now().plusWeeks(1), LocalDate.now().plusWeeks(2),
				Visibility.PUBLIC, List.of(Language.EN, Language.ES), julian);
			
	}
	
	@Test
	public void youCannotAddParticipantsToTournamentsThatHaveAStartedStatus() {
		Mockito.when(tournamentRepoMock.findById(anyLong())).thenReturn(Optional.of(tournamentMock));
		Mockito.when(tournamentMock.getStatus()).thenReturn(TournamentStatus.STARTED);
		Assertions
			.assertThatThrownBy ( () -> { tournamentService.addParticipant(Long.valueOf(1), julian, agus); } )
			.isInstanceOf(ForbiddenException.class)
			.hasMessage("Participants cannot be added to this tournament once it has started or finished");
	}
	
	@Test
	public void youCannotAddParticipantsToTournamentsThatHaveAFinishedStatus() {
		Mockito.when(tournamentRepoMock.findById(anyLong())).thenReturn(Optional.of(tournamentMock));
		Mockito.when(tournamentMock.getStatus()).thenReturn(TournamentStatus.FINISHED);
		Assertions
			.assertThatThrownBy ( () -> { tournamentService.addParticipant(Long.valueOf(1), julian, agus); } )
			.isInstanceOf(ForbiddenException.class)
			.hasMessage("Participants cannot be added to this tournament once it has started or finished");
	}
	
	@Test
	public void userTriedToAddParticipantToPrivateTournamentWithoutBeingTheOwner() {
		Mockito.when(tournamentRepoMock.findById(anyLong())).thenReturn(Optional.of(tournamentMock));
		Mockito.when(tournamentMock.getStatus()).thenReturn(TournamentStatus.NOTSTARTED);
		Mockito.when(tournamentMock.getVisibility()).thenReturn(Visibility.PRIVATE);
		Mockito.when(tournamentMock.userIsOwner(any(User.class))).thenReturn(false);
		Assertions
			.assertThatThrownBy ( () -> { tournamentService.addParticipant(Long.valueOf(1), julian, agus); } )
			.isInstanceOf(ForbiddenException.class)
			.hasMessage("User cannot add participant to this private tournament without being the owner");
	}
	
	@Test
	public void userTriedToAddParticipantToPublicTournamentWithoutBeingTheOwnerCanOnlyAddSelf() {
		Mockito.when(tournamentRepoMock.findById(anyLong())).thenReturn(Optional.of(tournamentMock));
		Mockito.when(tournamentMock.getStatus()).thenReturn(TournamentStatus.NOTSTARTED);
		Mockito.when(tournamentMock.getVisibility()).thenReturn(Visibility.PUBLIC);
		Mockito.when(tournamentMock.userIsOwner(any(User.class))).thenReturn(false);
		Assertions
			.assertThatThrownBy ( () -> { tournamentService.addParticipant(Long.valueOf(1), agus, julian); } )
			.isInstanceOf(ForbiddenException.class)
			.hasMessage("User can only add another participant to public tournament if owner");
	}
	
	@Test
	public void asAUserIWantToBeAbleToAddAnotherUserToAPrivateTournamentThatICreated() {
		Mockito.when(tournamentRepoMock.findById(anyLong())).thenReturn(Optional.of(privateTournament));
		Assertions.assertThatNoException()
			.isThrownBy(() -> { tournamentService.addParticipant(Long.valueOf(1), julian, agus); });
	}
	
	@Test 
	public void asAUserIWantToBeAbleToAddAnotherUserToAPublicTournamentThatICreated() {
		Mockito.when(tournamentRepoMock.findById(anyLong())).thenReturn(Optional.of(publicTournament));
		Assertions.assertThatNoException()
			.isThrownBy(() -> { tournamentService.addParticipant(Long.valueOf(1), julian, agus); });
	}
	
	@Test
	public void asAUserIWantToBeAbleToJoinAPublicTournamentThatHasNotStartedYet(){
		Mockito.when(tournamentRepoMock.findById(anyLong())).thenReturn(Optional.of(publicTournament));
		Assertions.assertThatNoException()
			.isThrownBy(() -> { tournamentService.addParticipant(Long.valueOf(1), agus, agus); });
	}
	
	@Test
	public void a() {
		
	}
}
