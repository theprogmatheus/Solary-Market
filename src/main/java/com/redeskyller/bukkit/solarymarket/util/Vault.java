package com.redeskyller.bukkit.solarymarket.util;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;

public class Vault {

	private Economy economy;
	private Chat chat;

	public boolean setupEconomy()
	{
		RegisteredServiceProvider<Economy> service = Bukkit.getServicesManager().getRegistration(Economy.class);
		if (service != null)
			this.economy = (service.getProvider());
		return this.economy != null;
	}

	public boolean setupChat()
	{
		RegisteredServiceProvider<Chat> service = Bukkit.getServicesManager().getRegistration(Chat.class);
		if (service != null)
			this.chat = (service.getProvider());
		return this.chat != null;
	}

	public Economy getEconomy()
	{
		return this.economy;
	}

	public String getPrefix(String jogador)
	{
		String prefix = "";

		if (this.chat != null) {
			Player player = Bukkit.getPlayer(jogador);
			if (player != null) {
				World world = player.getWorld();

				prefix = this.chat.getGroupPrefix(world, this.chat.getPrimaryGroup(world.getName(), player));
			}
		}

		return prefix.replace("&", "§");
	}

}
