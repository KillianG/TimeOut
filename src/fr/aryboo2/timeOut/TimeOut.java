package fr.aryboo2.timeOut;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;





public class TimeOut extends JavaPlugin {
	
	private SpawnsManager spawnsManager = null;

	public void onEnable() {
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new EventsListener(this), this);
		
		saveDefaultConfig();
		GameState.setState(GameState.waiting);
		spawnsManager = new SpawnsManager(this);
		
		for(World world : Bukkit.getWorlds()){
			world.setPVP(false);

		}
		
		this.getServer().setWhitelist(false);
		
		
		
		
		
		getLogger().info("Modification Spawn");
		

		try {
			generateLobby();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (MaxChangedBlocksException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		getLogger().info("generation des chunks");
		GameState.setState(GameState.reloading);
		new LoadingChunkTask(this, 600).runTaskTimer(this, 0L, 1L);

	}

	public void onDisable(){
		reloadMap();

	}

	public static void generateLobby() throws IOException,
			MaxChangedBlocksException, DataException {
		WorldEditPlugin we = (WorldEditPlugin) Bukkit.getPluginManager()
				.getPlugin("WorldEdit");
		File schematic = new File(
				"plugins/WorldEdit/schematics/spawn-run.schematic");
		for(World world : Bukkit.getWorlds()){
			EditSession session = getWorldEdit().getWorldEdit()
					.getEditSessionFactory()
					.getEditSession(new BukkitWorld(world),
							1000000);
			MCEditSchematicFormat.getFormat(schematic).load(schematic)
			.paste(session, new Vector(-10, 145, 10), false);

		}
		

		
	}
	
	public static void reloadMap(){
		
		File file = new File("world");
		try {
			FileUtils.deleteDirectory(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Deleted world !");
	}
	
	public static WorldEditPlugin getWorldEdit() {
        Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if (p instanceof WorldEditPlugin) return (WorldEditPlugin) p;
        else return null;
}
	public SpawnsManager getSpawnsManager() {
		return spawnsManager;
	}
}
