package obj;

import util.SortedList;

public class Stats {

	private String className, preffix, suffix;
	private SortedList<Level> levels;
	private int exp;

	public Stats() {
		className  = "(No Class)";
		preffix = suffix = "";
		levels = new SortedList<>(new Level.LevelComparator());
		exp = 0;
	}

	/**
	 * 
	 * @param className
	 * @param preffix
	 * @param suffix
	 * @param strength
	 * @param dexterity
	 * @param resistance
	 * @param block
	 * @param dodge
	 */
	public Stats(String className, String preffix, String suffix, SortedList<Level> levels) {
		this.className = className;
		this.preffix = preffix;
		this.suffix = suffix;
		this.levels = levels;
		exp = 0;
	}

	/**
	 * 
	 * @param base       The class this Stats evolved from (just for naming reasons)
	 * @param strength   Extra damage to physical attacks
	 * @param dexterity  Extra damage to arrows
	 * @param resistance % reduced damage
	 * @param block      % chance to reduce a physical attack's damage to 0
	 * @param            dodge% chance to reduce a arrow's damage to 0
	 */
	public Stats(RoleClass base, SortedList<Level> levels) {
		className = base.getName();
		preffix = base.getPrefix();
		suffix = base.getSuffix();
		this.levels = levels;
		exp = 0;
	}

	/**
	 * 
	 * @param strength   Extra damage to physical attacks
	 * @param dexterity  Extra damage to arrows
	 * @param resistance % reduced damage
	 * @param block      % chance to reduce a physical attack's damage to 0
	 * @param            dodge% chance to reduce a arrow's damage to 0
	 */
	public Stats(SortedList<Level> levels) {
		this.levels = levels;
		exp = 0;
	}

	public Stats(Stats copy) {
		className = copy.className;
		preffix = copy.preffix;
		suffix = copy.suffix;
		levels = copy.levels;
		exp = copy.exp;
	}

	public Stats(RoleClass base) {
		this(base.getLevels());
		className = base.getName();
		preffix = base.getPrefix();
		suffix = base.getSuffix();
		levels = base.getLevels();
		exp = 0;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getPreffix() {
		return preffix;
	}

	public void setPreffix(String preffix) {
		this.preffix = preffix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
	public int getExp() {
		return exp;
	}
	
	public void setExp(int exp) {
		this.exp = exp;
	}

	public void increaseExp(int increase) {
		exp+=increase;
	}

	public double getStrength() {
		return SortedList.getLevel(levels, exp).getStrength();
	}

	public double getDexterity() {
		return SortedList.getLevel(levels, exp).getDexterity();
	}

	public float getResistance() {
		return SortedList.getLevel(levels, exp).getResistance();
	}

	public float getBlock() {
		return SortedList.getLevel(levels, exp).getBlock();
	}

	public float getDodge() {
		return SortedList.getLevel(levels, exp).getDodge();
	}
}
