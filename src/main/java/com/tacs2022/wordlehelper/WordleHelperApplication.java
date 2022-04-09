package com.tacs2022.wordlehelper;

import com.tacs2022.wordlehelper.domain.Tournaments.Language;
import com.tacs2022.wordlehelper.domain.Tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.Tournaments.Visibility;
import com.tacs2022.wordlehelper.domain.User;
import com.tacs2022.wordlehelper.repos.TournamentRepository;
import com.tacs2022.wordlehelper.repos.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;

import javax.swing.text.html.parser.Entity;
import java.time.LocalDate;
import java.util.Arrays;

@SpringBootApplication
public class WordleHelperApplication {

	public static void main(String[] args) {
		SpringApplication.run(WordleHelperApplication.class, args);
	}

	@Bean
	CommandLineRunner initDatabase(UserRepository userRepo, TournamentRepository tournamentRepo) {

		return args -> {
			hardcodear(userRepo,
					new User(1l, "Julian", "1234")
					, new User(2l, "Agus", "password")
			);

			hardcodear(tournamentRepo,
					new Tournament("Budokay Tenkaichi", LocalDate.parse("2222-02-02"), LocalDate.parse("2222-02-10"), Language.ES, Visibility.PUBLIC)
					, new Tournament("Mundialito", LocalDate.parse("2222-02-02"), LocalDate.parse("2222-02-11"), Language.ES, Visibility.PUBLIC)
			);
		};
	}

	private <T, Repo extends CrudRepository<T, Long>> void hardcodear(Repo repo, T... hardcodeos){
		repo.saveAll(Arrays.asList(hardcodeos));
	}
}
