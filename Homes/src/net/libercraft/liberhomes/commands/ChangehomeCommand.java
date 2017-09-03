package net.libercraft.liberhomes.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.libercraft.liberhomes.Main;

public class ChangehomeCommand implements CommandExecutor {

	private Main plugin;
	
	public ChangehomeCommand(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		
		if (args.length < 1) return false;
		
		String group = plugin.getData().getGroup(player.getWorld());
		if (group == null) group = player.getWorld().getUID().toString();
		
		if (!plugin.getData().changeHome(player, group, player.getLocation(), args[0])) {
			player.sendMessage("That home does not exist.");
		}
		else {
			player.sendMessage("Home changed.");
		}
		return true;
	}
}
