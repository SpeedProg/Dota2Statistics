package general;

public enum GameMode {
	ALL_GAME_MODES("All Game Modes", -1, GameType.ALL_GAMES),
	NO_MODE_SET("No mode set", 0, GameType.STAT_GAME),
	ALL_PICK("All Pick", 1, GameType.STAT_GAME),
	CAPTAINS_MODE("Captains Mode", 2, GameType.STAT_GAME),
	RANDOM_DRAFT("Random Draft", 3, GameType.STAT_GAME),
	SINGLE_DRAFT("Single Draft", 4, GameType.STAT_GAME),
	ALL_RANDOM("All Random", 5, GameType.STAT_GAME),
	INTRO_DEATH("?? INTRO/DEATH ??", 6, GameType.STAT_GAME),
	DIRETIDE("Diretide", 7, GameType.NONE_STAT_GAME),
	REVERSE_CAPTAINS_MODE("Reverse Captains Mode", 8, GameType.STAT_GAME),
	GREEVILING("Greeviling", 9, GameType.NONE_STAT_GAME),
	TUTORIAL("Tutorial", 10, GameType.STAT_GAME),
	MID_ONLY("Mid Only", 11, GameType.STAT_GAME),
	LEAST_PLAYED("Least Played", 12, GameType.STAT_GAME),
	LIMITED_HEROES("Limited Heroes", 13, GameType.NONE_STAT_GAME),
	COMPENDIUM("Compendium", 14, GameType.STAT_GAME);
	private final String name;
	private final int id;
	private final GameType gameType;

	GameMode(String name, int id, GameType gameType) {
		this.id = id;
		this.name = name;
		this.gameType = gameType;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}
	
	public GameType getGameType() {
		return gameType;
	}

	public static final GameMode getGameModeFromId(int id) {
		for (GameMode g : GameMode.values()) {
			if (g.id == id) {
				return g;
			}
		}
		return null;
	}

	public String toString() {
		return this.name;
	}
}
