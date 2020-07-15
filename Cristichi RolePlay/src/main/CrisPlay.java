package main;

import java.io.File;
import java.nio.file.FileSystemException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import obj.ExpListener;
import obj.Level;
import obj.RoleClass;
import obj.Stats;
import obj.StatsListener;
import obj.StatsPlayer;
import util.SortedList;

public class CrisPlay extends JavaPlugin implements Listener {
	private PluginDescriptionFile desc = getDescription();

	public static final ChatColor mainColor = ChatColor.LIGHT_PURPLE;
	public static final ChatColor textColor = ChatColor.AQUA;
	public static final ChatColor accentColor = ChatColor.GOLD;
	public static final ChatColor errorColor = ChatColor.DARK_RED;
	public final String header = mainColor + "[" + desc.getName() + "] " + textColor;

	private final String msgNoPermission = header + errorColor + "You don't have permission to use that.";
	
	public static final String PERMISSION_USE = "crisrp.use";

	private static final File PLAYER_STATS_FILE = new File("plugins/Cris RolePlay/Player Stats.yml");

	@Override
	public void onEnable() {
		try {
			StatsPlayer.loadAllPlayersStats(PLAYER_STATS_FILE);

			getServer().getPluginManager().registerEvents(new ExpListener(this), this);
			getServer().getPluginManager().registerEvents(new StatsListener(this), this);
			getServer().getPluginManager().registerEvents(this, this);
			getLogger().info("Enabled");

			StatsPlayer.saveAllPlayersStats(PLAYER_STATS_FILE);
		} catch (FileSystemException e) {
			getServer().getPluginManager().disablePlugin(this);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onDisable() {
		StatsPlayer.saveAllPlayersStats(PLAYER_STATS_FILE);
		getLogger().info("Disabled");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	private void onLogin(PlayerJoinEvent e) {
		if (StatsPlayer.playerHasStats(e.getPlayer())){
			e.getPlayer().performCommand("rp stats");
		} else {
			e.getPlayer().sendMessage(header
					+ "¿Did you know? You can increase your power choosing a Class. Use /rp choose (Class Name)");
		}
	}

	@EventHandler
	private void onChat(AsyncPlayerChatEvent e) {
		if (StatsPlayer.playerHasStats(e.getPlayer())){
			Stats stats = StatsPlayer.getPlayerStats(e.getPlayer());
			e.setFormat(stats.getPrefix() + "%s" + stats.getSuffix() + ": %s");
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		label = label.toLowerCase();
		if (args.length == 0) {
			return false;
		}
		boolean correct = true;
		switch (args[0]) {
		case "help":
			sender.sendMessage(header + "Commands:");
			sender.sendMessage(
					accentColor + "  /" + label + " help" + textColor + ": " + "Shows this helping message.");
			sender.sendMessage(accentColor + "  /" + label + " stats" + textColor + ": "
					+ "Shows your class, your experience and your stats.");
			sender.sendMessage(accentColor + "  /" + label + " classes" + textColor + ": "
					+ "Lists all classes and a short description for each one of them.");
			sender.sendMessage(accentColor + "  /" + label + " info (Class Name)" + textColor + ": "
					+ "Shows you information about the different levels and how to gain experience if that class.");
			sender.sendMessage(accentColor + "  /" + label + " choose (Class Name)" + textColor + ": "
					+ "Changes your roleplay class, but resets your experience.");
			break;
		case "classes":
			sender.sendMessage(header + "Classes:");
			for (RoleClass rc : RoleClass.values()) {
				sender.sendMessage(accentColor + "  " + rc.getName() + textColor + ": " + rc.getDesc());
			}
			break;
		case "info":
			if (sender instanceof Player) {
				Player p = (Player) sender;
				RoleClass rc = null;
				if (args.length >= 2) {
					for (RoleClass roleClass : RoleClass.values()) {
						if (roleClass.getName().equalsIgnoreCase(args[1])) {
							rc = roleClass;
							break;
						}
					}
				} else if (args.length == 1 && StatsPlayer.playerHasStats(p)) {
					String playerClass = StatsPlayer.getPlayerStats(p).getClassName();
					for (RoleClass roleClass : RoleClass.values()) {
						if (roleClass.getName().equalsIgnoreCase(playerClass)) {
							rc = roleClass;
							break;
						}
					}
				}
				if (rc != null) {
					sender.sendMessage(new String[] { header + rc.getName() + ":", textColor + " " + rc.getInfo() });
					SortedList<Level> lvls = rc.getLevels();
					for (int i = 0; i < lvls.size(); i++) {
						Level lvl = lvls.get(i);
						sender.sendMessage(
								new String[] { textColor + "  Level " + accentColor + (i + 1) + textColor + ": ",
										textColor + "   Required Exp: " + accentColor + lvl.getRequiredExp() + ""
												+ textColor + "   Strength: " + accentColor + lvl.getStrength() + ""
												+ textColor + "    Dexterity: " + accentColor + lvl.getDexterity(),
										textColor + "   Resistance: " + accentColor + lvl.getResistance() + ""
												+ textColor + "      Block: " + accentColor + lvl.getBlock() + ""
												+ textColor + "      Dodge: " + accentColor + lvl.getBlock() });
					}
				} else {
					sender.sendMessage(header + "You must specify which class if you don't have any.");
				}
			} else {
				sender.sendMessage(header + "Only players can have stats.");
			}
			break;
		case "choose":
			if (sender.hasPermission(PERMISSION_USE)) {
				if (sender instanceof Player) {
					if (args.length >= 2) {
						Player p = (Player) sender;
						boolean change = true;
						boolean hadClass = StatsPlayer.playerHasStats(p);
						if (hadClass) {
							double diff = StatsPlayer.millsUntilChoose(p.getUniqueId());
							if (diff > 0) {
								p.sendMessage(header + "You need to wait at least "
										+ String.format(Locale.ENGLISH, "%.2f", diff / 1000)
										+ " seconds to change your class.");
								change = false;
							}
						}
						if (change) {
							boolean wrongClass = true;
							for (RoleClass roleClass : RoleClass.values()) {
								if (roleClass.getName().equalsIgnoreCase(args[1])) {
									wrongClass = false;
									if (hadClass) {
										Stats rc = StatsPlayer.getPlayerStats(p);
										if (rc.getClassName().equals(roleClass.getName())) {
											sender.sendMessage(
													header + "Your class is already " + rc.getClassName() + ".");
											break;
										}
										if (rc.getExp() > 0) {
											if (args.length < 3 || !args[2].equals("confirm")) {
												sender.sendMessage(header + "You will lose your current experience ("
														+ rc.getExp() + "), type \"/" + label + " " + args[0] + " "
														+ args[1] + " confirm\" to change your class.");
												break;
											}
										}
									}

									StatsPlayer.putPlayerStats(p, new Stats(roleClass));
									StatsPlayer.lastChanges.put(p.getUniqueId(), Date.from(Instant.now()));
									wrongClass = false;
									p.sendMessage(header + "You are now a " + roleClass.getName() + ".");
									break;
								}
							}
							if (wrongClass) {
								p.sendMessage(header + "Class \"" + args[1] + "\" does not exist.");
							}
						}
					} else {
						sender.sendMessage(
								header + "You may change your class using: /" + label + " choose (Class Name)");
					}
				} else {
					sender.sendMessage(header + "Only players can have stats.");
				}
			} else {
				sender.sendMessage(msgNoPermission);
			}
			break;
		case "testeazorl":
			try {
				Stats stats = StatsPlayer.getPlayerStats((Player) sender);
				stats.setExp(Integer.parseInt(args[1]));
				StatsPlayer.putPlayerStats((Player) sender, stats);
			} catch (Exception e) {
				e.printStackTrace();
			}
		case "stats":
			if (sender instanceof Player) {
				Player p = (Player) sender;
				Stats stats = StatsPlayer.getPlayerStats(p);
				if (stats == null) {
					sender.sendMessage(header + "You have to choose a class first. You can use /" + label
							+ " choose (Class Name)");
				} else {
					Level lvl = stats.getCurrentLevel();
					sender.sendMessage(header + "Your stats:");
					sender.sendMessage(accentColor + " Class" + textColor + ": " + stats.getClassName() + accentColor
							+ " Level " + textColor + stats.getNumLevel());
					sender.sendMessage(accentColor + " Experience" + textColor + ": " + stats.getExp() + textColor
							+ accentColor + "/" + textColor + stats.getNextLevelTotalExp());
					sender.sendMessage(accentColor + " Strength" + textColor + ": " + lvl.getStrength());
					sender.sendMessage(accentColor + " Dexterity" + textColor + ": " + lvl.getDexterity());
					sender.sendMessage(accentColor + " Resistance" + textColor + ": "
							+ (int) (lvl.getResistance() * 100) + accentColor + "%");
					sender.sendMessage(accentColor + " Block" + textColor + ": " + (int) (lvl.getBlock() * 100)
							+ accentColor + "%");
					sender.sendMessage(accentColor + " Dodge" + textColor + ": " + (int) (lvl.getDodge() * 100)
							+ accentColor + "%");
				}
			} else
				sender.sendMessage(header + "Only players can have stats.");
			break;

		default:
			correct = false;
			break;
		}
		return correct;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (!isEnabled()) {
			return null;
		}
		List<String> sol = new ArrayList<>();
		switch (args.length) {
		case 1:
			sol.add("help");
			sol.add("info");
			sol.add("classes");
			sol.add("choose");
			sol.add("stats");
			break;

		case 2:
			switch (args[0]) {
			case "choose":
			case "info":
				for (RoleClass roleClass : RoleClass.values()) {
					sol.add(roleClass.getName());
				}
				break;

			default:
				break;
			}
			break;
		default:
			break;
		}
		return sol;
	}
}
