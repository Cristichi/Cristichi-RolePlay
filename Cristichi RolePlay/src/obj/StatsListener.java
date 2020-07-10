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

	private Plugin plugin;

	public StatsListener(Plugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	private void alAtacar(EntityDamageByEntityEvent e) {
		Entity atacante = e.getDamager();
		if (atacante instanceof Player) {
			Player p = (Player) atacante;
			Stats clase = StatsPlayer.players.get(p.getUniqueId());
			if (clase != null) {
				e.setDamage(e.getDamage() + clase.getStrength());
			}
		}

		Entity atacado = e.getEntity();
		if (atacado instanceof Player) {
			Player p = (Player) atacado;
			Stats clase = StatsPlayer.players.get(p.getUniqueId());
			if (clase != null) {
				float porc = 0;
				switch (e.getCause()) {
				case ENTITY_ATTACK:
				case ENTITY_SWEEP_ATTACK:
				case BLOCK_EXPLOSION:
				case ENTITY_EXPLOSION:
					porc = clase.getBlock();
					break;
				case PROJECTILE:
					porc = clase.getDodge();
					break;

				default:
					break;
				}
				double nuevo = e.getDamage() * (1 - clase.getResistance());
				if (Math.random() < porc) {
					e.setCancelled(true);
					p.playSound(p.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1, 1);
					if (atacante instanceof Player) {
						((Player) atacante).playSound(p.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1, 1);
					}
					nuevo = 0;
				}
				e.setDamage(nuevo > 0 ? nuevo : 0);
			}
		}

		if (atacante instanceof Projectile) {
			if (atacante.hasMetadata("flechaDamage")) {
				double nuevoDmg = e.getDamage() + atacante.getMetadata("flechaDamage").get(0).asInt();
				e.setDamage(nuevoDmg > 0 ? nuevoDmg : 0);
			}
		}
	}

	@EventHandler
	private void alDispararArco(EntityShootBowEvent e) {
		LivingEntity atacante = e.getEntity();
		if (atacante instanceof Player) {
			Player p = (Player) atacante;
			Stats clase = StatsPlayer.players.get(p.getUniqueId());
			if (clase != null) {
				Entity proyectil = e.getProjectile();
				proyectil.setMetadata("flechaDamage", new FixedMetadataValue(plugin, clase.getDexterity()));
			}
		}
	}

	@EventHandler
	private void alRecibirDisparo(ProjectileHitEvent e) {
		Entity atacado = e.getHitEntity();
		if (atacado instanceof Player) {
			Player p = (Player) atacado;
			Stats clase = StatsPlayer.players.get(p.getUniqueId());
			if (clase != null) {
				if (Math.random() < clase.getBlock()) {
					p.playSound(p.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1, 1);
				}
			}
		}
	}

}
