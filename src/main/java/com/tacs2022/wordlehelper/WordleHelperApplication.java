package com.tacs2022.wordlehelper;

import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.tournaments.Visibility;
import com.tacs2022.wordlehelper.domain.user.User;
import com.tacs2022.wordlehelper.dtos.tournaments.NewTournamentDto;
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
			
			User julian = new User("Julian", ss.hash("1234", salt), salt);
			User agustin = new User("Agus", ss.hash("password", salt), salt);
			
			userRepo.save(julian);
			userRepo.save(agustin);

			List<Language> languages = asList(Language.ES, Language.EN);
			
			NewTournamentDto budokai = new NewTournamentDto("Budokai Tenkaichi", LocalDate.of(2022,2,2), LocalDate.of(2022, 3, 10), Visibility.PRIVATE, languages);
			Tournament budo = new Tournament(budokai, julian);
			budo.addParticipant(agustin);
			tournamentRepo.save(budo);
			
			NewTournamentDto mundialito = new NewTournamentDto("Mundialito", LocalDate.of(2023, 2, 2), LocalDate.of(2025, 2, 11), Visibility.PUBLIC, languages);
			tournamentRepo.save(new Tournament(mundialito, agustin));
		};
	}
}