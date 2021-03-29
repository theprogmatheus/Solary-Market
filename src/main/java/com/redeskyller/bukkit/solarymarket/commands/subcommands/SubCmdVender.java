package com.redeskyller.bukkit.solarymarket.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.redeskyller.bukkit.solarymarket.SolaryMarket;
import com.redeskyller.bukkit.solarymarket.commands.SubCommand;

public class SubCmdVender extends SubCommand {
	public SubCmdVender(String command)
	{
		super("vender", "§cUse: /" + command + " vender [pre§o] [jogador]", "solarymarket.command.vender",
				new String[] { "sell" });
	}

	@Override
	public void execute(CommandSender sender, String[] args)
	{
		if ((sender instanceof Player)) {
			Player player = (Player) sender;
			if (args.length >= 2) {
				org.bukkit.inventory.ItemStack item = player.getItemInHand();
				double valor = -1.0D;
				boolean vendertudo = false;
				String target = null;
				try {
					valor = Double.valueOf(args[1]).doubleValue();
				} catch (Exception localException) {
				}
				if (args.length >= 3)
					if ((args[2].equalsIgnoreCase("all")) || (args[2].equalsIgnoreCase("tudo")))
						vendertudo = true;
					else
						target = args[2];
				if (args.length >= 4)
					try {
						if ((args[3].equalsIgnoreCase("all")) || (args[2].equalsIgnoreCase("tudo")))
							vendertudo = true;
					} catch (Exception localException1) {
					}

				if (SolaryMarket.canSell(player)) {
					if (target != null)
						SolaryMarket.manager.vender(player, target, valor, item, vendertudo);
					else
						SolaryMarket.manager.vender(player, valor, item, vendertudo);
				} else
					player.sendMessage(SolaryMarket.mensagens.get("MAX_ITENS"));
			} else
				sender.sendMessage(getUsage());
		} else
			sender.sendMessage("§cEste recurso esta disponivel somente para jogadores em jogo.");
	}
}
