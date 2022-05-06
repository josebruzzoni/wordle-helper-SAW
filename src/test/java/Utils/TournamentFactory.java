package Utils;

import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.tournaments.Visibility;
import com.tacs2022.wordlehelper.domain.user.User;
import com.tacs2022.wordlehelper.dtos.tournaments.NewTournamentDto;
import com.tacs2022.wordlehelper.service.SecurityService;

import java.time.LocalDate;
import java.util.List;

public class TournamentFactory {


    public static Tournament tournamentBetweenDates(LocalDate startDate, LocalDate endDate) {
    	SecurityService ss = new SecurityService();
		byte[] salt = ss.getSalt();

		User julian = new User("Julian", ss.hash("1234", salt), salt);

        return new Tournament("Luchemos por la vida", startDate, endDate, Visibility.PUBLIC, List.of(Language.ES), julian);
    }
}
