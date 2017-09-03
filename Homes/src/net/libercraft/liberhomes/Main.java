package net.libercraft.liberhomes;

import java.io.File;
import java.io.IOException;

import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import net.libercraft.liberhomes.commands.ChangehomeCommand;
import net.libercraft.liberhomes.commands.DelhomeCommand;
import net.libercraft.liberhomes.commands.HomeCommand;
import net.libercraft.liberhomes.commands.HomesCommand;
import net.libercraft.liberhomes.commands.SethomeCommand;
import net.libercraft.liberhomes.commands.VisitCommand;

public class Main extends JavaPlugin implements CommandExecutor, Tracer {

	private File datafile;
	private Database data;
	
	public Database getData() {
		return data;
	}
	
	@Override
	public void onEnable() {
		// Create a reference to the datafile
		datafile = new File(getDataFolder(), "data.yml");
		
		// Make sure the data file exists
		if (!datafile.exists()) {
			datafile.getParentFile().mkdirs();
			saveResource("data.yml", false);
		}
		
		// Load the database in the data variable
		data = new Database(this);
		try  {
			data.load(datafile);
		} catch (InvalidConfigurationException | IOException e) {
			e.printStackTrace();
		}
		
		// Check the config if it has any registered groups;
		if (getConfig().getStringList("groups") != null) {
			
			// Iterate over all the groups that are registered;
			for (String group:getConfig().getStringList("groups")) {
				
				// Make sure that each group has an entry in the config where worlds can be added;
				if (getConfig().get(group) == null) 
					getConfig().addDefault(group, "EMPTY");
				
				// Iterate over the world entries in the group to link them with the database;
				else {
					data.addGroup(group);
					
					for (String worldName:getConfig().getStringList(group)) {
						for (World world:getServer().getWorlds()) {
							if (!world.getName().equalsIgnoreCase(worldName)) continue;
							
							data.addWorld(world, group);
						}
					}
				}
			}
		}
		
		getConfig().addDefault("groups", "EMPTY");
		
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		this.getCommand("home").setExecutor(new HomeCommand(this));
		this.getCommand("h").setExecutor(new HomeCommand(this));
		this.getCommand("sethome").setExecutor(new SethomeCommand(this));
		this.getCommand("changehome").setExecutor(new ChangehomeCommand(this));
		this.getCommand("delhome").setExecutor(new DelhomeCommand(this));
		this.getCommand("homes").setExecutor(new HomesCommand(this));
		this.getCommand("visit").setExecutor(new VisitCommand(this));
	}
	
	@Override
	public void onDisable() {}
}
