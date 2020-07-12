package obj;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;

public class StatsPlayer {
	public static HashMap<UUID, Stats> players = new HashMap<>();
	public static HashMap<UUID, Date> lastChanges = new HashMap<>();
	public static final long changeClassCD = 100000L;
	
	public static long millsUntilChoose(UUID id) {
		if (StatsPlayer.lastChanges.containsKey(id)) {
			long diff = Date.from(Instant.now()).getTime() - StatsPlayer.lastChanges.get(id).getTime();
			if (changeClassCD > diff) {
				return changeClassCD - diff;
			}
		}
		return 0;
	}

	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
	    long diffInMillies = date2.getTime() - date1.getTime();
	    return diffInMillies;
	    //return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}
	
	public static void saveAllPlayersStats(File file) {
		try {
			file.getParentFile().mkdirs();
			if (!file.exists()) {
				file.createNewFile();
			} else {
				file.delete();
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			
			String fileText = "";
			Set<UUID> keys = players.keySet();
			for (UUID key : keys) {
				Stats value = players.get(key);
				fileText += key.toString() + " ("+Bukkit.getOfflinePlayer(key).getName()+"): [";
				fileText += "\n  Class name: " + value.getClassName();
				fileText += "\n  Experience: " + value.getExp();
//				fileText += "\n  Class preffix: " + value.getPreffix();
//				fileText += "\n  Class suffix: " + value.getSuffix();
//				fileText += "\n  Strength: " + value.getStrength();
//				fileText += "\n  Dexirity: " + value.getDexterity();
//				fileText += "\n  Resistance: " + value.getResistance();
//				fileText += "\n  Block: " + value.getBlock();
//				fileText += "\n  Dodge: " + value.getDodge();
				fileText += "\n]\n";
			}

			bw.write(fileText);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void loadAllPlayersStats(File file) throws FileSystemException {
		String line = null;
		int lineCont = 0;
		try {
			file.getParentFile().mkdirs();
			if (!file.exists()) {
				return;
			} 
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);

			/*
				736ff9cb-e917-47cd-8038-0b77edf37935 (CristichiEX): [
				  Class name: Archer
				  Experience: 0
				]
			 */
			while ((line = br.readLine()) != null) {
				lineCont++;
				if (!line.startsWith("#") && !line.isEmpty()) {
					if (line.endsWith("[")) {
						StringTokenizer st = new StringTokenizer(line, "(");
						UUID uuid = UUID.fromString(st.nextToken().trim());

						Stats stats = null;
						for(int i=0; i<2; i++){
							lineCont++;
							line = br.readLine().trim();
							
							StringTokenizer lineST = new StringTokenizer(line, ":");
							String dataName = lineST.nextToken().trim().toLowerCase();
							String data = lineST.nextToken().trim();
							switch (dataName) {
							case "class name":
								for (RoleClass rc : RoleClass.values()) {
									if (rc.getName().equalsIgnoreCase(data)) {
										stats = new Stats(rc);
									}
								}
								break;
							case "exp":
							case "experience":
								stats.setExp(Integer.parseInt(data));
								break;
//							case "class preffix":
//								stats.setPreffix(st.nextToken());
//								break;
//							case "class suffix":
//								stats.setSuffix(st.nextToken());
//								break;
//							case "strength":
//								stats.setStrength(Float.parseFloat(st.nextToken().trim()));
//								break;
//							case "dexirity":
//								stats.setDexterity(Float.parseFloat(st.nextToken().trim()));
//								break;
//							case "resistance":
//								stats.setResistance(Float.parseFloat(st.nextToken().trim()));
//								break;
//							case "block":
//								stats.setBlock(Float.parseFloat(st.nextToken().trim()));
//								break;
//							case "dodge":
//								stats.setDodge(Float.parseFloat(st.nextToken().trim()));
//								break;

							default:
								throw new NullPointerException("stat \""+dataName+"\" not recognized");
							}
						}
						players.put(uuid, stats);
					}
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new FileSystemException(file.getName()+" could not be parsed"+(lineCont>0?" (line "+lineCont+": \""+line+"\")":""));
		}
	}
}
