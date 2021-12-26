package fr.aryboo2.timeOut;

import java.io.File;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;

public class Util
{
	
	public static boolean deleteworld(File f)
	  {
	    if (f.isDirectory())
	    {
	      File[] arrayOfFile;
	      int j = (arrayOfFile = f.listFiles()).length;
	      for (int i = 0; i < j; i++)
	      {
	        File f2 = arrayOfFile[i];
	        if (!deleteworld(f2)) {
	          return false;
	        }
	      }
	    }
	    if (!f.delete()) {
	      return false;
	    }
	    return true;
	  }
	
	public static void loadChunks(World w, Location location)
	  {
	    for (int x = (int)(location.getX() - 25.0D); x <= location.getX() + 25.0D; x++) {
	      for (int z = (int)(location.getZ() - 25.0D); z <= location.getZ() + 25.0D; z++)
	      {
	        Location l = location.clone();
	        Chunk c = l.add(x, 0.0D, z).getChunk();
	        if ((!l.getWorld().isChunkLoaded(c)) && 
	          (!l.getWorld().loadChunk(c.getX(), c.getZ(), true))) {
	          System.out.println("Failed to load chunk..");
	        }
	      }
	    }
	  }
	
	public static Location searchSafeSpot(Location location) {
		// We try to find a spot above or below the target
		
		Location safeSpot = null;
		final int maxHeight = (location.getWorld().getEnvironment() == World.Environment.NETHER) ? 125 : location.getWorld().getMaxHeight() - 2; // (thx to WorldBorder)
		
		for(int yGrow = (int) location.getBlockY(), yDecr = (int) location.getBlockY(); yDecr >= 1 || yGrow <= maxHeight; yDecr--, yGrow++) {
			// Above?
			if(yGrow < maxHeight) {
				Location spot = new Location(location.getWorld(), location.getBlockX(), yGrow, location.getBlockZ());
				if(isSafeSpot(spot)) {
					safeSpot = spot;
					break;
				}
			}
			
			// Below?
			if(yDecr > 1 && yDecr != yGrow) {
				Location spot = new Location(location.getWorld(), location.getX(), yDecr, location.getZ());
				if(isSafeSpot(spot)) {
					safeSpot = spot;
					break;
				}
			}
		}
		
		// A spot was found, we changes the pitch & yaw according to the original location.
		if(safeSpot != null) {
			safeSpot.setPitch(location.getPitch());
			safeSpot.setYaw(location.getYaw());
		}
		
		return safeSpot;
	}
	
	public static boolean isSafeSpot(Location location) {
		Block blockCenter = location.getWorld().getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
		Block blockAbove = location.getWorld().getBlockAt(location.getBlockX(), location.getBlockY() + 1, location.getBlockZ());
		Block blockBelow = location.getWorld().getBlockAt(location.getBlockX(), location.getBlockY() - 1, location.getBlockZ());
		
		if((blockCenter.getType().isTransparent() || (blockCenter.isLiquid() && !blockCenter.getType().equals(Material.LAVA) && !blockCenter.getType().equals(Material.STATIONARY_LAVA)))
				&& (blockAbove.getType().isTransparent() || (blockAbove.isLiquid() && !blockAbove.getType().equals(Material.LAVA) && !blockCenter.getType().equals(Material.STATIONARY_LAVA)))) {
			// two breathable blocks: ok

			if(blockBelow.getType().isSolid() || blockBelow.getType().equals(Material.WATER) || blockBelow.getType().equals(Material.STATIONARY_WATER)) {
				// The block below is solid, or liquid (but not lava)
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	
	public static boolean isInsideBorder(Location location, int diameter) {
		if(!location.getWorld().getEnvironment().equals(Environment.NORMAL)) { // The nether/end are not limited.
			return false;
		}else {
			return !Util.isInsideSquaredBorder(location, diameter);
		}
	}
	
	private static boolean isInsideSquaredBorder(Location location, int diameter) {
		Integer halfMapSize = (int) Math.floor(diameter/2);
		Integer x = location.getBlockX();
		Integer z = location.getBlockZ();
		
		Location spawn = location.getWorld().getSpawnLocation();
		Integer limitXInf = spawn.add(-halfMapSize, 0, 0).getBlockX();
		
		spawn = location.getWorld().getSpawnLocation();
		Integer limitXSup = spawn.add(halfMapSize, 0, 0).getBlockX();
		
		spawn = location.getWorld().getSpawnLocation();
		Integer limitZInf = spawn.add(0, 0, -halfMapSize).getBlockZ();
		
		spawn = location.getWorld().getSpawnLocation();
		Integer limitZSup = spawn.add(0, 0, halfMapSize).getBlockZ();
		
		return !(x < limitXInf || x > limitXSup || z < limitZInf || z > limitZSup);
	}
}