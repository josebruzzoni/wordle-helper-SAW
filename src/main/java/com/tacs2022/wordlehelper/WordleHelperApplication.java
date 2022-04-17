package com.tacs2022.wordlehelper;

import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.tournaments.Visibility;
import com.tacs2022.wordlehelper.domain.user.User;
import com.tacs2022.wordlehelper.repos.TournamentRepository;
import com.tacs2022.wordlehelper.repos.UserRepository;

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
			userRepo.save(new User(1L, "Julian", "1234"));
			userRepo.save(new User(2L, "Agus", "password"));

			List<Language> languages = asList(Language.ES, Language.EN);
			tournamentRepo.save(new Tournament("Budokai Tenkaichi", LocalDate.of(2022,2,2), LocalDate.of(2022, 2, 10), languages, Visibility.PRIVATE));
			tournamentRepo.save(new Tournament("Mundialito", LocalDate.of(2022, 2, 2), LocalDate.of(2022, 2, 11), languages, Visibility.PUBLIC));
		};
	}
}
