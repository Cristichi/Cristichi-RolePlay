package obj;

public enum RoleClass {
	BEGGAR("Beggar", new Stats(0,0,0,0,0), "[Beggar]"), 
	WARRIOR("Warrior", new Stats(2, 0, .1f, 0, .1f), "[Warrior]"),
	TANK("Tank", new Stats(0, 0, .4f, .4f, .4f), "[Tank]"),
	ARCHER("Archer", new Stats(0, 0, .4f, .4f, .4f), "[Archer]"),
	;
	
	private Stats stats;
	private String name, prefix, suffix;
	
	private RoleClass(String name, Stats stats) {
		this.name = name;
		this.stats = stats;
		prefix = suffix = "";
	}
	
	private RoleClass(String name, Stats stats, String prefix) {
		this.name = name;
		this.stats = stats;
		this.prefix = prefix.trim();
		suffix = "";
	}
	
	private RoleClass(String name, Stats stats, String prefix, String suffix) {
		this.name = name;
		this.stats = stats;
		this.prefix = prefix.trim();
		this.suffix = suffix.trim();
	}

	public Stats getStats() {
		return stats;
	}

	public String getName() {
		return name;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getSuffix() {
		return suffix;
	}

}
