package obj;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.ThrowableProjectile;
import org.bukkit.entity.Trident;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.HorseJumpEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import main.CrisPlay;

public class ExpListener implements Listener {
	private static final String META_ARROW_SHOOTER = "crisrp_arrow_player";
	private static final String META_ARROW_FORCE = "crisrp_arrow_force";

	private CrisPlay plugin;

	public ExpListener(CrisPlay plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	private void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		Stats stats = StatsPlayer.getPlayerStats(p);
		if (stats != null && stats.getClassName().equals(RoleClass.BEGGAR.getName()) && Math.random() < 0.001) {
			p.sendMessage(plugin.header + "You gained 1 exp for walking!");
			if (stats.changeExp(1)) {
				p.sendMessage(plugin.header + "You are now " + stats.getClassName() + " " + stats.getNumLevel() + "!");
			}
			StatsPlayer.putPlayerStats(p, stats);
			p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);
		} else if (stats != null && stats.getClassName().equals(RoleClass.RIDER.getName()) && p.isInsideVehicle()) {
			double prob = 0.02 * e.getFrom().distance(e.getTo());
			if (Math.random() > prob) {
				p.sendMessage(plugin.header + "You gained 1 exp for riding " + p.getVehicle().getName() + "!");
				if (stats.changeExp(1)) {
					p.sendMessage(plugin.header + "You are now " + stats.getClassName() + " " + stats.getNumLevel() + "!");
				}
				StatsPlayer.putPlayerStats(p, stats);
				p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);
			}
		}
	}

	@EventHandler
	private void onPlayerRiding(VehicleMoveEvent e) {
		Vehicle v = e.getVehicle();
		List<Entity> passengers = v.getPassengers();
		for (Entity passenger : passengers) {
			if (passenger instanceof Player) {
				Player p = (Player) passenger;
				Stats stats = StatsPlayer.getPlayerStats(p);
				double prob = 0.005 * e.getFrom().distance(e.getTo());
				if (stats != null && stats.getClassName().equals(RoleClass.RIDER.getName()) && Math.random() < prob) {
					p.sendMessage(plugin.header + "You gained 1 exp for moving inside " + v.getName() + "!");
					if (stats.changeExp(1)) {
						p.sendMessage(plugin.header + "You are now " + stats.getClassName() + " " + stats.getNumLevel() + "!");
					}
					StatsPlayer.putPlayerStats(p, stats);
					p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);
				}
			}
		}
	}

	@EventHandler
	private void onPlayerRiding(HorseJumpEvent e) {
		AbstractHorse horse = e.getEntity();
		List<Entity> passengers = horse.getPassengers();
		for (Entity passenger : passengers) {
			if (passenger instanceof Player) {
				Player p = (Player) passenger;
				Stats stats = StatsPlayer.getPlayerStats(p);
				double prob = 0.2 * e.getPower();
				if (stats != null && stats.getClassName().equals(RoleClass.RIDER.getName()) && Math.random() < prob) {
					p.sendMessage(plugin.header + "You gained 1 exp for jumping with " + horse.getName() + "!");
					if (stats.changeExp(1)) {
						p.sendMessage(plugin.header + "You are now " + stats.getClassName() + " " + stats.getNumLevel() + "!");
					}
					StatsPlayer.putPlayerStats(p, stats);
					p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);
				}
			}
		}
	}

	@EventHandler
	private void onEntAttackEnt(EntityDamageByEntityEvent e) {
		Entity offender = e.getDamager();
		Entity defender = e.getEntity();

		if (offender instanceof Player) {
			Player p = (Player) offender;
			Stats stats = StatsPlayer.getPlayerStats(p);
			if (stats != null && stats.getClassName().equals(RoleClass.WARRIOR.getName()) && Math.random() < 0.1) {
				p.sendMessage(plugin.header + "You gained 1 exp for hitting " + defender.getName() + "!");
				if (stats.changeExp(1)) {
					p.sendMessage(plugin.header + "You are now " + stats.getClassName() + " " + stats.getNumLevel() + "!");
				}
				StatsPlayer.putPlayerStats(p, stats);
				p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);
			}
		}

		if (offender instanceof Projectile) {
			if (offender.hasMetadata(META_ARROW_SHOOTER)) {
				String projectileName = offender.getName();
				String offenderName = offender.getMetadata(META_ARROW_SHOOTER).get(0).asString();
				Player p = plugin.getServer().getPlayer(offenderName);
				if (p != null) {
					Stats stats = StatsPlayer.getPlayerStats(p);
					if (stats != null) {
						if (stats.getClassName().equals(RoleClass.ARCHER.getName())) {
							double prob = 0.1;
							if (offender instanceof Arrow) {
								prob = 0.2;
								if (offender.hasMetadata(META_ARROW_FORCE)) {
									prob *= offender.getMetadata(META_ARROW_FORCE).get(0).asFloat();
								}
							} else if (offender instanceof SpectralArrow) {
								prob = 0.3;
							} else if (offender instanceof Trident) {
								prob = 0.3;
							} else if (offender instanceof EnderPearl) {
								prob = 0.4;
								projectileName = "Ender Pearl";
							} else if (offender instanceof Egg) {
								prob = 0.05;
							} else if (offender instanceof Snowball) {
								prob = 0.05;
							} else if (offender instanceof ThrowableProjectile) {
								prob = 0.115;
							}
							if (Math.random() < prob) {
								p.sendMessage(plugin.header + "You gained 1 exp for hitting " + defender.getName() + " with "
										+ projectileName + "!");
								if (stats.changeExp(1)) {
									p.sendMessage(plugin.header + "You are now " + stats.getClassName() + " " + stats.getNumLevel() + "!");
								}
								StatsPlayer.putPlayerStats(p, stats);
								p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);
								offender = p;
							}
						} else if (stats.getClassName().equals(RoleClass.DRAGON.getName())) {
							if (offender.getFireTicks()>0) {
								double prob = 0.1;
								ItemStack helmet =p.getInventory().getHelmet(); 
								if (helmet != null && helmet.getType().equals(Material.DRAGON_HEAD)) {
									prob+=.1;
								}
								if (Math.random() < prob) {
									p.sendMessage(plugin.header + "You gained 1 exp for setting " + defender.getName() + " on fire with "
											+ projectileName + "!");
									if (stats.changeExp(1)) {
										p.sendMessage(plugin.header + "You are now " + stats.getClassName() + " " + stats.getNumLevel() + "!");
									}
									StatsPlayer.putPlayerStats(p, stats);
									p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);
									offender = p;
								}
							}
						}
					}
				}
			}
		}

		if (defender instanceof Player) {
			Player p = (Player) defender;
			Stats stats = StatsPlayer.getPlayerStats(p);
			if (stats != null && stats.getClassName().equals(RoleClass.TANK.getName()) && Math.random() < 0.05) {
				p.sendMessage(plugin.header + "You gained 1 exp for getting hit by " + offender.getName() + "!");
				if (stats.changeExp(1)) {
					p.sendMessage(plugin.header + "You are now " + stats.getClassName() + " " + stats.getNumLevel() + "!");
				}
				StatsPlayer.putPlayerStats(p, stats);
				p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);
			}
		}
	}

	@EventHandler
	private void onEntShootArrow(EntityShootBowEvent e) {
		LivingEntity offender = e.getEntity();
		if (offender instanceof Player) {
			Player p = (Player) offender;
			Stats stats = StatsPlayer.getPlayerStats(p);
			if (stats != null && (stats.getClassName().equals(RoleClass.ARCHER.getName()) || stats.getClassName().equals(RoleClass.DRAGON.getName()))) {
				Entity projectile = e.getProjectile();
				projectile.setMetadata(META_ARROW_SHOOTER, new FixedMetadataValue(plugin, p.getName()));
				projectile.setMetadata(META_ARROW_FORCE, new FixedMetadataValue(plugin, e.getForce()));
			}
		}
	}

	@EventHandler
	private void onEntLaunch(ProjectileLaunchEvent e) {
		if (e.getEntity() instanceof ThrowableProjectile) {
			ThrowableProjectile projectile = (ThrowableProjectile) e.getEntity();
			if (projectile.getShooter() instanceof Player) {
				Player p = (Player) projectile.getShooter();
				Stats stats = StatsPlayer.getPlayerStats(p);
				if (stats != null && (stats.getClassName().equals(RoleClass.ARCHER.getName()) || stats.getClassName().equals(RoleClass.DRAGON.getName()))) {
					projectile.setMetadata(META_ARROW_SHOOTER, new FixedMetadataValue(plugin, p.getName()));
				}
			}
		}
	}

	@EventHandler
	private void onPlayerFish(PlayerFishEvent e) {
		if (e.getState().equals(State.CAUGHT_FISH)) {
			Player p = e.getPlayer();
			Stats stats = StatsPlayer.getPlayerStats(e.getPlayer());
			p.sendMessage("Estado: " + e.getState());
			if (stats != null && stats.getClassName().equals(RoleClass.FISHERMAN.getName()) && Math.random() < 0.3) {
				p.sendMessage(plugin.header + "You gained 1 exp for fishing " + e.getCaught().getName() + "!");
				if (stats.changeExp(1)) {
					p.sendMessage(plugin.header + "You are now " + stats.getClassName() + " " + stats.getNumLevel() + "!");
				}
				StatsPlayer.putPlayerStats(p, stats);
				p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);
			}
		}
	}
	
	@EventHandler
	private void onEntCombustByEnt(EntityCombustByEntityEvent e) {
		Entity offender = e.getCombuster();
		if (offender instanceof Player) {
			Player p = (Player) offender;
			Stats stats = StatsPlayer.getPlayerStats(p);
			double prob = 0.05;
			ItemStack helmet =p.getInventory().getHelmet(); 
			if (helmet != null && helmet.getType().equals(Material.DRAGON_HEAD)) {
				prob+=.1;
			}
			if (stats != null && stats.getClassName().equals(RoleClass.DRAGON.getName()) && Math.random() < prob) {
				Entity defender = e.getEntity();
				p.sendMessage(plugin.header + "You gained 1 exp for combusting " + defender.getName() + "!");
				if (stats.changeExp(1)) {
					p.sendMessage(plugin.header + "You are now " + stats.getClassName() + " " + stats.getNumLevel() + "!");
				}
				StatsPlayer.putPlayerStats(p, stats);
				p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);
			}
		}
	}
}
