package fr.aryboo2.timeOut;

import io.puharesource.mc.titlemanager.api.ActionbarTitleObject;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.scheduler.BukkitRunnable;

public class LoadingChunkTask
  extends BukkitRunnable
{
  public static double percent;
  public static int ancientPercent;
  public static double currentChunkLoad;
  public static double totalChunkToLoad;
  public int cx;
  private TimeOut p = null;
  
  public LoadingChunkTask(TimeOut plugin, int radius2)
  {
	  this.p = plugin;
	  percent = 0.0D;
    ancientPercent = 0;
    int cx = radius2 / 16;
    this.cx = (-cx);
    totalChunkToLoad = cx * cx;
    currentChunkLoad = 0.0D;
  }
  
  public void run()
  {
    for (int x = this.cx; x <= this.cx + 1; x++) {
      for (int z = this.cx; z <= this.cx + 1; z++) {
        try
        {
          Chunk c = Bukkit.getWorld("world").getChunkAt(x, z);
          c.load(true);
          if(!c.isLoaded()){
        	  c.load(true);
          }
          currentChunkLoad += 1.0D;
        }
        catch (Exception localException) {}
      }
    }
    this.cx += 2;
    
    percent = currentChunkLoad / totalChunkToLoad * 100.0D;
    if (ancientPercent < (int)percent)
    {
      System.out.println("Loading chunks.." + (int)percent + "%");
      GameState.setState(GameState.reloading);
      //Bukkit.getServer().broadcast("Généartion map : "+ (int)percent + "%", null);
      new ActionbarTitleObject(ChatColor.GOLD + "Génération map : "+ (int)percent + "%").broadcast();
      ancientPercent = (int)percent;
    }
    if (percent >= 100.0D)
    {
      Bukkit.getLogger().info("Fin generation des chunks !");
      
    	  Bukkit.setWhitelist(false);
      
      
      GameState.setState(GameState.waiting);
      cancel();
    }
  }
}


