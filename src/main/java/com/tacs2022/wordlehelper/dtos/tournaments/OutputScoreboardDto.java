package com.tacs2022.wordlehelper.dtos.tournaments;

import java.util.HashMap;
import java.util.Map;

import com.tacs2022.wordlehelper.domain.tournaments.Scoreboard;

import lombok.Data;

@Data
public class OutputScoreboardDto {
	private Map<String, Object> user;
	private int badScore;
	private int failedAttempts;
	
	public OutputScoreboardDto(Scoreboard scoreboard) {
		this.user = new HashMap<>();
		this.user.put("id", scoreboard.getUser().getId());
		this.user.put("username", scoreboard.getUser().getUsername());
		this.badScore = scoreboard.getBadScore();
		this.failedAttempts = scoreboard.getFailedAttempts();
	}
	
	
}
