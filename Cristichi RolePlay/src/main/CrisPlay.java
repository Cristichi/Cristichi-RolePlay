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
		if (StatsPlayer.players.containsKey(e.getPlayer().getUniqueId())) {
			e.getPlayer().performCommand("rp stats");
		} else {
			e.getPlayer().sendMessage(header+"¿Did you know? You can increase your power choosing a Class. Use /rp choose (Class Name)");
		}
	}

	@EventHandler
	private void onChat(AsyncPlayerChatEvent e) {
		if (StatsPlayer.players.containsKey(e.getPlayer().getUniqueId())) {
			Stats stats = StatsPlayer.players.get(e.getPlayer().getUniqueId());
			e.setFormat(stats.getPreffix() + "%s" + stats.getSuffix() + ": %s");
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
			sender.sendMessage(accentColor + "  /"+label+" help"+textColor+": " + "Shows this helping message.");
			sender.sendMessage(accentColor + "  /"+label+" stats"+textColor+": " + "Shows your class, your experience and your stats.");
			sender.sendMessage(accentColor + "  /"+label+" classes"+textColor+": " + "Lists all classes and a short description for each one of them.");
			sender.sendMessage(accentColor + "  /"+label+" info (Class Name)" + textColor+": " + "Shows you information about the different levels and how to gain experience if that class.");
			sender.sendMessage(accentColor + "  /"+label+" choose (Class Name)" + textColor+": " + "Changes your roleplay class, but resets your experience.");
			break;
		case "classes":
			sender.sendMessage(header + "Classes:");
			for (RoleClass rc : RoleClass.values()) {
				sender.sendMessage(accentColor + "  "+rc.getName()+textColor+": " + rc.getDesc());
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
				} else if (args.length == 1 && StatsPlayer.players.containsKey(p.getUniqueId())){
					String playerClass = StatsPlayer.players.get(p.getUniqueId()).getClassName();
					for (RoleClass roleClass : RoleClass.values()) {
						if (roleClass.getName().equalsIgnoreCase(playerClass)) {
							rc = roleClass;
							break;
						}
					}
				}
				if (rc != null) {
					sender.sendMessage(new String [] {
							header + rc.getName()+":",
							textColor + " "+rc.getInfo()
					});
					SortedList<Level> lvls = rc.getLevels();
					for (int i = 0; i < lvls.size(); i++) {
						Level lvl = lvls.get(i);
						sender.sendMessage(new String[] {
							textColor + "  Level " + accentColor + (i+1) + textColor + ": ",
							textColor + "   Required Exp: "  + accentColor + lvl.getRequiredExp() + "" +
							textColor + "   Strength: " + accentColor + lvl.getStrength() + "" +
							textColor + "    Dexterity: " + accentColor + lvl.getDexterity(),
							textColor + "   Resistance: " + accentColor + lvl.getResistance() + "" +
							textColor + "      Block: " + accentColor + lvl.getBlock() + "" +
							textColor + "      Dodge: " + accentColor + lvl.getBlock()
						});
					}
				} else {
					sender.sendMessage(header + "You must specify which class if you don't have any.");
				}
			} else {
				sender.sendMessage(header + "Only players can have stats.");
			}
			break;
		case "choose":
			if (sender instanceof Player) {
				if (args.length >= 2) {
					Player p = (Player) sender;
					boolean change = true;
					boolean hadClass = StatsPlayer.players.containsKey(p.getUniqueId()); 
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
									Stats rc = StatsPlayer.players.get(p.getUniqueId());
									if (rc.getClassName().equals(roleClass.getName())) {
										sender.sendMessage(header+"Your class is already "+rc.getClassName()+".");
										break;
									}
									if (rc.getExp()>0) {
										if (args.length<3 || !args[2].equals("confirm")) {
											sender.sendMessage(header+"You will lose your current experience ("+rc.getExp()+"), type \"/"+label+" "+args[0]+" "+args[1]+" confirm\" to change your class.");
											break;
										}
									}
								}
								
								StatsPlayer.players.put(p.getUniqueId(), new Stats(roleClass));
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
			break;
		case "stats":
			if (sender instanceof Player) {
				Player p = (Player) sender;
				Stats stats = StatsPlayer.players.get(p.getUniqueId());
				if (stats == null) {
					sender.sendMessage(header+"You have to choose a class first. You can use /"+label+" choose (Class Name)");
				}else {
					sender.sendMessage(header + "Your stats:");
					sender.sendMessage(accentColor + " Class"+textColor+": " + stats.getClassName());
					sender.sendMessage(accentColor + " Experience"+textColor+": " + stats.getExp()+textColor + accentColor + "/" + textColor + stats.getNextLevelTotalExp());
					sender.sendMessage(accentColor + " Strength"+textColor+": " + stats.getStrength());
					sender.sendMessage(accentColor + " Dexterity"+textColor+": " + stats.getDexterity());
					sender.sendMessage(
							accentColor + " Resistance"+textColor+": " + (int) (stats.getResistance() * 100) + accentColor + "%");
					sender.sendMessage(accentColor + " Block"+textColor+": " + (int) (stats.getBlock() * 100) + accentColor + "%");
					sender.sendMessage(accentColor + " Dodge"+textColor+": " + (int) (stats.getDodge() * 100) + accentColor + "%");
				}
			} else
				sender.sendMessage(header + "Only players can have stats.");
			break;

//		case "set":
//		case "add":
//			if (sender instanceof Player) {
//				if (sender.hasPermission("crisrp.admin")) {
//					try {
//						if (args.length >= 2) {
//							if (args.length >= 3) {
//								String elegido = args[1].toLowerCase();
//								Player p = (Player) sender;
//								float valorNuevo = Float.parseFloat(args[2]);
//								Stats clase = StatsPlayer.players.getOrDefault(p.getUniqueId(), new Stats());
//								switch (elegido) {
//								case "str":
//								case "dmg":
//								case "damage":
//								case "power":
//								case "strength":
//									clase.setStrength(
//											(int) (valorNuevo + (args[0].equals("add") ? clase.getStrength() : 0)));
//									sender.sendMessage(header + "New Strength: " + accentColor + clase.getStrength());
//									break;
//								case "dex":
//								case "dexterity":
//									clase.setDexterity(
//											(int) (valorNuevo + (args[0].equals("add") ? clase.getDexterity() : 0)));
//									sender.sendMessage(header + "New Dexterity: " + accentColor + clase.getDexterity());
//									break;
//								case "res":
//								case "resist":
//								case "resistance":
//									clase.setResistance(
//											valorNuevo + (args[0].equals("add") ? clase.getResistance() : 0));
//									sender.sendMessage(
//											header + "New Resistance: " + accentColor + clase.getResistance());
//									break;
//								case "blc":
//								case "blo":
//								case "block":
//									clase.setBlock(valorNuevo + (args[0].equals("add") ? clase.getBlock() : 0));
//									sender.sendMessage(header + "New Block: " + accentColor + clase.getBlock());
//									break;
//								case "dod":
//								case "dodge":
//									clase.setDodge(valorNuevo + (args[0].equals("add") ? clase.getDodge() : 0));
//									sender.sendMessage(header + "new Dodge: " + accentColor + clase.getDodge());
//									break;
//
//								default:
//									sender.sendMessage(
//											header + errorColor + "You must specify one of the available stats.");
//									break;
//								}
//								StatsPlayer.players.put(p.getUniqueId(), clase);
//								StatsPlayer.saveAllPlayersStats(ARCHIVO_JUGADORES);
//							} else {
//								sender.sendMessage(header + errorColor + "You must specify how much.");
//							}
//						} else {
//							sender.sendMessage(header + errorColor + "You must specify a stat.");
//						}
//					} catch (NumberFormatException e) {
//						sender.sendMessage(header + errorColor + "The quantity must be a number.");
//					} catch (IllegalArgumentException e) {
//						sender.sendMessage(
//								header + errorColor + "That stat must be a number between 0 y 1 (both included).");
//					}
//				} else {
//					sender.sendMessage(header+"You don't have permission to do that.");
//				}
//			} else
//				sender.sendMessage(header + "Only players can have stats.");
//			break;

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
//			if (sender.hasPermission("crisrp.admin")) {
//				sol.add("add");
//				sol.add("set");
//			}
			break;

		case 2:
			switch (args[0]) {
			case "choose":
			case "info":
				for (RoleClass roleClass : RoleClass.values()) {
					sol.add(roleClass.getName());
				}
				break;
//			case "add":
//			case "set":
//				sol.add("strength");
//				sol.add("dexterity");
//				sol.add("resistance");
//				sol.add("block");
//				sol.add("dodge");
//				break;

			default:
				break;
			}
			break;

		case 3:
			switch (args[1]) {
			case "strength":
			case "dexterity":
				sol.add("0");
				sol.add("1");
				sol.add("2");
				sol.add("3");
				sol.add("4");
				sol.add("5");
				sol.add("6");
				sol.add("7");
				sol.add("8");
				break;

			case "resistance":
			case "block":
			case "dodge":
				sol.add("0");
				sol.add("0.1");
				sol.add("0.2");
				sol.add("0.3");
				sol.add("0.4");
				sol.add("0.5");
				sol.add("0.6");
				sol.add("0.7");
				sol.add("0.8");
				sol.add("0.9");
				sol.add("1");
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
