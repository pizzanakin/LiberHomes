package net.libercraft.liberhomes.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.libercraft.liberhomes.Main;

public class DelhomeCommand implements CommandExecutor {

	private Main plugin;
	
	public DelhomeCommand(Main plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		
		if (args.length < 1) return false;
		
		String group = plugin.getData().getGroup(player.getWorld());
		if (group == null) group = player.getWorld().getUID().toString();
		
		if (!plugin.getData().removeHome(player, args[0], group)) {
			player.sendMessage("That home does not exist.");
		}
		else {
			player.sendMessage("Home deleted.");
		}
		return true;
	}

}
