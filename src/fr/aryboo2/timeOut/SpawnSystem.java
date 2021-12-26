package fr.aryboo2.timeOut;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SpawnSystem {

	public static void spawnPlayers(int borderSize) {
		int y;
		Location loc;
		int i = 0;

		for (int x = (-borderSize / 2); x < borderSize / 2; x = x + 100) {
			for (int z = (-borderSize / 2); z < borderSize / 2; z = z + 100) {

				if (i < Bukkit.getOnlinePlayers().size()) {
					Player p = Bukkit.getPlayer((String) EventsListener.pseudo
							.get(i));
					

					loc = new Location(p.getWorld(), x + 10, p.getWorld().getHighestBlockYAt(x, z) + 20, z + 10);
					i++;
					loc.getChunk().load();
					p.teleport(loc);
					p.setHealth(20);
					p.setFoodLevel(20);
					p.getInventory().clear();
				}

			}
		}

	}

}
