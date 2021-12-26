package fr.aryboo2.timeOut;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class Timer implements Runnable {
	static TimeOut to;
	int timerTotal;
	int vitesse = 1000;
	int playerNbr;
	private boolean run = true;

	public Timer(TimeOut to) {
		Timer.to = to;
	}

	@Override
	public void run() {
		timerTotal = 0;
		while(true){
		while (GameState.isState(GameState.inGame) || GameState.isState(GameState.invicible)) {

			timerTotal++;
			if(timerTotal >= 60 && GameState.isState(GameState.invicible)){
				GameState.setState(GameState.inGame);
				Bukkit.getServer().broadcastMessage(ChatColor.RED + "Vous n'êtes plus invincible !");
				for(World worlds : Bukkit.getWorlds()){
					worlds.setPVP(true);
				}
			}

			for (final Player p: Bukkit.getOnlinePlayers()) {

				Bukkit.getScheduler().runTask(to, new Runnable() {
					public void run() {

						for (int i = 0; i < EventsListener.nbrPlayer; i++) {
							playerNbr = i;
							ScoreboardManager manager = Bukkit
									.getScoreboardManager();
							Scoreboard board = manager.getNewScoreboard();
							Objective objective = board.registerNewObjective(
									"LeftTime", "dummy");
							objective.setDisplaySlot(DisplaySlot.SIDEBAR);
							objective.setDisplayName("Time-Out");


							p.setScoreboard(board);

							Score nbrPlayer = objective.getScore(ChatColor.GREEN
									+ "Joueurs restant: " + ChatColor.WHITE + 
									String.valueOf(Bukkit.getOnlinePlayers().size()));
							nbrPlayer.setScore(2);
							
							if(timerTotal % 60 < 10){
								Score scr = objective.getScore(ChatColor.GREEN
										+ "Temps écoulé : " + ChatColor.WHITE + timerTotal / 60 + ":0"
										+ timerTotal % 60);
								scr.setScore(1);
							}else{
								Score scr = objective.getScore(ChatColor.GREEN
										+ "Temps écoulé : " + ChatColor.WHITE + timerTotal / 60 + ":"
										+ timerTotal % 60);
								scr.setScore(1);
							}
							
							if((int) EventsListener.timeLeft.get(p) % 60 < 10){
								
								Score score = objective.getScore(ChatColor.GREEN
										+ "Mon temps: " + ChatColor.WHITE
										+ (int) EventsListener.timeLeft.get(p) / 60
										+ ":0"
										+ (int) EventsListener.timeLeft.get(p) % 60);
								score.setScore(0);
								
							}else{
								Score score = objective.getScore(ChatColor.GREEN
										+ "Mon temps: " + ChatColor.WHITE
										+ (int) EventsListener.timeLeft.get(p) / 60
										+ ":"
										+ (int) EventsListener.timeLeft.get(p) % 60);
								score.setScore(0);
								
														}
							
							
							
							if ((int) EventsListener.timeLeft.get(p) <= 0 && p.getGameMode() != GameMode.SPECTATOR) {
								playerNbr = i;
								to.getServer().broadcastMessage(ChatColor.GREEN + "TimeOut ! " + p.getName() + " est mort !");
								p.setGameMode(GameMode.SPECTATOR);
								p.setScoreboard(manager.getNewScoreboard());
								EventsListener.pseudo.remove(playerNbr);
								EventsListener.timeLeft.remove(playerNbr);
								to.getServer().getPlayer((String) EventsListener.pseudo.get(playerNbr)).sendMessage(ChatColor.RED + "Votre temps est écoulé, vous êtes mort");
								EventsListener.nbrPlayer = EventsListener.nbrPlayer - 1;

							}
								

							
							
							

						}
					}
				});

				EventsListener.timeLeft.put(p,(int) EventsListener.timeLeft.get(p) - 1);

				

			}

			if (timerTotal == 1500) {
				final ItemStack itemstack = new ItemStack(Material.COMPASS, 1);
				for(final Player p : Bukkit.getOnlinePlayers()){
				

					
						Bukkit.getScheduler().runTask(to, new Runnable() {
							public void run() {
						p.getWorld().dropItem(p.getLocation(), itemstack);
							}});
					
					
				}
				vitesse = 500;
				Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "Attention, le temps passe dÃ©sormais" + ChatColor.RED +  " 2 " + ChatColor.GREEN + "fois plus vite !");
			}
			
			if(EventsListener.nbrPlayer <= 1){
				String name = null;
				if(GameState.isState(GameState.inGame) || GameState.isState(GameState.doubletimeingame)){
					for(Player player : Bukkit.getOnlinePlayers()){
						
						if(player.getGameMode() == GameMode.SURVIVAL){
							name = player.getName();
						}
					}
					Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "Et nous avons un gagnant ! Bravo à " + name );

				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
				break;
				
			}
			}

			try {
				Thread.sleep(vitesse);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		break;
		}

		}

	public static void teleportToSpawn(Player p) {
		final Location loc = new Location(p.getWorld(), 0, p.getWorld().getHighestBlockAt(0, 0).getY(), 0);
		
		Bukkit.getScheduler().runTask(to, new Runnable() {
			public void run() {
		loc.getChunk().load();
			}});
		p.teleport(loc);
		p.sendMessage(ChatColor.RED
				+ "Il vous reste seulement 5 minutes de vie, vous ne pouvez plus miner, vous devez vous battre !");
		

	}

}
