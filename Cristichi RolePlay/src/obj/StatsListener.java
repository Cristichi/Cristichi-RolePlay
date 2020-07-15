package obj;

import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class StatsListener implements Listener {
	private static final String META_ARROW_DAMAGE = "crisrp_arrow_damage";

	private Plugin plugin;

	public StatsListener(Plugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	private void onEntAttackEnt(EntityDamageByEntityEvent e) {
		Entity offender = e.getDamager();
		if (offender instanceof Player) {
			Player p = (Player) offender;
			Stats stats = StatsPlayer.getPlayerStats(p);
			if (stats != null) {
				e.setDamage(e.getDamage() + stats.getCurrentLevel().getStrength());
			}
		}

		Entity defender = e.getEntity();
		if (defender instanceof Player) {
			Player p = (Player) defender;
			Stats stats = StatsPlayer.getPlayerStats(p);
			if (stats != null) {
				double newDmg = calculateDamage(stats, e.getCause(), e.getDamage(), offender, defender);

				if (newDmg == 0) {
					e.setCancelled(true);
					p.playSound(p.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1, 1);
					if (offender instanceof Player) {
						((Player) offender).playSound(p.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1, 1);

					}
				} else {
					e.setDamage(newDmg);
				}
			}
		}

		if (offender instanceof Projectile) {
			if (offender.hasMetadata(META_ARROW_DAMAGE)) {
				double nuevoDmg = e.getDamage() + offender.getMetadata(META_ARROW_DAMAGE).get(0).asInt();
				e.setDamage(nuevoDmg > 0 ? nuevoDmg : 0);
			}
		}
	}

	@EventHandler
	private void onEntShoot(EntityShootBowEvent e) {
		LivingEntity offender = e.getEntity();
		if (offender instanceof Player) {
			Player p = (Player) offender;
			Stats stats = StatsPlayer.getPlayerStats(p);
			if (stats != null) {
				Entity proyectil = e.getProjectile();
				proyectil.setMetadata(META_ARROW_DAMAGE,
						new FixedMetadataValue(plugin, stats.getCurrentLevel().getDexterity()));
			}
		}
	}

//	@EventHandler
//	private void onEntGetShooted(ProjectileHitEvent e) {
//		System.out.println("onEntGetShooted");
//		Entity defender = e.getHitEntity();
//		if (defender instanceof Player) {
//			Player p = (Player) defender;
//			Stats stats = StatsPlayer.getPlayerStats(p);
//			if (stats != null) {
//				calculateDamage(stats, DamageCause.PROJECTILE, e.get, offender, defender)
//			}
//		}
//	}

	private double calculateDamage(Stats stats, DamageCause cause, double initialDamage, Entity offender,
			Entity defender) {
		Level lvl = stats.getCurrentLevel();
		float chance0dmg = 0;
		switch (cause) {
		case ENTITY_ATTACK:
		case ENTITY_SWEEP_ATTACK:
		case BLOCK_EXPLOSION:
		case ENTITY_EXPLOSION:
			chance0dmg = lvl.getBlock();
			break;
		case PROJECTILE:
			chance0dmg = lvl.getDodge();
			break;

		default:
			break;
		}
		double newDmg = initialDamage * (1 - lvl.getResistance());
		if (Math.random() < chance0dmg) {
			newDmg = 0;
		}
		return newDmg;
	}
}
