package obj;

import org.bukkit.ChatColor;

import main.CrisPlay;

public enum RoleClass {
	BEGGAR("Beggar", "You aren't much at the start but you will level up easier.",
			"You gain experience while walking.", CrisPlay.mainColor + "[Beggar %lvl]" + ChatColor.RESET),
	WARRIOR("Warrior", "Hitting things is your way to go.",
			"You gain experience when you hit mobs or players.", CrisPlay.mainColor + "[Warrior %lvl]" + ChatColor.RESET),
	TANK("Tank", "You will die fighting or fight without dying at all.",
			"You gain experience when you get hit.", CrisPlay.mainColor + "[Tank %lvl]" + ChatColor.RESET),
	ARCHER("Archer", "I hope you have a bow in your pocket, or tons of snowballs.",
			"You gain experience when you hit mobs or players using a bow or throwing snowballs, eggs, etc.", CrisPlay.mainColor + "[Archer %lvl]" + ChatColor.RESET),
	FISHERMAN("Fisherman", "It's not the most interesting job, but you enjoy it.",
			"You gain experience when you fish.", CrisPlay.mainColor + "[Fisherman %lvl]" + ChatColor.RESET),
	RIDER("Rider", "Walking is for duumies.",
			"You gain experience while moving on minecarts, boats, horses, etc.", CrisPlay.mainColor + "[Rider %lvl]" + ChatColor.RESET),
	;
	static {
		BEGGAR.setLevels(new Level[] {
				new Level(0, 0, 0, 0, 0, 0),
				new Level(100, 0, 0, 0, 0, 0),
				new Level(200, 0, 0, 0, 0, 0),
		}, new Level(100, 1, 1, .001f, .001f, .001f));
		WARRIOR.setLevels(new Level[] {
				new Level(0, 2, 0, 0f, .1f, 0),
				new Level(100, 4, 0, 0f, .1f, 0),
				new Level(200, 6, 0, .1f, .1f, 0),
		}, new Level(100, 0, 0, 0, 0, 0));
		TANK.setLevels(new Level[] {
				new Level(0, -2, -2, .4f, .4f, .4f),
		}, new Level(100, .2f, .2f, .01f, .005f, .005f));
		ARCHER.setLevels(new Level[] {
				new Level(0, 0, 2, .1f, 0, .1f),
				new Level(100, 0, 4, .1f, 0, .1f),
				new Level(200, 0, 6, .1f, 0, .1f),
		}, new Level(100, 0, 0, 0, 0, 0));
		// strength, dexterity, resistance, block, dodge
		FISHERMAN.setLevels(new Level[] {
				new Level(0, 1, 1, 0, 0, 0f),
				new Level(80, 2, 2, 0, 0, 0f),
				new Level(160, 3, 3, 0, 0, 0f),
		}, new Level(100, 0, 0, 0, 0, 0));
		RIDER.setLevels(new Level[] {
				new Level(0, 0, 1, 0, 0, .3f),
				new Level(80, 1, 2, 0, 0, .4f),
				new Level(160, 2, 3, 0, 0, .5f),
		}, new Level(100, 0, 0, 0, 0, 0));
		
	}

	private Levels levels;
	private String name, desc, info, prefix = "", suffix = "";

	private RoleClass(String name, String desc, String info) {
		this.name = name;
		this.desc = desc;
		this.info = info;
	}

	private RoleClass(String name, String desc, String info, String prefix) {
		this.name = name;
		this.desc = desc;
		this.info = info;
		this.prefix = prefix;
	}


	private RoleClass(String name, String desc, String info, String prefix, String suffix) {
		this.name = name;
		this.desc = desc;
		this.info = info;
		this.prefix = prefix;
		this.suffix = suffix;
	}
	
	private void setLevels(Level[] lvls, Level increase) {
		levels = new Levels(increase);
		for (Level level : lvls) {
			levels.add(level);
		}
	}
	
	public Levels getLevels() {
		return levels;
	}

	public String getName() {
		return name;
	}
	
	public String getDesc() {
		return desc;
	}

	public String getInfo() {
		return info;
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
