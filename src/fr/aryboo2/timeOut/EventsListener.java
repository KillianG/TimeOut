package fr.aryboo2.timeOut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class EventsListener implements Listener {

	GameState gs;
	static TimeOut to;
	public static ArrayList pseudo = new ArrayList();
	public static HashMap<Player, Integer> timeLeft = new HashMap<Player, Integer>();
	public static Player lastPlayerJoined;
	private int isVIPInProgress = -1;
	private static Random random = null;
	private boolean islaunched = false;

	public static int nbrPlayer = 0;

	public EventsListener(TimeOut to) {
		this.random = new Random();
		this.to = to;
	}
	
	@EventHandler
	 public void ServerListPing(ServerListPingEvent event) {
	  if(GameState.getState() == GameState.reloading) {
	   event.setMotd(ChatColor.RED + "Gen : "+(int)LoadingChunkTask.percent+"% !");
	  } else {
	   if(GameState.getState() == GameState.inGame) {
	     event.setMotd(ChatColor.RED + "En cours !");
	   } else {
	    event.setMotd(ChatColor.GREEN + "En attente...");
	   }
	  }
	 }
	
	@EventHandler
	public void onLogin(PlayerLoginEvent e){
		
		Player p = e.getPlayer();
		
		if(GameState.isState(GameState.inGame) && !p.isWhitelisted()){
			e.setResult(Result.KICK_OTHER);
		     e.setKickMessage("Partie en cour");
		}
		if(GameState.isState(GameState.reloading) && !p.isWhitelisted()){
			e.setResult(Result.KICK_OTHER);
		     e.setKickMessage("Chargement, veuillez patienter");
		}
		
		if(to.getConfig().getInt("nbvip") > 0) {
			   if((Bukkit.getServer().getMaxPlayers() - Bukkit.getOnlinePlayers().size()) <= to.getConfig().getInt("nbvip")) {
			    if(isVIPInProgress == -1) {
			     isVIPInProgress = 15;
			     decompte();
			    }
			    if(isVIPInProgress > 0) {
			    	if(!e.getPlayer().hasPermission("haspriority")){
			    		 e.setResult(Result.KICK_OTHER);
					     e.setKickMessage("Slots réservés aux VIP pendant " + isVIPInProgress + " secondes !");
			    	}
			     
			    }
			   }
			  }
	}
	
	 @EventHandler
	    public void onFoodLevelChange(FoodLevelChangeEvent event)
	    {
		 if(GameState.isState(GameState.waiting)){
	        event.setCancelled(true);
		 }
	    }

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		lastPlayerJoined = e.getPlayer();
		Player p = e.getPlayer();
		
		p.getInventory().clear();
		p.setHealth(20);
		p.setFoodLevel(20);
		
		if(Bukkit.getOnlinePlayers().size() == 10 && !islaunched){
			new Thread(new TimerStart(to)).start();
			islaunched = true;
		}
		 
		
		Location loc = new Location(p.getWorld(), 0,150,0);
		
			loc.getChunk().load();
			if(GameState.isState(GameState.waiting) || GameState.isState(GameState.reloading)){
				p.teleport(loc);
				nbrPlayer++;
				pseudo.add(p.getName());
				timeLeft.put(p,1800);
				p.setGameMode(GameMode.ADVENTURE);
			}

	

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		
		if(GameState.isState(GameState.inGame) || GameState.isState(GameState.doubletimeingame) || GameState.isState(GameState.invicible)){
			
			nbrPlayer--;
				
		}
		
		if (Bukkit.getOnlinePlayers().size() <= 0 ) {
			if(GameState.isState(GameState.inGame) || GameState.isState(GameState.doubletimeingame) || GameState.isState(GameState.invicible)){

			World world = p.getWorld();

			WorldBorder border = world.getWorldBorder();
			to.getServer().setWhitelist(false);
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");

			GameState.setState(GameState.waiting);
			}
		}
		if(GameState.isState(GameState.waiting) || GameState.isState(GameState.reloading)){
		for (int i = 0; i < Bukkit.getOnlinePlayers().size(); i++) {
			if (((String) pseudo.get(i)).equalsIgnoreCase(p.getName())) {

				pseudo.remove(i);
				timeLeft.remove(i);
				nbrPlayer--;
				break;
			}
		}
		}

	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		String command = e.getMessage();
		String[] str = command.split(" ");
		
		

		if (p.hasPermission("TimeOut.Start")
				&& str[0].equalsIgnoreCase("/tostart")) {
			this.startGame(p);
			
			
			
			for(World world : Bukkit.getWorlds()){
				world.setPVP(true);
			}
			e.setCancelled(true);
		}
		if(p.isOp() && str[0].equalsIgnoreCase("/tospawnadd")){
			to.getSpawnsManager().addSpawnPoint(p.getLocation());
			System.out.println("Spawn Created ! Number: " + to.getSpawnsManager().getSpawnPoints().size());
			e.setCancelled(true);
			
		}
		if(p.isOp() && str[0].equalsIgnoreCase("/tospawnrandom")){
			this.generateSpawnsRandom(p);
			e.setCancelled(true);
			
		}
	}

	@EventHandler
	public void onRightClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (GameState.isState(GameState.inGame)|| GameState.isState(GameState.doubletimeingame) || GameState.isState(GameState.invicible)) {
			if (e.getAction() == Action.RIGHT_CLICK_AIR
					|| e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				
				if(p.getItemInHand().getType() == Material.COMPASS){
					
					List<Entity> entities = p.getNearbyEntities(200,200,200);
					for(Entity entity : entities){
						if(entity instanceof Player){
						p.setCompassTarget(entity.getLocation());
						}
					}
					
				}
				
				
				if (p.getItemInHand().getType() == Material.DIAMOND) {
					
					for (int i = 0; i < EventsListener.nbrPlayer; i++) {
						System.out.println("--> " + timeLeft.get(p) + "-->" + pseudo.get(i));
						}
					
					for (int i = 0; i < EventsListener.nbrPlayer; i++) {
						if (((String) pseudo.get(i)).equalsIgnoreCase(p
								.getName())) {
							timeLeft.put(p, (int) timeLeft.get(p) + 60);
						}
					}
					
					if(p.getItemInHand().getAmount() != 1){
					p.getItemInHand().setAmount(
							p.getItemInHand().getAmount() - 1);
					}else{
						p.setItemInHand(null);
					}
					e.setCancelled(true);

				}
				
				if (p.getItemInHand().getType() == Material.EMERALD) {
					for (int i = 0; i < EventsListener.nbrPlayer; i++) {
						if (((String) pseudo.get(i)).equalsIgnoreCase(p
								.getName())) {
							timeLeft.put(p, (int) timeLeft.get(p) + 30);
						}
					}
					
					if(p.getItemInHand().getAmount() != 1){
					p.getItemInHand().setAmount(
							p.getItemInHand().getAmount() - 1);
					}else{
						p.setItemInHand(null);
					}
					e.setCancelled(true);

				}

				if (p.getItemInHand().getType() == Material.GHAST_TEAR) {

					for (int i = 0; i < EventsListener.nbrPlayer; i++) {
						if (((String) pseudo.get(i)).equalsIgnoreCase(p
								.getName())) {
							timeLeft.put(p, (int) timeLeft.get(p) + 300);
						}
					}

					if(p.getItemInHand().getAmount() != 1){
						p.getItemInHand().setAmount(
								p.getItemInHand().getAmount() - 1);
						}else{
							p.setItemInHand(null);
						}
					e.setCancelled(true);
				}
				
				if (p.getItemInHand().getType() == Material.QUARTZ_BLOCK) {

					for (int i = 0; i < EventsListener.nbrPlayer; i++) {
						if (((String) pseudo.get(i)).equalsIgnoreCase(p
								.getName())) {
							timeLeft.put(p, (int) timeLeft.get(p) + 5);
						}
					}

					if(p.getItemInHand().getAmount() != 1){
						p.getItemInHand().setAmount(
								p.getItemInHand().getAmount() - 1);
						}else{
							p.setItemInHand(null);
						}
					e.setCancelled(true);
				}
				
				

			}
			
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		if (GameState.isState(GameState.inGame) || GameState.isState(GameState.doubletimeingame)) {
			
			
			
			for(Player players : Bukkit.getOnlinePlayers()){
				
				to.getSpawnsManager().generateRandomSpawnPoints(players.getWorld(), 2, 200, 10, 0, 0);
				Location lo = to.getSpawnsManager().getSpawnPoints().get(random.nextInt(to.getSpawnsManager().getSpawnPoints().size()));
				players.getWorld().setSpawnLocation((int)lo.getX(), players.getWorld().getHighestBlockYAt((int)lo.getX(), (int)lo.getZ()) +1, (int)lo.getZ());
				(to.getSpawnsManager()).getSpawnPoints().remove(lo);
			}
			
			Player killed = e.getEntity().getPlayer();

			for (int i = 0; i < Bukkit.getOnlinePlayers().size(); i++) {
				if (((String) pseudo.get(i)).equalsIgnoreCase(killed.getName())) {
					timeLeft.put(killed, (int) timeLeft.get(killed) - 600);
					break;
				}
			}

			if (e.getEntity().getKiller() instanceof Player) {

				Player killer = e.getEntity().getKiller();
				ItemStack itemstack = new ItemStack(Material.GOLDEN_APPLE, 1);
				killer.getWorld().dropItem(killer.getLocation(), itemstack);

				for (int i = 0; i < Bukkit.getOnlinePlayers().size(); i++) {
					if (((String) pseudo.get(i)).equalsIgnoreCase(killer
							.getName())) {
						timeLeft.put(killer, (int) timeLeft.get(killer) + 600);
						break;
					}
				}

			}
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (GameState.isState(GameState.inGame) || GameState.isState(GameState.doubletimeingame)) {
			if (e.getEntity() instanceof Player) {
				Player p = (Player) e.getEntity();

				for (int i = 0; i < EventsListener.nbrPlayer; i++) {
					if (((String) pseudo.get(i)).equalsIgnoreCase(p.getName())) {
						timeLeft.put(p, (int) timeLeft.get(p) - 10);
					}
				}
			}
		}else if(GameState.isState(GameState.invicible) && e.getEntityType() == EntityType.PLAYER){
			e.setCancelled(true);
		}

	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if (GameState.isState(GameState.inGame)|| GameState.isState(GameState.doubletimeingame)) {

			if (e.getEntity() instanceof Player) {
				Player p = (Player) e.getEntity();

				for (int i = 0; i < EventsListener.nbrPlayer; i++) {
					if (((String) pseudo.get(i)).equalsIgnoreCase(p.getName())) {
						timeLeft.put(p, (int) timeLeft.get(p) - 10);
					}
				}
			}

		}
	}
	
	
	
	
	public void decompte(){
		  if(isVIPInProgress > 0) {
		   isVIPInProgress--;
		   Bukkit.getScheduler().runTaskLater(to, new BukkitRunnable() {
		    public void run() {
		     decompte();
		    }
		   }, 20L);
		  }
		 }
	
	public static void startGame(Player p){
		
		EventsListener.generateSpawnsRandom(p);
		

		for(Player players : Bukkit.getOnlinePlayers()){
			players.setGameMode(GameMode.SURVIVAL);
			Bukkit.getServer().getWhitelistedPlayers().add(players);
		}

		GameState.setState(GameState.invicible);

		World world = p.getWorld();
		p.getWorld().setTime(0);
		WorldBorder border = world.getWorldBorder();
		border.setSize(600);
		border.setCenter(0.0, 0.0);
		p.getWorld().setGameRuleValue("keepInventory", "true");
		
		
			for(Player players : Bukkit.getOnlinePlayers()){
			List<Location> unusedTP = to.getSpawnsManager().getSpawnPoints();
			Location lo = unusedTP.get(random.nextInt(unusedTP.size()));
			lo.getChunk().load(true);
			players.teleport(lo);
			
			unusedTP.remove(lo);
			}
		Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "Vous êtes invincible durant 1 minutes");
		p.getWorld().setSpawnLocation(25, p.getWorld().getHighestBlockYAt(25,25) + 2, 25);
		
		

		to.getServer().setWhitelist(true);
		new Thread(new Timer(to)).start();
		
	}
	
	public static void generateSpawnsRandom(Player p){
		to.getSpawnsManager().generateGridSpawnPoints(p.getWorld(), Bukkit.getOnlinePlayers().size() + 10, 600, 90, 0, 0);
		System.out.println("Spawns Created ! Number: " + to.getSpawnsManager().getSpawnPoints().size());
	}
	
	
}
