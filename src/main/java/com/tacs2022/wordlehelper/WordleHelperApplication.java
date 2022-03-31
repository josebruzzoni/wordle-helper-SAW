package com.tacs2022.wordlehelper;

import com.tacs2022.wordlehelper.domain.User;
import com.tacs2022.wordlehelper.repos.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WordleHelperApplication {

	public static void main(String[] args) {
		SpringApplication.run(WordleHelperApplication.class, args);
	}

	@Bean
	CommandLineRunner initDatabase(UserRepository userRepo) {
		return args -> {
			userRepo.save(new User(1l, "Julian", "1234"));
			userRepo.save(new User(2l, "Agus", "password"));
		};
	}
}
