package com.redeskyller.bukkit.solarymarket.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.redeskyller.bukkit.solarymarket.commands.SubCommand;

public class SubCmdColetar extends SubCommand {
	public SubCmdColetar(String command)
	{
		super("coletar", "§cUse: /" + command + " coletar", "solarymarket.command.coletar",
				new String[] { "expirados" });
	}

	@Override
	public void execute(CommandSender sender, String[] args)
	{
		if ((sender instanceof Player)) {
			Player player = (Player) sender;
			com.redeskyller.bukkit.solarymarket.SolaryMarket.manager.expirados(player);
		} else
			sender.sendMessage("§cEste recurso esta disponivel somente para jogadores em jogo.");
	}
}
