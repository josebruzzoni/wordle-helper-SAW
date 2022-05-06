package com.tacs2022.wordlehelper.dtos.tournaments;

import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class OutputTournamentsDto {
    List<OutputTournamentDto> tournaments;

	public OutputTournamentsDto(List<Tournament> tournaments) {
		this.tournaments = tournaments.stream()
				.map(OutputTournamentDto::new)
				.collect(Collectors.toList());
	}
}
