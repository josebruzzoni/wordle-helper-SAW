package com.tacs2022.wordlehelper;

import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.tournaments.Visibility;
import com.tacs2022.wordlehelper.domain.user.Result;
import com.tacs2022.wordlehelper.domain.user.User;
import com.tacs2022.wordlehelper.repos.TournamentRepository;
import com.tacs2022.wordlehelper.repos.UserRepository;
import com.tacs2022.wordlehelper.service.SecurityService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.List;

import static java.util.Arrays.asList;

@SpringBootApplication
public class WordleHelperApplication {

	public static void main(String[] args) {
		SpringApplication.run(WordleHelperApplication.class, args);
	}

	@Bean
	CommandLineRunner initDatabase(UserRepository userRepo, TournamentRepository tournamentRepo) {

		return args -> {
			SecurityService ss = new SecurityService();
			byte[] salt = ss.getSalt();
			User user1 =new User("Julian", ss.hash("1234", salt), salt);
			user1.addResult(new Result(2, Language.ES, LocalDate.of(2021, 7, 19)));
			user1.addResult(new Result(1, Language.ES, LocalDate.of(2021, 8, 19)));

			userRepo.save(user1);
			userRepo.save(new User("Agus", ss.hash("password", salt), salt));

			List<Language> languages = asList(Language.ES, Language.EN);

			Tournament publicTournament = new Tournament("Copa America", LocalDate.of(2021, 7, 19), LocalDate.of(2021, 7, 21), languages, Visibility.PUBLIC);
			publicTournament.addParticipant(user1);

			tournamentRepo.save(publicTournament);
			tournamentRepo.save(new Tournament("Budokai Tenkaichi", LocalDate.of(2022,2,2), LocalDate.of(2023, 2, 10), languages, Visibility.PRIVATE));
			tournamentRepo.save(new Tournament("Mundialito", LocalDate.of(2023, 2, 2), LocalDate.of(2025, 2, 11), languages, Visibility.PUBLIC));
		};
	}
}
