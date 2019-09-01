package nuvemplugins.solarymarket.commands.subcommands;

import java.sql.ResultSet;

import org.bukkit.command.CommandSender;

import nuvemplugins.solarymarket.app.SolaryMarket;
import nuvemplugins.solarymarket.commands.SubCommand;
import nuvemplugins.solarymarket.database.Database;

public class SubCmdDespunir extends SubCommand
{
	public SubCmdDespunir(String command) {
		super("despunir", "§cUse: /" + command + " despunir [jogador]", "solarymarket.command.despunir",
				new String[] { "desbanir", "unban" });
	}

	@Override
	public void execute(CommandSender sender, String[] args)
	{
		if (args.length >= 2) {
			try {
				String target = args[1];
				Database database = SolaryMarket.database;
				String table = SolaryMarket.table.concat("_punicoes");
				long tempo = 0L;
				database.open();
				ResultSet result = database.query("select * from " + table + " where player='" + target + "';");
				if (result.next()) {
					tempo = result.getLong("tempo");
					if (System.currentTimeMillis() < tempo) {
						database.execute("delete from " + table + " where player='" + target + "';");
						sender.sendMessage(SolaryMarket.mensagens.get("UNBAN_SUCESS").replace("{player}", target));
					} else {
						sender.sendMessage(SolaryMarket.mensagens.get("PLAYER_NULL").replace("{player}", target));
					}
				} else {
					sender.sendMessage(SolaryMarket.mensagens.get("PLAYER_NULL").replace("{player}", target));
				}
				database.close();
			} catch (Exception exception) {
				exception.printStackTrace();
			}

		} else {
			sender.sendMessage(this.getUsage());
		}
	}
}
