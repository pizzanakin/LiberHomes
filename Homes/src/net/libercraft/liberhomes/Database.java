package net.libercraft.liberhomes;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Database extends YamlConfiguration implements Tracer {
	
	Main plugin;
	
	public Database(Main _plugin) {
		plugin = _plugin;
	}
	
	// --- GROUPS ---
	
	// Return the group number of the given world
	public String getGroup(World world) {

		// Iterate over all the groups;
		for (int i = 0; i < getGroupsLength(); i++) {

			// Iterate over the entries in the group
			for (int j = 0; j < getGroupLength(getString("groups." + i + ".name")); j++) {
				
				// Check if the world is found and return it's group number
				if (!getString("groups." + i + "." + j).equals(world.getUID().toString())) continue;
				return getString("groups." + i + ".name");
			}
		}
		return null;
	}
	
	// Returns the number of entries in a specific group;
	private int getGroupLength(String group) {
		
		for (int i = 0; i < getGroupsLength(); i++) {
			if (!getString("groups." + i + ".name").equalsIgnoreCase(group)) continue;
			
			int index = 0;
			while (get("groups." + i + "." + index) != null) {
				index++;
			}
			return index;
		}
		
		return -1;
	}
	
	private int getGroupsLength() {
		int index = 0;
		while (get("groups." + index) != null) {
			index++;
		}
		return index;
	}
	
	private int getGroupIndex(String group) {
		for (int i = 0; i < getGroupsLength(); i++) {
			if (getString("groups." + i + ".name").equalsIgnoreCase(group)) return i;
		}
		return -1;
	}
	
	public boolean addGroup(String group) {
		
		for (int i = 0; i < getGroupsLength(); i++) {
			if (getString("groups." + i + ".name").equalsIgnoreCase(group))
				return false;
		}
		
		set("groups." + getGroupsLength() + ".name", group);
		return true;
	}
	
	// Add a world to a group;
	public boolean addWorld(World world, String group) {
		
		// Check if the world is already grouped
		// Iterate over all the groups that are made
		for (int i = 0; i < getGroupsLength(); i++) {
			
			// Iterate over the entries in the group
			for (int j = 0; j < getGroupLength(getString("groups." + i + ".name")); j++) {
				
				// Check if the world is found and return it's group number
				if (!getString("groups." + i + "." + j).equals(world.getUID().toString())) continue;
				return false;
			}
		}
		
		// Add the world at the end of the group;
		int worldIndex = getGroupLength(group);
		int groupIndex = getGroupIndex(group);
		set("groups." + groupIndex + "." + worldIndex, world.getUID().toString());
		
		// Save the database
		try {
			save(new File(plugin.getDataFolder(), "data.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	// --- HOMES ---
	
	private Object getHomeValue(Player player, String group, int index, String property) {
		return get("homes." + group + "." + player.getUniqueId().toString() + "." + index + "." + property);
	}
	
	private void setHomeValue(Player player, String group, int index, String property, Object value) {
		set("homes." + group + "." + player.getUniqueId().toString() + "." + index + "." + property, value);
	}
	
	// Remove the home of the player with the specified name
	public boolean removeHome(Player player, String name, String group) {
		
		if (getHome(player, name, group) == null)
			return false;
		
		int removedIndex = -1;
		for (int i = 0; i < 3; i++) {
			
			// Compare the names in the databases with the specified name
			if (removedIndex == -1 && ((String) getHomeValue(player, group, i, "name")).equalsIgnoreCase(name))
				removedIndex = i;
			
			// Overwrite all entries after the removed entry
			if (removedIndex >= 0) {
				setHomeValue(player, group, i, "world", getHomeValue(player, group, i + 1, "world"));
				setHomeValue(player, group, i, "x", getHomeValue(player, group, i + 1, "x"));
				setHomeValue(player, group, i, "y", getHomeValue(player, group, i + 1, "y"));
				setHomeValue(player, group, i, "z", getHomeValue(player, group, i + 1, "z"));
				setHomeValue(player, group, i, "yaw", getHomeValue(player, group, i + 1, "yaw"));
				setHomeValue(player, group, i, "pitch", getHomeValue(player, group, i + 1, "pitch"));
				setHomeValue(player, group, i, "name", getHomeValue(player, group, i + 1, "name"));
			}
		}
		if (removedIndex == -1)
			return false;
		
		// Save the database
		try {
			save(new File(plugin.getDataFolder(), "data.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	
	// Return the number of homes the player has
	public int getHomesAmount(Player player, String group) {
		for (int i = 0; i < 3; i++) {
			if (getHomeValue(player, group, i, "name") == null) return i;
		}
		return 0;
	}
	
	public String getHomeNames(Player player, String group) {
		String names = "";
		for (int i = 0; i < getHomesAmount(player, group); i++) {
			names += (String) getHomeValue(player, group, i, "name");
			if (i != getHomesAmount(player, group) - 1) names += ", ";
		}
		return names;
	}
	
	
	// Return the location of the player's home with the specified name
	public Location getHome(Player player, String name, String group) {
		for (int i = 0; i < 3; i++) {
			if (!name.equalsIgnoreCase((String) getHomeValue(player, group, i, "name"))) continue;
			World world = plugin.getServer().getWorld(UUID.fromString((String) getHomeValue(player, group, i, "world")));
			double x = (double) getHomeValue(player, group, i, "x");
			double y = (double) getHomeValue(player, group, i, "y");
			double z = (double) getHomeValue(player, group, i, "z");
			
			float yaw;
			float pitch;
			if (getHomeValue(player, group, i, "yaw").getClass().equals(Float.class)) {
				yaw = (float) getHomeValue(player, group, i, "yaw");
				pitch = (float) getHomeValue(player, group, i, "pitch");
			} else {
				yaw = ((Double) getHomeValue(player, group, i, "yaw")).floatValue();
				pitch = ((Double) getHomeValue(player, group, i, "pitch")).floatValue();
			}
			
			return new Location(world, x, y, z, yaw, pitch);
		}
		return null;
	}
	
	
	// Add a home to the database with the specified name 
	public int addHome(Player player, String group, Location location, String name) {
		int index = 0;
		
		// Make sure the home doesn't exist yet
		for (int i = 0; i < getHomesAmount(player, group); i++) {
			if (((String) getHomeValue(player, group, i, "name")).equalsIgnoreCase(name))
				return 0;
		}
		
		// Make sure the player has less than 3 homes
		for (int i = 0; i < 3; i++) {
			if (getHomeValue(player, group, i, "name") != null) index++;
			else break;
		}
		if (index == 3) return 1;
		
		// Store the new home
		setHomeValue(player, group, index, "name", name);
		setHomeValue(player, group, index, "world", location.getWorld().getUID().toString());
		setHomeValue(player, group, index, "x", location.getX());
		setHomeValue(player, group, index, "y", location.getY());
		setHomeValue(player, group, index, "z", location.getZ());
		setHomeValue(player, group, index, "yaw", location.getYaw());
		setHomeValue(player, group, index, "pitch", location.getPitch());
		
		// Save the database
		try {
			save(new File(plugin.getDataFolder(), "data.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 2;
	}
	
	// Change the location of the specified home
	public boolean changeHome(Player player, String group, Location location, String name) {
		for (int i = 0; i < 3; i++) {
			if (!((String) getHomeValue(player, group, i, "name")).equalsIgnoreCase(name)) continue;
			setHomeValue(player, group, i, "world", location.getWorld().getUID().toString());
			setHomeValue(player, group, i, "x", location.getX());
			setHomeValue(player, group, i, "y", location.getY());
			setHomeValue(player, group, i, "z", location.getZ());
			setHomeValue(player, group, i, "yaw", location.getYaw());
			setHomeValue(player, group, i, "pitch", location.getPitch());
			
			// Save the database
			try {
				save(new File(plugin.getDataFolder(), "data.yml"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return true;
		}
		return false;
	}
}
