package net.libercraft.liberhomes.commands;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.libercraft.liberhomes.Main;

public class VisitCommand implements CommandExecutor {

	private Main plugin;
	
	public VisitCommand(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		
		if (args.length < 2) return false;
		
		String group = plugin.getData().getGroup(player.getWorld());
		if (group == null) group = player.getWorld().getUID().toString();
		
		// Retrieve the home from the database
		Location home = null;
		
		for (OfflinePlayer op:plugin.getServer().getOfflinePlayers()) {
			if (op.isOnline()) continue;
			if (op.getName().equals(args[0]))
				home = plugin.getData().getHome(op.getPlayer(), args[1], group);
		}
		
		for (Player p:plugin.getServer().getOnlinePlayers()) {
			if (p.getName().equals(args[0]))
				home = plugin.getData().getHome(p, args[1], group);
		}
		
		
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
