package com.redeskyller.bukkit.solarymarket.plugin.objetos;

import static com.redeskyller.bukkit.solarymarket.SolaryMarket.database;
import static com.redeskyller.bukkit.solarymarket.SolaryMarket.tableName;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.redeskyller.bukkit.solarymarket.SolaryMarket;
import com.redeskyller.bukkit.solarymarket.lib.itembuilder.ItemBuilder;
import com.redeskyller.bukkit.solarymarket.lib.nbt.NBTItem;
import com.redeskyller.bukkit.solarymarket.lib.nbt.NBTTagCompound;
import com.redeskyller.bukkit.solarymarket.util.Base64;
import com.redeskyller.bukkit.solarymarket.util.StringUtils;

public class Categoria implements org.bukkit.event.Listener {
	private ItemStack icone;
	private String id;
	private String name;
	private int slot;
	private boolean sellall;
	private String description;
	private List<String> itens;
	private List<String> filterNames;
	private List<String> filterLores;
	private List<String> filterEnchants;
	private Map<String, MapView> mapViews;

	public Categoria(ItemStack icone, String id, String name, int slot, boolean sellall)
	{
		this.icone = icone;
		this.id = id;
		this.name = name;
		this.slot = slot;
		this.sellall = sellall;
		this.description = "";
		this.itens = new ArrayList<>();
		this.filterNames = new ArrayList<>();
		this.filterLores = new ArrayList<>();
		this.filterEnchants = new ArrayList<>();
		this.mapViews = new java.util.HashMap<>();
	}

	public void register()
	{
		Bukkit.getPluginManager().registerEvents(this, SolaryMarket.getInstance());
	}

	public void unregister()
	{
		org.bukkit.event.HandlerList.unregisterAll(this);
	}

	public void expirar(String jogador, String uuid, boolean remove)
	{
		try {

			String mercado = tableName.concat("_mercado");
			String expirado = tableName.concat("_expirados");

			try (ResultSet resultSet = database.query("SELECT * FROM " + mercado + " WHERE uuid='" + uuid + "';")) {
				if (resultSet.next()) {

					String cache = resultSet.getString("cache");
					String player = resultSet.getString("player");

					database.execute(
							"INSERT INTO " + expirado + " VALUES ('" + player + "', '" + uuid + "', '" + cache + "');");

					database.execute("DELETE FROM " + mercado.concat(" WHERE uuid='").concat(uuid).concat("';"));

					Player target = Bukkit.getPlayer(player);
					if ((remove) && (target != null) && (!target.getName().equalsIgnoreCase(jogador)))
						target.sendMessage(SolaryMarket.mensagens.get("ITEM_REMOVE_TARGET"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void vizualizar(Player player)
	{
		String invname = StringUtils.removeColors(this.name);
		invname = invname.length() <= 10 ? invname : invname.substring(0, 10);
		MapView mapview = new MapView(this.icone.clone(), player, "§0§0§8Mercado - ".concat(invname));
		try {

			ResultSet result = database
					.query("SELECT * FROM " + SolaryMarket.tableName + "_mercado WHERE categoria='" + getId() + "';");
			while (result.next())
				try {
					String dono = result.getString("player");
					UUID uuid = UUID.fromString(result.getString("uuid"));
					double preco = result.getDouble("preco");
					long tempo = result.getLong("tempo");
					List<ItemStack> itens = Base64.fromBase64(result.getString("cache"));
					if (System.currentTimeMillis() < tempo) {
						ItemStack icone = null;
						if (itens.size() > 1)
							icone =

									new ItemBuilder(
											"54 1 name:&6Baú_De_Itens lore:&6 lore:&7Clique_com_o_botão_direito_para_ver_os_itens.")
													.toItemStack();
						else
							icone = itens.get(0).clone();
						icone = this.toProduto(player, icone, uuid, dono, preco);
						mapview.add(icone);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			if (getId().startsWith("PESSOAL:")) {
				ResultSet query = database.query("SELECT * FROM " + SolaryMarket.tableName + "_mercado WHERE player='"
						+ player.getName() + "';");
				while (query.next())
					try {
						String categoria = query.getString("categoria");
						if (categoria.startsWith("PESSOAL:")) {
							UUID uuid = UUID.fromString(query.getString("uuid"));
							double preco = query.getDouble("preco");
							long tempo = query.getLong("tempo");
							List<ItemStack> itens = Base64.fromBase64(query.getString("cache"));
							if (System.currentTimeMillis() < tempo) {
								ItemStack icone = null;
								if (itens.size() > 1)
									icone =

											new ItemBuilder(
													"54 1 name:&6Baú_De_Itens lore:&6 lore:&7Clique_com_o_botão_direito_para_ver_os_itens.")
															.toItemStack();
								else
									icone = itens.get(0).clone();
								icone = this.toProduto(player, icone, uuid, player.getName(), preco);
								mapview.add(icone);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		this.mapViews.put(player.getName(), mapview);
		mapview.visualizar();
	}

	public void addItem(String item)
	{
		if (!this.itens.contains(item))
			this.itens.add(item);
	}

	public ItemStack toItemStack(Player player, boolean contagem)
	{
		if (this.icone != null)
			try {
				ItemStack item = this.icone.clone();
				NBTItem itemnbt = new NBTItem(item);
				NBTTagCompound itemtag = itemnbt.getTag();
				NBTTagCompound newtag = new NBTTagCompound();
				newtag.setString("type", getId());
				itemtag.setCompound("huntersmarket", newtag);
				itemnbt.setTag(itemtag);
				item = itemnbt.getItem();
				ItemMeta meta = item.getItemMeta();
				List<String> newlore = new ArrayList<>();
				newlore.add("");
				if (!this.description.isEmpty()) {
					newlore.add(this.description.replace("&", "§").replace("{player}", player.getName()));
					newlore.add(" ");
				}
				String table = tableName.concat("_mercado");
				int quantidade = 0;

				try (ResultSet resultSet = database.query(
						"SELECT tempo FROM ".concat(table).concat(" WHERE categoria='").concat(this.id).concat("';"))) {
					while (resultSet.next())
						if (System.currentTimeMillis() < resultSet.getLong("tempo"))
							quantidade++;

				}

				newlore.add("§7Itens a venda: " + quantidade);

				if (meta.getDisplayName() == null)
					meta.setDisplayName(this.name.replace("&", "§"));
				meta.setLore(newlore);
				item.setItemMeta(meta);
				if (contagem)
					item.setAmount((quantidade >= 64) ? 64 : (quantidade > 1 ? quantidade : 1));
				return item;
			} catch (Exception e) {
				e.printStackTrace();
			}

		return null;
	}

	public boolean verify(ItemStack item)
	{

		if ((item == null) || (item.getType() == Material.AIR))
			return false;

		if ((this.itens.contains(String.valueOf((item.getTypeId() + ":" + item.getDurability()))))
				|| (this.itens.contains(String.valueOf(item.getTypeId())))) {

			ItemMeta meta = item.getItemMeta();

			if ((!this.filterNames.isEmpty()) && (meta.getDisplayName() != null))
				for (String filtername : this.filterNames)
					if (meta.getDisplayName().toLowerCase().contains(filtername.toLowerCase()))
						return false;

			if ((!this.filterLores.isEmpty()) && (meta.getLore() != null))
				for (String string : meta.getLore())
					for (String filterlores : this.filterLores)
						if (string.toLowerCase().contains(filterlores.toLowerCase()))
							return false;

			if ((!this.filterEnchants.isEmpty()) && (!item.getEnchantments().isEmpty()))
				for (Enchantment enchant : item.getEnchantments().keySet())
					for (String filterenchant : this.filterEnchants)
						if (enchant.getName().toLowerCase().contains(filterenchant.toLowerCase()))
							return false;

			return true;
		}
		return false;
	}

	private int getEspaco(Inventory inventory)
	{
		int amount = 0;
		int size = inventory.getSize();
		ItemStack[] arrayOfItemStack;
		int j = (arrayOfItemStack = inventory.getContents()).length;
		for (int i = 0; i < j; i++) {
			ItemStack item = arrayOfItemStack[i];
			if ((item != null) && (item.getType() != Material.AIR))
				amount++;
		}

		int espaco = size - amount;
		return espaco;
	}

	@org.bukkit.event.EventHandler
	public void onClick(InventoryClickEvent event)
	{
		String invname = StringUtils.removeColors(this.name);
		invname = invname.length() <= 10 ? invname : invname.substring(0, 10);

		if ((event.getWhoClicked() instanceof Player)) {
			Player player = (Player) event.getWhoClicked();
			if (event.getInventory().getTitle().startsWith("§0§0§8Mercado - ".concat(invname))) {
				event.setCancelled(true);
				if ((event.getCurrentItem() == null) || (event.getCurrentItem().getType() == Material.AIR))
					return;
				NBTItem itemnbt = new NBTItem(event.getCurrentItem());
				NBTTagCompound itemtag = itemnbt.getTag();
				NBTTagCompound hunterstag = itemtag.getCompound("huntersmarket");
				if (hunterstag.getNbtTag() != null) {
					MapView mapview = this.mapViews.get(player.getName());
					if (mapview != null) {
						String type = hunterstag.getString("type");
						if (type.equalsIgnoreCase("produto"))
							try (ResultSet resultSet = database.query("SELECT * FROM " + tableName
									+ "_mercado WHERE uuid='" + hunterstag.getString("uuid") + "';")) {

								if (resultSet.next()) {
									List<ItemStack> itens = Base64.fromBase64(resultSet.getString("cache"));
									String dono = resultSet.getString("player");
									double preco = resultSet.getDouble("preco");
									if (event.getClick() == org.bukkit.event.inventory.ClickType.RIGHT)
										vizualizarItens(player, itens, dono, preco,
												UUID.fromString(hunterstag.getString("uuid")));
									else if (player.getName().equals(dono)) {
										if (getEspaco(player.getInventory()) >= itens.size()) {
											database.execute("DELETE FROM " + tableName + "_mercado WHERE uuid='"
													+ hunterstag.getString("uuid") + "';");
											for (ItemStack item : itens)
												player.getInventory().addItem(new ItemStack[] { item });
											player.sendMessage(SolaryMarket.mensagens.get("COLLECT_SUCESS"));
											player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 1.0F, 1.0F);
											vizualizar(player);
										} else {
											player.sendMessage(SolaryMarket.mensagens.get("INVENTORY_FULL"));
											player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0F, 1.0F);
										}

									} else if (SolaryMarket.vault.getEconomy().getBalance(player) >= preco)
										confirmar(player, itens, dono, preco,
												UUID.fromString(hunterstag.getString("uuid")));
									else {
										player.sendMessage(SolaryMarket.mensagens.get("NO_MONEY"));
										player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0F, 1.0F);
									}
								} else {
									player.sendMessage(SolaryMarket.mensagens.get("ITEM_NOTFOUND"));
									player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0F, 1.0F);
									vizualizar(player);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						if (type.equalsIgnoreCase("back"))
							SolaryMarket.manager.mercado(player);
						if (type.equalsIgnoreCase("back-page"))
							mapview.backPage();
						if (type.equalsIgnoreCase("refresh"))
							vizualizar(player);
						if (type.equalsIgnoreCase("next-page"))
							mapview.nextPage();
						if (type.equalsIgnoreCase("expirado"))
							SolaryMarket.manager.expirados(player);
					}
				}
			} else if (event.getInventory().getTitle().startsWith("§3§3§8Detalhes - ".concat(invname))) {
				event.setCancelled(true);
				if ((event.getCurrentItem() == null) || (event.getCurrentItem().getType() == Material.AIR))
					return;
				NBTItem itemnbt = new NBTItem(event.getCurrentItem());
				NBTTagCompound itemtag = itemnbt.getTag();
				NBTTagCompound hunterstag = itemtag.getCompound("huntersmarket");

				if (hunterstag.getNbtTag() != null) {
					String type = hunterstag.getString("type");
					if (type.equalsIgnoreCase("back")) {
						vizualizar(player);
						return;
					}

					if (type.equalsIgnoreCase("remove")) {
						if (player.hasPermission("solarymarket.admin")) {
							expirar(player.getName(), hunterstag.getString("uuid"), true);

							vizualizar(player);
							player.sendMessage(SolaryMarket.mensagens.get("ITEM_REMOVE_SUCESS"));
							player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 1.0F, 1.0F);
						} else {
							vizualizar(player);
							player.sendMessage(SolaryMarket.mensagens.get("NO_PERMISSION"));
							player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0F, 1.0F);
						}
						return;
					}
				}
			}
		}
	}

	private ItemStack backIcon()
	{
		ItemStack item = new ItemStack(Material.ARROW);
		NBTItem itemnbt = new NBTItem(item);
		NBTTagCompound itemtag = itemnbt.getTag();
		NBTTagCompound newtag = new NBTTagCompound();
		newtag.setString("type", "back");
		itemtag.setCompound("huntersmarket", newtag);
		itemnbt.setTag(itemtag);
		item = itemnbt.getItem();

		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§6Voltar");
		meta.setLore(Arrays.asList(new String[] { "§7Clique para voltar a página anterior." }));
		item.setItemMeta(meta);
		return item;
	}

	private ItemStack removeIcon(UUID uuid)
	{
		ItemStack item = new ItemStack(Material.WOOL, 1, (short) 1);
		NBTItem itemnbt = new NBTItem(item);
		NBTTagCompound itemtag = itemnbt.getTag();
		NBTTagCompound newtag = new NBTTagCompound();
		newtag.setString("type", "remove");
		newtag.setString("uuid", uuid.toString());
		itemtag.setCompound("huntersmarket", newtag);
		itemnbt.setTag(itemtag);
		item = itemnbt.getItem();
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§6Remover");
		meta.setLore(Arrays.asList(new String[] { "§7Clique aqui para remover este item do mercado.",
				"§7Este item vai para os itens expirados do dono." }));
		item.setItemMeta(meta);
		return item;
	}

	private ItemStack toProduto(ItemStack icone, UUID uuid, String dono, double valor)
	{
		String prefix = SolaryMarket.vault.getPrefix(dono);
		NBTItem itemnbt = new NBTItem(icone);
		NBTTagCompound itemtag = itemnbt.getTag();
		NBTTagCompound newtag = new NBTTagCompound();
		List<String> lore = new ArrayList<>();
		newtag.setString("type", "produto");
		newtag.setString("uuid", uuid.toString());
		itemtag.setCompound("huntersmarket", newtag);
		itemnbt.setTag(itemtag);
		icone = itemnbt.getItem();
		ItemMeta meta = icone.getItemMeta();
		if (meta.getLore() != null)
			lore = meta.getLore();
		lore.add("");
		lore.add("§7Vendedor: §a" + prefix + dono);
		lore.add("§7Preço: §a" + SolaryMarket.currencyFormat(valor));
		meta.setLore(lore);
		icone.setItemMeta(meta);
		return icone;
	}

	private ItemStack toProduto(Player player, ItemStack icone, UUID uuid, String dono, double valor)
	{
		String prefix = SolaryMarket.vault.getPrefix(dono);
		NBTItem itemnbt = new NBTItem(icone);
		NBTTagCompound itemtag = itemnbt.getTag();
		NBTTagCompound newtag = new NBTTagCompound();
		List<String> lore = new ArrayList<>();
		newtag.setString("type", "produto");
		newtag.setString("uuid", uuid.toString());
		itemtag.setCompound("huntersmarket", newtag);
		itemnbt.setTag(itemtag);
		icone = itemnbt.getItem();
		ItemMeta meta = icone.getItemMeta();
		if (meta.getLore() != null)
			lore = meta.getLore();
		lore.add("");
		if (player.getName().equalsIgnoreCase(dono)) {
			lore.add("§7Vendedor: §a" + prefix + dono);
			lore.add("§7Preço: §a" + SolaryMarket.currencyFormat(valor));
			lore.add("");
			lore.add("§aClique aqui para coletar esse item.");
		} else {
			double saldo = SolaryMarket.vault.getEconomy().getBalance(player);
			if (saldo >= valor) {
				lore.add("§7Vendedor: §a" + prefix + dono);
				lore.add("§7Preço: §a" + SolaryMarket.currencyFormat(valor));
				lore.add("");
				lore.add("§aClique aqui para adquirir esse item.");
			} else {
				lore.add("§7Vendedor: §c" + prefix + dono);
				lore.add("§7Preço: §c" + SolaryMarket.currencyFormat(valor));
				lore.add("");
				lore.add("§cSaldo insuficiente.");
			}
		}

		meta.setLore(lore);
		icone.setItemMeta(meta);
		return icone;
	}

	private void vizualizarItens(Player player, List<ItemStack> itens, String dono, double valor, UUID uuid)
	{
		Inventory inventory = null;
		String invname = StringUtils.removeColors(this.name);
		if (itens.size() > 1) {
			inventory = Bukkit.createInventory(null, 54, "§3§3§8Detalhes - ".concat(invname));
			for (ItemStack item : itens)
				inventory.addItem(new ItemStack[] { item });
			inventory.setItem(45, backIcon());
			if (player.hasPermission("solarymarket.admin"))
				inventory.setItem(53, removeIcon(uuid));
		} else {
			inventory = Bukkit.createInventory(null, 36, "§3§3§8Detalhes - ".concat(invname));
			inventory.setItem(13, itens.get(0));
			inventory.setItem(27, backIcon());
			if (player.hasPermission("solarymarket.admin"))
				inventory.setItem(35, removeIcon(uuid));
		}
		player.openInventory(inventory);
	}

	private void confirmar(Player player, List<ItemStack> itens, String dono, double valor, UUID uuid)
	{
		Inventory inventory = Bukkit.createInventory(null, 36, "§3§3§8Confirmar Compra");
		if (itens.size() > 1)
			inventory.setItem(13,
					this.toProduto(new ItemBuilder("54 1 name:&6Baú_De_Itens").toItemStack(), uuid, dono, valor));
		else
			inventory.setItem(13, this.toProduto(itens.get(0), uuid, dono, valor));
		ItemStack yes = new ItemBuilder("35:5 1 name:&aComprar_Item lore:&7Clique_para_comprar_este_item.")
				.toItemStack();
		ItemStack no = new ItemBuilder("35:14 1 name:&cCancelar lore:&7Clique_para_cancelar_esta_operação.")
				.toItemStack();

		NBTItem yesnbt = new NBTItem(yes);
		NBTTagCompound yestag = yesnbt.getTag();
		NBTTagCompound newyestag = new NBTTagCompound();
		newyestag.setString("type", "confirmar");
		newyestag.setString("uuid", uuid.toString());
		yestag.setCompound("huntersmarket", newyestag);
		yesnbt.setTag(yestag);
		yes = yesnbt.getItem();

		NBTItem nonbt = new NBTItem(no);
		NBTTagCompound notag = nonbt.getTag();
		NBTTagCompound newnotag = new NBTTagCompound();
		newnotag.setString("type", "cancelar");
		newnotag.setString("uuid", uuid.toString());
		notag.setCompound("huntersmarket", newnotag);
		nonbt.setTag(notag);
		no = nonbt.getItem();
		inventory.setItem(24, no);
		inventory.setItem(20, yes);
		player.openInventory(inventory);
	}

	public String getDescription()
	{
		return this.description;
	}

	public void setDescription(String description)
	{
		if (description == null)
			this.description = "";
		this.description = description;
	}

	public ItemStack getIcone()
	{
		return this.icone;
	}

	public void setIcone(ItemStack icone)
	{
		this.icone = icone;
	}

	public String getId()
	{
		return this.id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getSlot()
	{
		return this.slot;
	}

	public void setSlot(int slot)
	{
		this.slot = slot;
	}

	public boolean isSellall()
	{
		return this.sellall;
	}

	public void setSellall(boolean sellall)
	{
		this.sellall = sellall;
	}

	public List<String> getItens()
	{
		return this.itens;
	}

	public void setItens(List<String> itens)
	{
		this.itens = itens;
	}

	public List<String> getFilterNames()
	{
		return this.filterNames;
	}

	public void setFilterNames(List<String> filterNames)
	{
		this.filterNames = filterNames;
	}

	public List<String> getFilterLores()
	{
		return this.filterLores;
	}

	public void setFilterLores(List<String> filterLores)
	{
		this.filterLores = filterLores;
	}

	public List<String> getFilterEnchants()
	{
		return this.filterEnchants;
	}

	public void setFilterEnchants(List<String> filterEnchants)
	{
		this.filterEnchants = filterEnchants;
	}
}
