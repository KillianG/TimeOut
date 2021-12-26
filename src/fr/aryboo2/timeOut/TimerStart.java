package fr.aryboo2.timeOut;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;

public class TimerStart implements Runnable{
	private int timer;
	static TimeOut to;

	public TimerStart(TimeOut timeOut) {
		this.to = timeOut;
	}

	@Override
	public void run() {
		
		timer = 30;
		while(true){
			
				timer--;
				if(timer == 30)
					Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "Début dans: " + ChatColor.RED + timer + ChatColor.GREEN + " secondes");
				if(timer == 15)
					Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "Début dans: " + ChatColor.RED + timer + ChatColor.GREEN + " secondes");
				if(timer <= 5)
					Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "Début dans: " + ChatColor.RED + timer + ChatColor.GREEN + " secondes");

				if(timer == 0){
					Bukkit.getScheduler().runTask(to, new Runnable() {
						public void run() {
					EventsListener.startGame(EventsListener.lastPlayerJoined);
					
						}});
					break;
				
				
			}
			
			
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
			
		
		
		
		
	}

}
