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
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import obj.RoleClass;
import obj.Stats;
import obj.StatsListener;
import obj.StatsPlayer;

public class CrisPlay extends JavaPlugin implements Listener {
	private PluginDescriptionFile desc = getDescription();

	public final ChatColor mainColor = ChatColor.BLUE;
	public final ChatColor textColor = ChatColor.AQUA;
	public final ChatColor accentColor = ChatColor.GOLD;
	public final ChatColor errorColor = ChatColor.DARK_RED;
	public final String header = mainColor + "[" + desc.getName() + "] " + textColor;

	private static final File ARCHIVO_JUGADORES = new File("plugins/Cris RolePlay/Jugadores.yml");

	@Override
	public void onEnable() {
		try {
			StatsPlayer.loadAllPlayersStats(ARCHIVO_JUGADORES);

			getServer().getPluginManager().registerEvents(new StatsListener(this), this);
			getServer().getPluginManager().registerEvents(this, this);
			getLogger().info("Enabled");

			StatsPlayer.saveAllPlayersStats(ARCHIVO_JUGADORES);
		} catch (FileSystemException e) {
			getServer().getPluginManager().disablePlugin(this);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onDisable() {
		StatsPlayer.saveAllPlayersStats(ARCHIVO_JUGADORES);
		getLogger().info("Disabled");
	}

	@EventHandler
	private void onJoin(PlayerJoinEvent e) {
//		if (isEnabled()) {
//			if (StatsPlayer.players.containsKey(e.getPlayer().getUniqueId())) {
//				Stats stats = StatsPlayer.players.get(e.getPlayer().getUniqueId());
//				e.getPlayer().setDisplayName(stats.getPreffix() + e.getPlayer().getName() + stats.getSuffix());
//			}
//		}
	}
	
	@EventHandler
	private void onChat(AsyncPlayerChatEvent e){
		if (StatsPlayer.players.containsKey(e.getPlayer().getUniqueId())) {
			Stats stats = StatsPlayer.players.get(e.getPlayer().getUniqueId());
			e.setFormat(stats.getPreffix() + "%s" + stats.getSuffix()+": %s");
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		label = label.toLowerCase();
		if (args.length == 0) {
			return false;
		}
		boolean bueno = true;
		switch (args[0]) {
		case "help":
			sender.sendMessage(header + "Sin ayuda :D");
			break;
		case "choose":
			if (sender instanceof Player) {
				if (args.length == 2) {
					Player p = (Player) sender;
					boolean change = true;
					if (StatsPlayer.players.containsKey(p.getUniqueId())) {
						double diff = StatsPlayer.millsUntilChoose(p.getUniqueId());
						if (diff > 0) {
							p.sendMessage(header+"You need to wait at least "+String.format(Locale.ENGLISH, "%.2f", diff/1000)+" seconds to change your class.");
							change = false;
						}
						//p.sendMessage(header+"diff = "+diff);
					}
					if (change) {
						boolean wrongClass = true;
						for (RoleClass roleClass : RoleClass.values()) {
							if (roleClass.getName().equalsIgnoreCase(args[1])) {
								StatsPlayer.players.put(p.getUniqueId(), new Stats(roleClass));
								StatsPlayer.lastChanges.put(p.getUniqueId(), Date.from(Instant.now()));
								//p.setDisplayName(roleClass.getPreffix() + p.getPlayerListName() + roleClass.getSuffix());
								wrongClass = false;
								p.sendMessage(header+"You are now a "+roleClass.getName()+".");
								break;
							}
						}
						if (wrongClass) {
							p.sendMessage(header+"Class \""+args[1]+"\" does not exist.");	
						}
					}
				} else {
					sender.sendMessage(
							header + "You have to change your class using: /" + label + " choose (Class Name)");
				}
			} else {
				sender.sendMessage(header + "Only players can have stats.");
			}
			break;
		case "stats":
			if (sender instanceof Player) {
				Player p = (Player) sender;
				Stats stats = StatsPlayer.players.getOrDefault(p.getUniqueId(), new Stats());
				sender.sendMessage(header + "Your stats:");
				sender.sendMessage(mainColor + " Class: " + accentColor + stats.getClassName());
				sender.sendMessage(mainColor + " Strength: " + accentColor + stats.getStrength());
				sender.sendMessage(mainColor + " Dexterity: " + accentColor + stats.getDexterity());
				sender.sendMessage(
						mainColor + " Resistance: " + accentColor + (int) (stats.getResistance() * 100) + "%");
				sender.sendMessage(mainColor + " Block: " + accentColor + (int) (stats.getBlock() * 100) + "%");
				sender.sendMessage(mainColor + " Dodge: " + accentColor + (int) (stats.getDodge() * 100) + "%");
			} else
				sender.sendMessage(header + "Only players can have stats.");
			break;

		case "set":
		case "add":
			if (sender instanceof Player) {
				try {
					if (args.length >= 2) {
						if (args.length >= 3) {
							String elegido = args[1].toLowerCase();
							Player p = (Player) sender;
							float valorNuevo = Float.parseFloat(args[2]);
							Stats clase = StatsPlayer.players.getOrDefault(p.getUniqueId(), new Stats());
							switch (elegido) {
							case "str":
							case "dmg":
							case "damage":
							case "power":
							case "strength":
								clase.setStrength(
										(int) (valorNuevo + (args[0].equals("add") ? clase.getStrength() : 0)));
								sender.sendMessage(header + "New Strength: " + accentColor + clase.getStrength());
								break;
							case "dex":
							case "dexterity":
								clase.setDexterity(
										(int) (valorNuevo + (args[0].equals("add") ? clase.getDexterity() : 0)));
								sender.sendMessage(header + "New Dexterity: " + accentColor + clase.getDexterity());
								break;
							case "res":
							case "resist":
							case "resistance":
								clase.setResistance(valorNuevo + (args[0].equals("add") ? clase.getResistance() : 0));
								sender.sendMessage(header + "New Resistance: " + accentColor + clase.getResistance());
								break;
							case "blc":
							case "blo":
							case "block":
								clase.setBlock(valorNuevo + (args[0].equals("add") ? clase.getBlock() : 0));
								sender.sendMessage(header + "New Block: " + accentColor + clase.getBlock());
								break;
							case "dod":
							case "dodge":
								clase.setDodge(valorNuevo + (args[0].equals("add") ? clase.getDodge() : 0));
								sender.sendMessage(header + "new Dodge: " + accentColor + clase.getDodge());
								break;

							default:
								sender.sendMessage(
										header + errorColor + "You must specify one of the available stats.");
								break;
							}
							StatsPlayer.players.put(p.getUniqueId(), clase);
							StatsPlayer.saveAllPlayersStats(ARCHIVO_JUGADORES);
						} else {
							sender.sendMessage(header + errorColor + "You must specify how much.");
						}
					} else {
						sender.sendMessage(header + errorColor + "You must specify a stat.");
					}
				} catch (NumberFormatException e) {
					sender.sendMessage(header + errorColor + "The quantity must be a number.");
				} catch (IllegalArgumentException e) {
					sender.sendMessage(
							header + errorColor + "That stat must be a number between 0 y 1 (both included).");
				}
			} else
				sender.sendMessage(header + "Only players can have stats.");
			break;

		default:
			bueno = false;
			break;
		}
		return bueno;
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
			sol.add("choose");
			sol.add("stats");
			sol.add("add");
			sol.add("set");
			break;

		case 2:
			switch (args[0]) {
			case "choose":
				for (RoleClass roleClass : RoleClass.values()) {
					sol.add(roleClass.getName());
				}
				break;
			case "add":
			case "set":
				sol.add("strength");
				sol.add("dexterity");
				sol.add("resistance");
				sol.add("block");
				sol.add("dodge");
				break;

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
			// sol = null;
			break;
		}
		return sol;
	}
}
