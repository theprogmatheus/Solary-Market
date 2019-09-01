package nuvemplugins.solarymarket.commands.subcommands;

import java.sql.ResultSet;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nuvemplugins.solarymarket.app.SolaryMarket;
import nuvemplugins.solarymarket.commands.SubCommand;
import nuvemplugins.solarymarket.database.Database;
import nuvemplugins.solarymarket.util.StringUtils;

public class SubCmdPunir extends SubCommand
{
	public SubCmdPunir(String command) {
		super("punir", "§cUse: /" + command + " punir [jogador] [tempo] [motivo]", "solarymarket.command.punir",
				new String[] { "banir", "ban" });
	}

	@Override
	public void execute(CommandSender sender, String[] args)
	{
		if (args.length >= 4) {
			try {
				Database database = SolaryMarket.database;
				String table = SolaryMarket.table.concat("_punicoes");

				Player target = Bukkit.getPlayer(args[1]);
				if (target == null) {
					sender.sendMessage(SolaryMarket.mensagens.get("PLAYER_NULL").replace("{player}", args[1]));
					return;
				}

				long tempo = this.getTempo(args[2]);
				if (tempo <= 0L) {
					sender.sendMessage(SolaryMarket.mensagens.get("BAN_ERRO_NUMBER"));
					return;
				}
				String motivo = "";
				for (int i = 3; i < args.length; i++) {
					motivo = motivo + args[i];
					motivo = motivo + " ";
				}
				if (motivo.length() >= 100) {
					motivo = motivo.substring(0, 100);
				}
				String staff = sender.getName();
				if (staff.equalsIgnoreCase("CONSOLE")) {
					staff = "Console";
				}

				database.open();
				ResultSet result = database
						.query("select * from " + table + " where player='" + target.getName() + "';");
				if (result.next()) {
					String result_staff = result.getString("staff");
					long result_tempo = result.getLong("tempo");
					String result_motivo = result.getString("motivo");

					if (System.currentTimeMillis() >= result_tempo) {
						database.execute("update " + table + " set tempo='" + (System.currentTimeMillis() + tempo)
								+ "' where player='" + target.getName() + "';");

						sender.sendMessage("");
						sender.sendMessage(
								SolaryMarket.mensagens.get("BAN_SUCESS").replace("{player}", target.getName()));
						sender.sendMessage("");
						sender.sendMessage("§cMotivo: " + motivo);
						sender.sendMessage("§cTempo: " + StringUtils.formatDelay(tempo));
						sender.sendMessage("");
						if (sender != target) {
							target.sendMessage("");
							target.sendMessage(SolaryMarket.mensagens.get("BAN_MESSAGE"));
							target.sendMessage("");
							target.sendMessage("§cMotivo: " + motivo);
							target.sendMessage("§cStaffer: " + staff);
							target.sendMessage("§cTempo: " + StringUtils.formatDelay(tempo));
							target.sendMessage("");
						}
					} else {
						sender.sendMessage("");
						sender.sendMessage(SolaryMarket.mensagens.get("BAN_ERRO_ISBAN"));
						sender.sendMessage("");
						sender.sendMessage("§cMotivo: " + result_motivo);
						sender.sendMessage("§cStaffer: " + result_staff);
						sender.sendMessage("§cTempo Restante: "
								+ StringUtils.formatDelay(result_tempo - System.currentTimeMillis()));
						sender.sendMessage("");
					}
				} else {
					database.execute("insert into " + table + " values ('" + target.getName() + "', '" + staff + "', '"
							+ (System.currentTimeMillis() + tempo) + "', '" + motivo + "');");
					sender.sendMessage("");
					sender.sendMessage(SolaryMarket.mensagens.get("BAN_SUCCESS").replace("{player}", target.getName()));
					sender.sendMessage("");
					sender.sendMessage("§cMotivo: " + motivo);
					sender.sendMessage("§cTempo: " + StringUtils.formatDelay(tempo));
					sender.sendMessage("");
					if (sender != target) {
						target.sendMessage("");
						target.sendMessage(SolaryMarket.mensagens.get("BAN_MESSAGE"));
						target.sendMessage("");
						target.sendMessage("§cMotivo: " + motivo);
						target.sendMessage("§cStaffer: " + staff);
						target.sendMessage("§cTempo: " + StringUtils.formatDelay(tempo));
						target.sendMessage("");
					}
				}

				database.close();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
		sender.sendMessage(this.getUsage());
	}

	public long getTempo(String tempo)
	{
		tempo = tempo.toLowerCase();
		if (tempo.contains("nan")) {
			tempo = "1";
		}
		long i = -1L;
		try {
			i = 60000L + Long.parseLong(tempo);
		} catch (Exception exceptionminutos) {
			if ((tempo.endsWith("d")) || (tempo.endsWith("dias")) || (tempo.endsWith("dia"))) {
				try {
					i = Long.parseLong(tempo.replace("d", "").replace("dia", "").replace("dias", "")) * 86400000L;
				} catch (Exception exception) {
					i = -1L;
				}
			}

			if ((tempo.endsWith("h")) || (tempo.endsWith("horas")) || (tempo.endsWith("hora"))) {
				try {
					i = Long.parseLong(tempo.replace("h", "").replace("horas", "").replace("hora", "")) * 3600000L;
				} catch (Exception exception) {
					i = -1L;
				}
			}
			if ((tempo.endsWith("m")) || (tempo.endsWith("minutos")) || (tempo.endsWith("minuto"))) {
				try {
					i = Long.parseLong(tempo.replace("m", "").replace("minutos", "").replace("minuto", "")) * 60000L;
				} catch (Exception exception) {
					i = -1L;
				}
			}
			if ((tempo.endsWith("s")) || (tempo.endsWith("segundos")) || (tempo.endsWith("segundo"))) {
				try {
					i = Long.parseLong(tempo.replace("s", "").replace("segundos", "").replace("segundo", "")) * 1000L;
				} catch (Exception exception) {
					i = -1L;
				}
			}
		}

		return i;
	}
}
