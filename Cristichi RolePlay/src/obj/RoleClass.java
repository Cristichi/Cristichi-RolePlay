package obj;

import main.CrisPlay;
import util.SortedList;
import org.bukkit.ChatColor;

public enum RoleClass {
	BEGGAR("Beggar", "You aren't much at the start but you will level up easier", CrisPlay.mainColor + "[Beggar]" + ChatColor.RESET),
	WARRIOR("Warrior", "Hitting things is your way to go.", CrisPlay.mainColor + "[Warrior]" + ChatColor.RESET),
	TANK("Tank", "You will die fighting or fight without dying at all.", CrisPlay.mainColor + "[Tank]" + ChatColor.RESET),
	ARCHER("Archer", "I hope you have a bow in your pocket, or tons of snowballs.", CrisPlay.mainColor + "[Archer]" + ChatColor.RESET),
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
				new Level(0, -2, -2, .4f, .4f, .4f),
				new Level(100, -1, -1, .5f, .4f, .4f),
				new Level(200, 0, 0, .6f, .4f, .4f),
		});
		ARCHER.addLevels(new Level[] {
				new Level(0, 0, 2, .1f, 0, .1f),
				new Level(100, 0, 4, .1f, 0, .1f),
				new Level(200, 0, 6, .1f, 0, .1f),
		});
		
	}

	private SortedList<Level> levels = new SortedList<>(new Level.LevelComparator());
	private String name, desc, prefix = "", suffix = "";

	private RoleClass(String name, String desc) {
		this.name = name;
		this.desc = desc;
	}

	private RoleClass(String name, String desc, String prefix) {
		this.name = name;
		this.desc = desc;
		this.prefix = prefix;
	}


	private RoleClass(String name, String desc, String prefix, String suffix) {
		this.name = name;
		this.desc = desc;
		this.prefix = prefix;
		this.suffix = suffix;
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
	
	private void addLevel(Level lvl) {
		levels.add(lvl);
	}
	
	private void addLevels(Level[] lvls) {
		for (Level level : lvls) {
			addLevel(level);
		}
	}
	
	public SortedList<Level> getLevels() {
		return levels;
	}

	public String getName() {
		return name;
	}
	
	public String getDesc() {
		return desc;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	@Override
	public String toString() {
		return name+" ("+desc+")";
	}
}
