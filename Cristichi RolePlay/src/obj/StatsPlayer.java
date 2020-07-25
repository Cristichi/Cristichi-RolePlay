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

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import main.CrisPlay;

public class StatsPlayer {
	private static HashMap<UUID, Stats> players = new HashMap<>();
	public static HashMap<UUID, Date> lastChanges = new HashMap<>();
	public static final long changeClassCD = 100000L;
	
	/**
	 * Gets the player stats, or null if he has no permission or doesn't have any stats associated yet
	 * @param p Player
	 * @return Stats, or null
	 */
	@Nullable
	public static Stats getPlayerStats(Player p) {
		if (!p.hasPermission(CrisPlay.PERMISSION_USE)) {
			return null;
		}
		return players.get(p.getUniqueId());
	}
	
	/**
	 * Puts stats into a certain player
	 * @param p Player
	 * @param stats
	 * @return true if player can have stats, false if stats didn't change because player has no permission
	 */
	public static boolean putPlayerStats(Player p, Stats stats) {
		if (!p.hasPermission(CrisPlay.PERMISSION_USE)) {
			return false;
		}
		players.put(p.getUniqueId(), stats);
		return true;
	}
	
	/**
	 * Checks if player has stats
	 * @param p Player
	 * @return true if player has permission to have stats and have them, false otherwise
	 */
	public static boolean playerHasStats(Player p) {
		if (!p.hasPermission(CrisPlay.PERMISSION_USE)) {
			return false;
		}
		return players.containsKey(p.getUniqueId());
	}
	
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
							loopasswitch: switch (dataName) {
							case "class name":
								for (RoleClass rc : RoleClass.values()) {
									if (rc.getName().equalsIgnoreCase(data)) {
										stats = new Stats(rc);
										break loopasswitch;
									}
								}
								throw new NullPointerException("Class \""+dataName+"\" not recognized (Maybe it was removed or name changed?)");
							case "exp":
							case "experience":
								stats.setExp(Integer.parseInt(data));
								break;

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
