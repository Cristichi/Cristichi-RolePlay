package obj;

import util.SortedList;

public enum RoleClass {
	BEGGAR("Beggar", "[Beggar]"),
	WARRIOR("Warrior", "[Warrior]"),
	TANK("Tank", "[Tank]"),
	ARCHER("Archer", "[Archer]"),
	;
	static {
		BEGGAR.addLevels(new Level[] {
				new Level(0, 0, 0, 0, 0, 0),
				new Level(100, 0, 0, 0, 0, 0),
				new Level(200, 0, 0, 0, 0, 0),
		});
		WARRIOR.addLevels(new Level[] {
				new Level(0, 2, 0, .1f, .1f, 0),
				new Level(100, 4, 0, .1f, .1f, 0),
				new Level(200, 6, 0, .1f, .1f, 0),
		});
		TANK.addLevels(new Level[] {
				new Level(0, 0, 0, .4f, .4f, .4f),
				new Level(100, 0, 0, .5f, .4f, .4f),
				new Level(200, 0, 0, .6f, .4f, .4f),
		});
		ARCHER.addLevels(new Level[] {
				new Level(0, 0, 2, .1f, 0, .1f),
				new Level(100, 0, 4, .1f, 0, .1f),
				new Level(200, 0, 6, .1f, 0, .1f),
		});
		
	}

	private SortedList<Level> levels;
	private String name, prefix, suffix;

	private RoleClass(String name) {
		this.name = name;
		levels = new SortedList<>(new Level.LevelComparator());
		prefix = suffix = "";
	}

	private RoleClass(String name, String prefix) {
		this.name = name;
		levels = new SortedList<>(new Level.LevelComparator());
		this.prefix = prefix.trim();
		suffix = "";
	}


	private RoleClass(String name, String prefix, String suffix) {
		this.name = name;
		levels = new SortedList<>(new Level.LevelComparator());
		this.prefix = prefix.trim();
		this.suffix = suffix.trim();
	}


	private RoleClass(String name, float strength, float dexterity, float resistance, float block, float dodge) {
		this.name = name;
		levels = new SortedList<>(new Level.LevelComparator());
		this.levels.add(new Level(0, strength, dexterity, resistance, block, dodge));
		prefix = suffix = "";
	}

	private RoleClass(String name, String prefix, float strength, float dexterity, float resistance, float block,
			float dodge) {
		this.name = name;
		levels = new SortedList<>(new Level.LevelComparator());
		this.levels.add(new Level(0, strength, dexterity, resistance, block, dodge));
		this.prefix = prefix.trim();
		suffix = "";
	}

	private RoleClass(String name, String prefix, String suffix, float strength, float dexterity, float resistance,
			float block, float dodge) {
		this.name = name;
		levels = new SortedList<>(new Level.LevelComparator());
		this.levels.add(new Level(0, strength, dexterity, resistance, block, dodge));
		this.prefix = prefix.trim();
		this.suffix = suffix.trim();
	}
	
	public void addLevel(Level lvl) {
		levels.add(lvl);
	}
	
	public void addLevels(Level[] lvls) {
		for (Level level : lvls) {
			addLevel(level);
		}
	}
	
	public SortedList<Level> getLevels() {
		return levels;
	}
	
	public void setLevels(SortedList<Level> levels) {
		this.levels = levels;
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
