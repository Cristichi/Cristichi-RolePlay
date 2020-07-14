package obj;

import exceptions.LevelAlgorithmException;
import util.SortedList;

public class Levels extends SortedList<Level>{
	private static final long serialVersionUID = 4790254109865267279L;
	
	/**
	 * Defines how much does each stat increase for each level above the last specified one
	 */
	private Level increase;

	public Levels(Level increase) {
		super(Level.Comparator);
		this.increase = increase;
	}
	
	public Level getIncrease() {
		return increase;
	}
	
	public void setIncrease(Level increase) {
		this.increase = increase;
	}

	public Level getLevel(int exp) {
		try {
			int i = 0;
			Level lvl = null;
			for(; true; i++) {
				if (i < size()) {
					lvl = new Level(get(i));
				} else if (lvl != null) {
					lvl = lvl.add(increase);
				} else {
					lvl = new Level(increase);
				}
				if (exp < lvl.getRequiredExp()) {
					return lvl;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		throw new LevelAlgorithmException();
	}

	public int getNumLevel(int exp) {
		try {
			int i = 0;
			Level lvl = null;
			for(; true; i++) {
				if (i < size()) {
					lvl = new Level(get(i));
				} else if (lvl != null) {
					lvl = lvl.add(increase);
				} else {
					lvl = new Level(increase);
				}
				if (exp < lvl.getRequiredExp()) {
					return i;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		throw new LevelAlgorithmException();
	}

//	public int getExpForNextLevel(int exp) {
//		try {
//			int i = 0;
//			Level lvl = null;
//			for(; true; i++) {
//				if (i < size()) {
//					lvl = new Level(get(i));
//				} else if (lvl != null) {
//					lvl = lvl.add(increase);
//				} else {
//					lvl = new Level(increase);
//				}
//				if (exp < lvl.getRequiredExp()) {
//					return lvl.getRequiredExp() - exp;
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		throw new LevelAlgorithmException();
//	}
}
