package obj;

import java.util.Comparator;

public class Level implements Comparable<Level> {
	private int requiredExp;
	private float strength, dexterity, resistance, block, dodge;

	public Level(int requiredExp, float strength, float dexterity, float resistance, float block, float dodge) {
		this.requiredExp = requiredExp;
		this.strength = strength;
		this.dexterity = dexterity;
		this.resistance = resistance;
		this.block = block;
		this.dodge = dodge;
	}

	public int getRequiredExp() {
		return requiredExp;
	}

	public void setRequiredExp(int requiredExp) {
		this.requiredExp = requiredExp;
	}

	public float getStrength() {
		return strength;
	}

	public void setStrength(float strength) {
		this.strength = strength;
	}

	public float getDexterity() {
		return dexterity;
	}

	public void setDexterity(float dexterity) {
		this.dexterity = dexterity;
	}

	public float getResistance() {
		return resistance;
	}

	public void setResistance(float resistance) {
		this.resistance = resistance;
	}

	public float getBlock() {
		return block;
	}

	public void setBlock(float block) {
		this.block = block;
	}

	public float getDodge() {
		return dodge;
	}

	public void setDodge(float dodge) {
		this.dodge = dodge;
	}

	@Override
	public int compareTo(Level o) {
		return requiredExp > o.requiredExp ? 1 : requiredExp < o.requiredExp ? -1 : 0;
	}

	public static class LevelComparator implements Comparator<Level> {
		@Override
		public int compare(Level o1, Level o2) {
			return o1.requiredExp > o2.requiredExp ? 1 : o1.requiredExp < o2.requiredExp ? -1 : 0;
		}

	}
}
