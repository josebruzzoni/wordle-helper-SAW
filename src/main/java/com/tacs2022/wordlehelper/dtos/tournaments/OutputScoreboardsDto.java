package com.tacs2022.wordlehelper.dtos.tournaments;

import java.util.List;
import java.util.stream.Collectors;

import com.tacs2022.wordlehelper.domain.tournaments.Scoreboard;

import lombok.Data;

@Data
public class OutputScoreboardsDto {
	//TODO: arreglar todo este desastre, NORMALIZAR como nos vamos a manejar con los dtos (sobre todo los de output)
	private List<OutputScoreboardDto> leaderboard;

	public OutputScoreboardsDto(List<Scoreboard> leaderboard) {
		this.leaderboard = leaderboard
				.stream()
				.map( (Scoreboard scoreboard) -> new OutputScoreboardDto(scoreboard))
				.collect(Collectors.toList());
	}
	
	
}
