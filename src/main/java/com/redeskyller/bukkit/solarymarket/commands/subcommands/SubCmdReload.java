package com.redeskyller.bukkit.solarymarket.commands.subcommands;

import org.bukkit.command.CommandSender;

import com.redeskyller.bukkit.solarymarket.SolaryMarket;
import com.redeskyller.bukkit.solarymarket.commands.SubCommand;

public class SubCmdReload extends SubCommand {
	public SubCmdReload(String command)
	{
		super("reload", "§cUse: /" + command + " reload", "solarymarket.command.reload", new String[] { "rl" });
	}

	@Override
	public void execute(CommandSender sender, String[] args)
	{
		SolaryMarket.config.load();
		SolaryMarket.mensagens.load();
		SolaryMarket.manager.loadCategorias();
		sender.sendMessage("§aCategorias e mensagens recarregadas com sucesso.");
	}
}
