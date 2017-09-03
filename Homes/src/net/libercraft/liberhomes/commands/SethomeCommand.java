package net.libercraft.liberhomes.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.libercraft.liberhomes.Main;

public class SethomeCommand implements CommandExecutor {

	private Main plugin;
	
	public SethomeCommand(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		
		if (args.length < 1) return false;
		
		String group = plugin.getData().getGroup(player.getWorld());
		if (group == null) group = player.getWorld().getUID().toString();
		
		int result = plugin.getData().addHome(player, group, player.getLocation(), args[0]);
		switch (result) {
		case 0:
			player.sendMessage("That home already exists.");
			return true;
		case 1:
			player.sendMessage("You already have 3 homes. Change a current home using /changehome or delete a home using /delhome.");
			return true;
		case 2:
			player.sendMessage("Home set.");
			return true;
		default:
			return true;
		}
	}
}
