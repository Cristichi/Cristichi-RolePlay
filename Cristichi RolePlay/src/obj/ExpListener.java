package obj;

import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;

import main.CrisPlay;

public class ExpListener implements Listener {
	private static final String META_ARROW_ARCHER = "crisrp_arrow_player";

	private CrisPlay plugin;

	public ExpListener(CrisPlay plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	private void onEntWalk(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		Stats stats = StatsPlayer.players.get(p.getUniqueId());
		if (stats != null && stats.getClassName().equals(RoleClass.BEGGAR.getName()) && Math.random() < 0.001) {
			stats.increaseExp(1);
			StatsPlayer.players.put(p.getUniqueId(), stats);
			p.sendMessage(plugin.header+"You gained 1 exp for walking!");
			p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);
		}
	}
	
	@EventHandler
	private void onEntAttackEnt(EntityDamageByEntityEvent e) {
		Entity offender = e.getDamager();
		Entity defender = e.getEntity();
		
		if (offender instanceof Player) {
			Player p = (Player) offender;
			Stats stats = StatsPlayer.players.get(p.getUniqueId());
			if (stats != null && stats.getClassName().equals(RoleClass.WARRIOR.getName()) && Math.random() < 0.1) {
				stats.increaseExp(1);
				StatsPlayer.players.put(p.getUniqueId(), stats);
				p.sendMessage(plugin.header+"You gained 1 exp for hitting "+defender.getName()+"!");
				p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);
			}
		}
		
		if (offender instanceof Projectile) {
			if (offender.hasMetadata(META_ARROW_ARCHER)) {
				String offenderName = offender.getMetadata(META_ARROW_ARCHER).get(0).asString();
				Player p = plugin.getServer().getPlayer(offenderName);
				if (p != null) {
					Stats stats = StatsPlayer.players.get(p.getUniqueId());
					double prob = 0.1;
					if (offender instanceof FishHook) {
						prob = 0.05;
					} else if (offender instanceof Arrow) {
						prob = 0.2;
					} else if (offender instanceof SpectralArrow || offender instanceof Trident) {
						prob = 0.3;
					}
					if (stats != null && stats.getClassName().equals(RoleClass.ARCHER.getName()) && Math.random() < prob) {
						stats.increaseExp(1);
						StatsPlayer.players.put(p.getUniqueId(), stats);
						p.sendMessage(plugin.header+"You gained 1 exp for hitting "+defender.getName()+" with "+offender.getName()+"!");
						p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);
						offender = p;
					}
				}
			}
		}
		
		if (defender instanceof Player) {
			Player p = (Player) defender;
			Stats stats = StatsPlayer.players.get(p.getUniqueId());
			if (stats != null && stats.getClassName().equals(RoleClass.TANK.getName()) && Math.random() < 0.1) {
				stats.increaseExp(1);
				StatsPlayer.players.put(p.getUniqueId(), stats);
				p.sendMessage(plugin.header+"You gained 1 exp for getting hit by "+offender.getName()+"!");
				p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);
			}
		}	
	}


	@EventHandler
	private void onEntShoot(EntityShootBowEvent e) {
		LivingEntity offender = e.getEntity();
		if (offender instanceof Player) {
			Player p = (Player) offender;
			Stats stats = StatsPlayer.players.get(p.getUniqueId());
			if (stats != null && stats.getClassName().equals(RoleClass.ARCHER.getName())) {
				Entity proyectil = e.getProjectile();
				proyectil.setMetadata(META_ARROW_ARCHER, new FixedMetadataValue(plugin, p.getName()));
			}
		}
	}
}
