package com.tacs2022.wordlehelper.utils;

public class QueryUtils {
	//conditions my tournaments
	public static final String TOURNAMENT_CONDITION_SELF = " AND t.OWNER_ID = :userId";
	
	//findByType
	public static final String TOURNAMENT_REGISTERED = "SELECT * FROM TOURNAMENT t INNER JOIN TOURNAMENT_PARTICIPANTS p ON t.ID = p.TOURNAMENT_ID WHERE p.PARTICIPANTS_ID = :userId";
	
	//conditions status
	public static final String TOURNAMENT_CONDITION_NOT_STARTED = " AND CAST(:today AS date) < CAST(START_DATE AS date)";
	public static final String TOURNAMENT_CONDITION_STARTED = " AND CAST(:today AS date) >= CAST(START_DATE AS date) AND CAST(:today AS date) <= CAST(END_DATE AS date)";
	public static final String TOURNAMENT_CONDITION_FINISHED = " AND CAST(:today AS date) > CAST(END_DATE AS date)";
}
