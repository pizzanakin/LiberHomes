package net.libercraft.liberhomes.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.libercraft.liberhomes.Main;

public class HomesCommand implements CommandExecutor {

	private Main plugin;
	
	public HomesCommand(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		
		String group = plugin.getData().getGroup(player.getWorld());
		if (group == null) group = player.getWorld().getUID().toString();
		
		if (args.length == 0) 
			player.sendMessage("Your current homes are: " + plugin.getData().getHomeNames(player, group));
		else 
			player.sendMessage(args[0] + "'s homes are: " + plugin.getData().getHomeNames(plugin.getServer().getPlayer(args[0]), group));
		return true;
	}
}
