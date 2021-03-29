package com.redeskyller.bukkit.solarymarket.commands.subcommands;

import static com.redeskyller.bukkit.solarymarket.SolaryMarket.database;

import java.sql.ResultSet;

import org.bukkit.command.CommandSender;

import com.redeskyller.bukkit.solarymarket.SolaryMarket;
import com.redeskyller.bukkit.solarymarket.commands.SubCommand;

public class SubCmdDespunir extends SubCommand {
	public SubCmdDespunir(String command)
	{
		super("despunir", "§cUse: /" + command + " despunir [jogador]", "solarymarket.command.despunir",
				new String[] { "desbanir", "unban" });
	}

	@Override
	public void execute(CommandSender sender, String[] args)
	{
		if (args.length >= 2)
			try {
				String target = args[1];
				String table = SolaryMarket.tableName.concat("_punicoes");
				long tempo = 0L;
				ResultSet result = database.query("SELECT * FROM " + table + " WHERE player='" + target + "';");
				if (result.next()) {
					tempo = result.getLong("tempo");
					if (System.currentTimeMillis() < tempo) {
						database.execute("DELETE FROM " + table + " WHERE player='" + target + "';");
						sender.sendMessage(SolaryMarket.mensagens.get("UNBAN_SUCESS").replace("{player}", target));
					} else
						sender.sendMessage(SolaryMarket.mensagens.get("PLAYER_NULL").replace("{player}", target));
				} else
					sender.sendMessage(SolaryMarket.mensagens.get("PLAYER_NULL").replace("{player}", target));
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		else
			sender.sendMessage(getUsage());
	}
}
