package obj;

import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
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
			Stats stats = StatsPlayer.players.get(p.getUniqueId());
			if (stats != null) {
				e.setDamage(e.getDamage() + stats.getStrength());
			}
		}

		Entity defender = e.getEntity();
		if (defender instanceof Player) {
			Player p = (Player) defender;
			Stats stats = StatsPlayer.players.get(p.getUniqueId());
			if (stats != null) {
				float chance0dmg = 0;
				switch (e.getCause()) {
				case ENTITY_ATTACK:
				case ENTITY_SWEEP_ATTACK:
				case BLOCK_EXPLOSION:
				case ENTITY_EXPLOSION:
					chance0dmg = stats.getBlock();
					break;
				case PROJECTILE:
					chance0dmg = stats.getDodge();
					break;

				default:
					break;
				}
				double newDmg = e.getDamage() * (1 - stats.getResistance());
				if (Math.random() < chance0dmg) {
					e.setCancelled(true);
					p.playSound(p.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1, 1);
					if (offender instanceof Player) {
						((Player) offender).playSound(p.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1, 1);
					}
					newDmg = 0;
				}
				e.setDamage(newDmg > 0 ? newDmg : 0);
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
			Stats stats = StatsPlayer.players.get(p.getUniqueId());
			if (stats != null) {
				Entity proyectil = e.getProjectile();
				proyectil.setMetadata(META_ARROW_DAMAGE, new FixedMetadataValue(plugin, stats.getDexterity()));
			}
		}
	}

	@EventHandler
	private void onEntGetShooted(ProjectileHitEvent e) {
		Entity defender = e.getHitEntity();
		if (defender instanceof Player) {
			Player p = (Player) defender;
			Stats clase = StatsPlayer.players.get(p.getUniqueId());
			if (clase != null) {
				if (Math.random() < clase.getBlock()) {
					p.playSound(p.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1, 1);
				}
			}
		}
	}

}
