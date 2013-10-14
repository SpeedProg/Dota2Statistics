package tk.speedprog.dota2.statistics;

public enum GameType {
	ALL_GAMES("All Games", 0), STAT_GAME("Stat Games Only", 1), NONE_STAT_GAME("None Stat Games Only", 2);
	private String name;
	private int type;

	GameType(String name, int type) {
		this.name = name;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public int getType() {
		return type;
	}
	
	public String toString() {
		return name;
	}
}
