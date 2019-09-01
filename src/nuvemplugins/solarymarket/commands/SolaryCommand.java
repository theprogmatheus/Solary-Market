package nuvemplugins.solarymarket.commands;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import nuvemplugins.solarymarket.app.SolaryMarket;
import nuvemplugins.solarymarket.commands.subcommands.SubCmdAjuda;
import nuvemplugins.solarymarket.commands.subcommands.SubCmdColetar;
import nuvemplugins.solarymarket.commands.subcommands.SubCmdDespunir;
import nuvemplugins.solarymarket.commands.subcommands.SubCmdPessoal;
import nuvemplugins.solarymarket.commands.subcommands.SubCmdPunir;
import nuvemplugins.solarymarket.commands.subcommands.SubCmdReload;
import nuvemplugins.solarymarket.commands.subcommands.SubCmdVender;
import nuvemplugins.solarymarket.commands.subcommands.SubCmdVer;
import nuvemplugins.solarymarket.database.Database;
import nuvemplugins.solarymarket.util.StringUtils;

public class SolaryCommand implements CommandExecutor
{
	private List<SubCommand> subcommands;

	public SolaryCommand(String comando) {
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
			Database database = SolaryMarket.database;
			String table = SolaryMarket.table.concat("_punicoes");
			boolean banned = false;
			String staff = "";
			String motivo = "";
			long tempo = 0L;

			database.open();
			ResultSet result = database.query("select * from " + table + " where player='" + sender.getName() + "';");
			if (result.next()) {
				staff = result.getString("staff");
				motivo = result.getString("motivo");
				tempo = result.getLong("tempo");
				banned = System.currentTimeMillis() < tempo;
				if (!banned) {
					database.execute("delete from " + table + " where player='" + sender.getName() + "';");
				}
			}
			database.close();

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
				if (!this.subcommands.isEmpty()) {
					for (SubCommand subCommand : this.subcommands) {
						if ((arg.equalsIgnoreCase(subCommand.getName().toLowerCase()))
								|| (subCommand.getAlias().contains(arg))) {
							if ((sender.hasPermission(subCommand.getPermission()))
									|| (subCommand.getPermission().isEmpty())) {
								subCommand.execute(sender, args);
							} else {
								sender.sendMessage(SolaryMarket.mensagens.get("NO_PERMISSION"));
							}
							return false;
						}
					}
				}
			} else {
				SubCmdAjuda.sendHelp(sender);
			}
		} catch (Exception exception) {
			exception.printStackTrace();

		}
		return false;
	}
}
