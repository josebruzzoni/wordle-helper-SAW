package Utils;

import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.tournaments.Visibility;

import java.time.LocalDate;
import java.util.List;

public class TournamentFactory {


    public static Tournament tournamentBetweenDates(LocalDate startDate, LocalDate endDate) {
        return new Tournament(
                "Luchemos por la vida"
                , startDate, endDate
                , List.of(Language.ES)
                , Visibility.PUBLIC
        );
    }
}
