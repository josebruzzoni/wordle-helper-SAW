package com.tacs2022.wordlehelper.dtos.tournaments;

import java.util.List;
import java.util.stream.Collectors;

import com.tacs2022.wordlehelper.domain.tournaments.Scoreboard;

import lombok.Data;

@Data
public class OutputScoreboardsDto {
	private List<OutputScoreboardDto> leaderboard;

	public OutputScoreboardsDto(List<Scoreboard> leaderboard) {
		this.leaderboard = leaderboard
				.stream()
				.map(OutputScoreboardDto::new)
				.collect(Collectors.toList());
	}
	
	
}
