package com.redeskyller.bukkit.solarymarket.commands;

import static com.redeskyller.bukkit.solarymarket.SolaryMarket.database;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.redeskyller.bukkit.solarymarket.SolaryMarket;
import com.redeskyller.bukkit.solarymarket.commands.subcommands.SubCmdAjuda;
import com.redeskyller.bukkit.solarymarket.commands.subcommands.SubCmdColetar;
import com.redeskyller.bukkit.solarymarket.commands.subcommands.SubCmdDespunir;
import com.redeskyller.bukkit.solarymarket.commands.subcommands.SubCmdPessoal;
import com.redeskyller.bukkit.solarymarket.commands.subcommands.SubCmdPunir;
import com.redeskyller.bukkit.solarymarket.commands.subcommands.SubCmdReload;
import com.redeskyller.bukkit.solarymarket.commands.subcommands.SubCmdVender;
import com.redeskyller.bukkit.solarymarket.commands.subcommands.SubCmdVer;
import com.redeskyller.bukkit.solarymarket.util.StringUtils;

public class SolaryCommand implements CommandExecutor {
	private List<SubCommand> subcommands;

	public SolaryCommand(String comando)
	{
		this.subcommands = new ArrayList<>();
		this.subcommands.add(new SubCmdAjuda(comando));
		this.subcommands.add(new SubCmdVer(comando));
		this.subcommands.add(new SubCmdColetar(comando));
		this.subcommands.add(new SubCmdPessoal(comando));
		this.subcommands.add(new SubCmdVender(comando));
		this.subcommands.add(new SubCmdReload(comando));
		this.subcommands.add(new SubCmdPunir(comando));
		this.subcommands.add(new SubCmdDespunir(comando));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		try {
			String table = SolaryMarket.tableName.concat("_punicoes");
			boolean banned = false;
			String staff = "";
			String motivo = "";
			long tempo = 0L;

			ResultSet result = database.query("SELECT * FROM " + table + " WHERE player='" + sender.getName() + "';");
			if (result.next()) {
				staff = result.getString("staff");
				motivo = result.getString("motivo");
				tempo = result.getLong("tempo");
				banned = System.currentTimeMillis() < tempo;
				if (!banned)
					database.execute("DELETE FROM " + table + " WHERE player='" + sender.getName() + "';");
			}

			if (banned) {
				sender.sendMessage("");
				sender.sendMessage(SolaryMarket.mensagens.get("BAN_MESSAGE"));
				sender.sendMessage("");
				sender.sendMessage("§cMotivo: " + motivo);
				sender.sendMessage("§cStaffer: " + staff);
				sender.sendMessage("§cTempo Restante: " + StringUtils.formatDelay(tempo - System.currentTimeMillis()));
				sender.sendMessage("");
				return false;
			}
			if (args.length >= 1) {
				String arg = args[0].toLowerCase();
				if (!this.subcommands.isEmpty())
					for (SubCommand subCommand : this.subcommands)
						if ((arg.equalsIgnoreCase(subCommand.getName().toLowerCase()))
								|| (subCommand.getAlias().contains(arg))) {
							if ((sender.hasPermission(subCommand.getPermission()))
									|| (subCommand.getPermission().isEmpty()))
								subCommand.execute(sender, args);
							else
								sender.sendMessage(SolaryMarket.mensagens.get("NO_PERMISSION"));
							return false;
						}
			} else
				SubCmdAjuda.sendHelp(sender);
		} catch (Exception exception) {
			exception.printStackTrace();

		}
		return false;
	}
}
