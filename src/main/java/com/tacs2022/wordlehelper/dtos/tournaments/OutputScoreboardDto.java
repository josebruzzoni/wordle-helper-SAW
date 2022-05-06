package com.tacs2022.wordlehelper.dtos.tournaments;

import java.util.List;
import java.util.stream.Collectors;
import com.tacs2022.wordlehelper.domain.tournaments.Scoreboard;
import com.tacs2022.wordlehelper.dtos.user.UsernameDto;
import lombok.Data;

@Data
public class OutputScoreboardDto {
	private UsernameDto user;
	private int badScore;
	private int failedAttempts;
	
	public OutputScoreboardDto(Scoreboard scoreboard) {
		this.user = new UsernameDto(scoreboard.getUser());
		this.badScore = scoreboard.getBadScore();
		this.failedAttempts = scoreboard.getFailedAttempts();
	}

	public static List<OutputScoreboardDto> list(List<Scoreboard> scoreboards){
		return scoreboards.stream().map(OutputScoreboardDto::new).collect(Collectors.toList());
	}
}
