package obj;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ExpListener implements Listener {

	@EventHandler
	private void onEntAttackEnt(EntityDamageByEntityEvent e) {
		Entity offender = e.getDamager();
		if (offender instanceof Player) {
			Player p = (Player) offender;
			Stats stats = StatsPlayer.players.get(p.getUniqueId());
			if (stats != null) {
				stats.increaseExp(1);
				StatsPlayer.players.put(p.getUniqueId(), stats);
			}
		}
	}
}
