package nuvemplugins.solarymarket.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nuvemplugins.solarymarket.commands.SubCommand;

public class SubCmdPessoal extends SubCommand
{
	public SubCmdPessoal(String command) {
		super("pessoal", "§cUse: /" + command + " pessoal", "solarymarket.command.pessoal",
				new String[] { "particular" });
	}

	@Override
	public void execute(CommandSender sender, String[] args)
	{
		if ((sender instanceof Player)) {
			Player player = (Player) sender;
			nuvemplugins.solarymarket.app.SolaryMarket.manager.pessoal(player);
		} else {
			sender.sendMessage("§cEste recurso esta disponivel somente para jogadores em jogo.");
		}
	}
}
