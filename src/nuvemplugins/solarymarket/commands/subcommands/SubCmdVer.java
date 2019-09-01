package nuvemplugins.solarymarket.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nuvemplugins.solarymarket.app.SolaryMarket;
import nuvemplugins.solarymarket.plugin.objetos.Categoria;

public class SubCmdVer extends nuvemplugins.solarymarket.commands.SubCommand
{
	public SubCmdVer(String command) {
		super("ver", "§cUse: /" + command + " ver", "solarymarket.command.ver", new String[] { "view", "visualizar" });
	}

	@Override
	public void execute(CommandSender sender, String[] args)
	{
		if ((sender instanceof Player)) {
			Player player = (Player) sender;
			if (args.length >= 2) {
				String name = args[1].toLowerCase();
				Categoria categoria = SolaryMarket.manager.getCategorias().get(name.toLowerCase());
				if (categoria != null) {
					categoria.vizualizar(player);
				} else {
					SolaryMarket.manager.mercado(player);
				}
			} else {
				SolaryMarket.manager.mercado(player);
			}
		} else {
			sender.sendMessage("§cEste recurso esta disponivel somente para jogadores em jogo.");
		}
	}
}
