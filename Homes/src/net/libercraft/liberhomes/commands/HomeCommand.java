package net.libercraft.liberhomes.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.libercraft.liberhomes.Main;

public class HomeCommand implements CommandExecutor {

	private Main plugin;
	
	public HomeCommand(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		
		if (args.length < 1) return false;
		
		// Retrieve the home from the database
		String group = plugin.getData().getGroup(player.getWorld());
		if (group == null) group = player.getWorld().getUID().toString();
		
		Location home = plugin.getData().getHome(player, args[0], group);
			
		// Make sure the home exists
		if (home == null) {
			player.sendMessage("Home not found.");
			return true;
		}
		
		// Teleport the player
		player.teleport(home);
		return true;
	}
}
