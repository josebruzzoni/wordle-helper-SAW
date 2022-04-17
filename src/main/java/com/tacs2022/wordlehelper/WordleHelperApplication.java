package com.tacs2022.wordlehelper;

import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.tournaments.Visibility;
import com.tacs2022.wordlehelper.domain.user.User;
import com.tacs2022.wordlehelper.repos.TournamentRepository;
import com.tacs2022.wordlehelper.repos.UserRepository;

import com.tacs2022.wordlehelper.service.SecurityService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.time.LocalDate;

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
			userRepo.save(new User("Julian", ss.hash("1234", salt), salt));
			userRepo.save(new User("Agus", ss.hash("password", salt), salt));

			tournamentRepo.save(new Tournament("Budokai Tenkaichi", LocalDate.parse("2222-02-02"), LocalDate.parse("2222-02-10"), Language.ES, Visibility.PUBLIC));
			tournamentRepo.save(new Tournament("Mundialito", LocalDate.parse("2222-02-02"), LocalDate.parse("2222-02-11"), Language.ES, Visibility.PUBLIC));
		};
	}
}
