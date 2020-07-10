package obj;

public class Stats {

	private String className, preffix, suffix;
	private float strength, dexterity, resistance, block, dodge;

	public Stats() {
		className = preffix = suffix = "";
		strength = dexterity = resistance = block = dodge = 0;
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
	public Stats(String className, String preffix, String suffix, float strength, float dexterity, float resistance,
			float block, float dodge) {
		this.className = className;
		this.preffix = preffix;
		this.suffix = suffix;
		this.strength = strength;
		this.dexterity = dexterity;
		this.resistance = resistance;
		this.block = block;
		this.dodge = dodge;
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
	public Stats(RoleClass base, float strength, float dexterity, float resistance, float block, float dodge) {
		className = base.getName();
		preffix = base.getPrefix();
		suffix = base.getSuffix();
		this.strength = strength;
		this.dexterity = dexterity;
		this.resistance = resistance;
		this.block = block;
		this.dodge = dodge;
	}

	/**
	 * 
	 * @param strength   Extra damage to physical attacks
	 * @param dexterity  Extra damage to arrows
	 * @param resistance % reduced damage
	 * @param block      % chance to reduce a physical attack's damage to 0
	 * @param            dodge% chance to reduce a arrow's damage to 0
	 */
	public Stats(float strength, float dexterity, float resistance, float block, float dodge) {
		this.strength = strength;
		this.dexterity = dexterity;
		this.resistance = resistance;
		this.block = block;
		this.dodge = dodge;
	}

	public Stats(Stats copy) {
		this.strength = copy.strength;
		this.dexterity = copy.dexterity;
		this.resistance = copy.resistance;
		this.block = copy.block;
		this.dodge = copy.dodge;
	}

	public Stats(RoleClass base) {
		this(base.getStats());
		className = base.getName();
		preffix = base.getPrefix();
		suffix = base.getSuffix();
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

	public double getStrength() {
		return strength;
	}

	public void setStrength(float strength) {
		this.strength = strength;
	}

	public double getDexterity() {
		return dexterity;
	}

	public void setDexterity(float dexerity) {
		this.dexterity = dexerity;
	}

	public float getResistance() {
		return resistance;
	}

	/**
	 * @param resistance Must be a number bewteen 0 and 1, both inclusives
	 */
	public void setResistance(float resistance) {
		if (resistance < 0 || resistance > 1)
			throw new IllegalArgumentException();
		this.resistance = resistance;
	}

	public float getBlock() {
		return block;
	}

	/**
	 * @param block Must be a number bewteen 0 and 1, both inclusives
	 */
	public void setBlock(float block) {
		if (block < 0 || block > 1)
			throw new IllegalArgumentException();
		this.block = block;
	}

	public float getDodge() {
		return dodge;
	}

	/**
	 * 
	 * @param dodge Must be a number bewteen 0 and 1, both inclusives
	 */
	public void setDodge(float dodge) {
		if (dodge < 0 || dodge > 1)
			throw new IllegalArgumentException();
		this.dodge = dodge;
	}
}
