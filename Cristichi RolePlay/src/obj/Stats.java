package obj;

public class Stats {

	private String className, prefix, suffix;
	private Levels levels;
	private int exp;

//	public Stats() {
//		className  = "(No Class)";
//		prefix = suffix = "";
//		levels = new Levels(new Level(0,0,0,0,0,0));
//		exp = 0;
//	}

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
	public Stats(String className, String preffix, String suffix, Levels levels) {
		this.className = className;
		this.prefix = preffix;
		this.suffix = suffix;
		this.levels = levels;
		exp = 0;
	}

	public Stats(RoleClass base, Levels levels) {
		className = base.getName();
		prefix = base.getPrefix();
		suffix = base.getSuffix();
		this.levels = levels;
		exp = 0;
	}

	public Stats(Levels levels) {
		this.levels = levels;
		exp = 0;
	}

	public Stats(Stats copy) {
		className = copy.className;
		prefix = copy.prefix;
		suffix = copy.suffix;
		levels = copy.levels;
		exp = copy.exp;
	}

	public Stats(RoleClass base) {
		this(base.getLevels());
		className = base.getName();
		prefix = base.getPrefix();
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

	public String getPrefix() {
		return prefix.replace("%lvl", ""+levels.getNumLevel(exp));
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
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
	
	public Level getCurrentLevel() {
		return levels.getLevel(exp);
	}

//	public double getStrength() {
//		return levels.getLevel(exp).getStrength();
//	}
//
//	public double getDexterity() {
//		return levels.getLevel(exp).getDexterity();
//	}
//
//	public float getResistance() {
//		return levels.getLevel(exp).getResistance();
//	}
//
//	public float getBlock() {
//		return levels.getLevel(exp).getBlock();
//	}
//
//	public float getDodge() {
//		return levels.getLevel(exp).getDodge();
//	}

	public int getNextLevelTotalExp() {
		return levels.getExpForNextLevel(exp) + exp;
	}

	public int getExpForNextLevel() {
		return levels.getExpForNextLevel(exp);
	}

	@Override
	public String toString() {
		return "Stats [className=" + className + ", preffix=" + prefix + ", suffix=" + suffix + ", levels=" + levels
				+ ", exp=" + exp + "]";
	}

	public int getNumLevel() {
		return levels.getNumLevel(exp);
	}
}
