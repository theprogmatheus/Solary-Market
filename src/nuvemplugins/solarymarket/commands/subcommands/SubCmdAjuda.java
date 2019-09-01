package nuvemplugins.solarymarket.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nuvemplugins.solarymarket.commands.SubCommand;

public class SubCmdAjuda extends SubCommand
{
	public SubCmdAjuda(String command) {
		super("help", "§cUse: /" + command + " ajuda", "solarymarket.command.ajuda", new String[] { "ajuda", "?" });
	}

	@Override
	public void execute(CommandSender sender, String[] args)
	{
		sendHelp(sender);
	}

	public static void sendHelp(CommandSender sender)
	{
		if ((sender instanceof Player)) {
			sender.sendMessage(" ");
			sender.sendMessage("§a/mercado ajuda §8- §7para ver os comandos do mercado.");
			if (sender.hasPermission("solarymarket.command.ver")) {
				sender.sendMessage("§a/mercado ver §8- §7para ver os itens disponiveis no mercado.");
			}
			if (sender.hasPermission("solarymarket.command.vender")) {
				sender.sendMessage("§a/mercado vender [pre§o] §8- §7para vender um item no mercado.");
			}
			if (sender.hasPermission("solarymarket.command.coletar")) {
				sender.sendMessage("§a/mercado coletar §8- §7para coletar seus itens expirados.");
			}
			if (sender.hasPermission("solarymarket.command.punir")) {
				sender.sendMessage("§a/mercado punir §8- §7para punir um jogador do mercado.");
			}
			if (sender.hasPermission("solarymarket.command.despunir")) {
				sender.sendMessage("§a/mercado despunir §8- §7para despunir um jogador do mercado.");
			}
			if (sender.hasPermission("solarymarket.command.pessoal")) {
				sender.sendMessage("§a/mercado pessoal §8- §7para ver seu mercado pessoal.");
			}
			if (sender.hasPermission("solarymarket.command.reload")) {
				sender.sendMessage("§a/mercado reload §8- §7para recarregar as configs.");
			}
			sender.sendMessage(" ");
		} else {
			sender.sendMessage(" ");
			sender.sendMessage("§a/mercado ajuda §8- §7para ver os comandos do mercado.");
			sender.sendMessage("§a/mercado reload §8- §7para recarregar as configs.");
			sender.sendMessage("§a/mercado punir §8- §7para punir um jogador do mercado.");
			sender.sendMessage("§a/mercado despunir §8- §7para despunir um jogador do mercado.");
			sender.sendMessage(" ");
		}
	}
}
