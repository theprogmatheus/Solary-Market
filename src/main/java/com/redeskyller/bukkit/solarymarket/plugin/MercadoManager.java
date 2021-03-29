package com.redeskyller.bukkit.solarymarket.plugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.redeskyller.bukkit.solarymarket.app.SolaryMarket;
import com.redeskyller.bukkit.solarymarket.database.Database;
import com.redeskyller.bukkit.solarymarket.lib.fanciful.FancyMessage;
import com.redeskyller.bukkit.solarymarket.lib.itembuilder.ItemBuilder;
import com.redeskyller.bukkit.solarymarket.lib.nbt.NBTItem;
import com.redeskyller.bukkit.solarymarket.lib.nbt.NBTTagCompound;
import com.redeskyller.bukkit.solarymarket.plugin.objetos.Categoria;
import com.redeskyller.bukkit.solarymarket.util.Base64;

public class MercadoManager implements org.bukkit.event.Listener {
	private Map<String, Categoria> categorias;
	private Map<String, Categoria> categoriasPessoais;
	private double precomax;
	private Map<String, Long> broadcastDelays;

	public MercadoManager()
	{
		YamlConfiguration config = SolaryMarket.config.getYaml();
		this.categorias = new HashMap<>();
		this.categoriasPessoais = new HashMap<>();
		this.precomax = config.getDouble("preco_max");
		this.broadcastDelays = new HashMap<>();
		Bukkit.getPluginManager().registerEvents(this, SolaryMarket.instance);
		loadCategorias();
	}

	private List<String> toList(String string)
	{
		List<String> list = new ArrayList<>();
		if ((string != null) && (!string.isEmpty())) {
			string = string.replace("&", "§");
			if (string.contains(", ")) {
				String[] split = string.split(", ");
				String[] arrayOfString1;
				int j = (arrayOfString1 = split).length;
				for (int i = 0; i < j; i++) {
					String s = arrayOfString1[i];
					list.add(s);
				}
			} else
				list.add(string);
		}
		return list;
	}

	public void loadCategorias()
	{
		YamlConfiguration config = SolaryMarket.config.getYaml();

		if (!this.categorias.isEmpty())
			for (Categoria c : this.categorias.values())
				c.unregister();
		for (String id : config.getConfigurationSection("categorias").getKeys(false)) {
			String path = "categorias." + id + ".";
			String name = config.getString(path.concat("name")).replace("&", "§");
			int slot = config.getInt(path.concat("slot"));
			ItemStack icone = new ItemBuilder(config.getString(path.concat("icone")).replace("&", "§")).toItemStack();
			boolean sellall = config.getBoolean(path.concat("sellall"));
			List<String> itens = toList(config.getString(path.concat("itens")));
			List<String> filternames = toList(config.getString(path.concat("filternames")));
			List<String> filterlores = toList(config.getString(path.concat("filterlores")));
			List<String> filterenchants = toList(config.getString(path.concat("filterenchants")));
			String description = config.getString(path.concat("description"));
			if (description != null)
				description.replace("&", "§");
			Categoria categoria = new Categoria(icone, id, name, slot, sellall);
			if (description != null)
				categoria.setDescription(description);
			if (!itens.isEmpty())
				for (String items : itens)
					categoria.addItem(items);
			categoria.setFilterNames(filternames);
			categoria.setFilterLores(filterlores);
			categoria.setFilterEnchants(filterenchants);

			categoria.register();
			this.categorias.put(id.toLowerCase(), categoria);
		}
	}

	public void vender(Player player, double valor, ItemStack item, boolean vendertudo)
	{
		if ((valor <= 0.0D) || (valor > this.precomax)) {
			player.sendMessage(SolaryMarket.mensagens.get("NUMBER_NULL").replace("{valor_max}",
					SolaryMarket.numberFormat(this.precomax)));
			return;
		}
		if ((item == null) || (item.getType() == Material.AIR)) {
			player.sendMessage(SolaryMarket.mensagens.get("ITEM_NULL"));
			return;
		}

		YamlConfiguration config = SolaryMarket.config.getYaml();
		Database db = SolaryMarket.database;
		String table = SolaryMarket.table.concat("_mercado");
		long tempo = System.currentTimeMillis() + (config.getLong("tempo") * 1000L);
		List<ItemStack> itens = new ArrayList<>();
		String categoria_id = null;
		for (Categoria categoria : this.categorias.values())
			if (categoria.verify(item)) {
				if (vendertudo) {
					if (!categoria.isSellall()) {
						player.sendMessage(SolaryMarket.mensagens.get("ITEM_NOCOMP2"));
						return;
					}
					int slot = 0;
					ItemStack hand = player.getItemInHand();
					if (hand.equals(item))
						for (int i = 0; i < player.getInventory().getContents().length; i++) {
							ItemStack contents = player.getInventory().getItem(i);
							if ((contents != null) && (this.equals(item, contents))) {
								player.getInventory().setItem(slot, new ItemStack(Material.AIR));
								itens.add(contents);
							}
							slot++;
						}
				} else {
					ItemStack hand = player.getItemInHand();
					if (hand.equals(item)) {
						player.setItemInHand(new ItemStack(Material.AIR));
						itens.add(item);
					}
				}
				categoria_id = categoria.getId();
				break;
			}

		if (categoria_id == null) {
			player.sendMessage(SolaryMarket.mensagens.get("ITEM_NOCOMP"));
			return;
		}
		if (itens.isEmpty()) {
			player.sendMessage(SolaryMarket.mensagens.get("SELL_ERROR"));
			return;
		}

		db.open();
		db.execute("insert into ".concat(table).concat(" values ('").concat(player.getName()).concat("', '")
				.concat(categoria_id).concat("', '").concat(UUID.randomUUID().toString()).concat("', '")
				.concat(String.valueOf(valor)).concat("', '").concat(String.valueOf(tempo)).concat("', '")
				.concat(Base64.toBase64(itens)).concat("');"));
		db.close();
		player.sendMessage(
				SolaryMarket.mensagens.get("SELL_SUCESS").replace("{valor}", SolaryMarket.numberFormat(valor)));

		if (config.getBoolean("broadcast.enable")) {
			long delay = config.getLong("broadcast.delay");
			Long lastdelay = this.broadcastDelays.get(player.getName());
			if ((lastdelay != null) && (System.currentTimeMillis() < lastdelay.longValue()))
				return;
			if ((valor >= config.getDouble("broadcast.valor")) && (player.hasPermission("solarymarket.broadcast"))) {
				this.broadcastDelays.put(player.getName(), Long.valueOf(System.currentTimeMillis() + (1000L * delay)));
				FancyMessage msg = null;
				if (itens.size() > 1) {
					msg = new FancyMessage("§l[Mercado] ");
					msg.color(ChatColor.GREEN)
							.then(player.getName() + " colocou um baú de itens no mercado por "
									+ SolaryMarket.numberFormat(valor) + " coins.")
							.color(ChatColor.GRAY)
							.itemTooltip(new ItemBuilder("54 1 name:&6Baú_De_Itens").toItemStack())
							.command("/mercado ver " + categoria_id);
				} else {
					msg = new FancyMessage("§l[Mercado] ");
					msg.color(ChatColor.GREEN)
							.then(player.getName() + " colocou um item no mercado por "
									+ SolaryMarket.numberFormat(valor) + " coins.")
							.color(ChatColor.GRAY).itemTooltip(item).command("/mercado ver " + categoria_id);
				}

				for (World world : Bukkit.getWorlds())
					for (Player targetPlayer : world.getPlayers())
						msg.send(targetPlayer);

			}
		}
	}

	public void vender(Player player, String target, double valor, ItemStack item, boolean vendertudo)
	{
		if ((valor <= 0.0D) || (valor > this.precomax)) {
			player.sendMessage(SolaryMarket.mensagens.get("NUMBER_NULL").replace("{valor_max}",
					SolaryMarket.numberFormat(this.precomax)));
			return;
		}
		if ((item == null) || (item.getType() == Material.AIR)) {
			player.sendMessage(SolaryMarket.mensagens.get("ITEM_NULL"));
			return;
		}
		if (player.getName().equalsIgnoreCase(target)) {
			player.sendMessage(SolaryMarket.mensagens.get("SELL_ERRO_PESSOAL"));
			return;
		}
		Categoria pessoal = this.categoriasPessoais.get(target);
		if (Bukkit.getPlayer(target) != null) {
			loadPessoal(Bukkit.getPlayer(target));
			pessoal = this.categoriasPessoais.get(target);
		}
		if (pessoal != null) {
			YamlConfiguration config = SolaryMarket.config.getYaml();
			Database db = SolaryMarket.database;
			String table = SolaryMarket.table.concat("_mercado");
			long tempo = System.currentTimeMillis() + (config.getLong("tempo") * 1000L);
			List<ItemStack> itens = new ArrayList<>();
			String categoria_id = null;
			for (Categoria categoria : this.categorias.values())
				if (categoria.verify(item)) {
					if (vendertudo) {
						if (!categoria.isSellall()) {
							player.sendMessage(SolaryMarket.mensagens.get("ITEM_NOCOMP2"));
							return;
						}
						int slot = 0;
						ItemStack hand = player.getItemInHand();
						if (hand.equals(item))
							for (int i = 0; i < player.getInventory().getContents().length; i++) {
								ItemStack contents = player.getInventory().getItem(i);
								if ((contents != null) && (this.equals(item, contents))) {
									player.getInventory().setItem(slot, new ItemStack(Material.AIR));
									itens.add(contents);
								}
								slot++;
							}
					} else {
						ItemStack hand = player.getItemInHand();
						if (hand.equals(item)) {
							player.setItemInHand(new ItemStack(Material.AIR));
							itens.add(item);
						}
					}
					categoria_id = pessoal.getId();
					break;
				}
			if (categoria_id == null) {
				player.sendMessage(SolaryMarket.mensagens.get("ITEM_NOCOMP"));
				return;
			}
			if (itens.isEmpty())
				return;

			db.open();
			db.execute("insert into ".concat(table).concat(" values ('").concat(player.getName()).concat("', '")
					.concat(categoria_id).concat("', '").concat(UUID.randomUUID().toString()).concat("', '")
					.concat(String.valueOf(valor)).concat("', '").concat(String.valueOf(tempo)).concat("', '")
					.concat(Base64.toBase64(itens)).concat("');"));
			db.close();
			player.sendMessage(SolaryMarket.mensagens.get("SELL_SUCESS_PESSOAL")
					.replace("{valor}", SolaryMarket.numberFormat(valor)).replace("{player}", target));
		} else
			player.sendMessage(SolaryMarket.mensagens.get("PLAYER_NULL").replace("{player}", target));
	}

	private boolean equals(ItemStack i, ItemStack o)
	{
		i = i.clone();
		i.setAmount(1);
		o = o.clone();
		o.setAmount(1);
		return i.equals(o);
	}

	public void mercado(Player player)
	{
		YamlConfiguration config = SolaryMarket.config.getYaml();
		int menurows = config.getInt("menu.rows");
		int pessoalslot = config.getInt("menu.pessoalslot");
		int expiradoslot = config.getInt("menu.expiradoslot");
		boolean contagem = config.getBoolean("contagem");
		Inventory inventory = Bukkit.createInventory(null, 9 * menurows, "§0§0§0§8Mercado");
		if (!this.categorias.isEmpty())
			for (Categoria categoria : this.categorias.values())
				try {
					if (categoria != null)
						inventory.setItem(categoria.getSlot() - 1, categoria.toItemStack(player, contagem));
				} catch (Exception exception) {
					exception.printStackTrace();
				}
		ItemStack pessoal = load(player, "pessoal");
		ItemStack expirado = load(player, "expirado");
		inventory.setItem(expiradoslot - 1, expirado);
		inventory.setItem(pessoalslot - 1, pessoal);
		player.openInventory(inventory);
	}

	public int expiradosSize(Player player)
	{
		int size = 0;
		try {
			Database database = SolaryMarket.database;
			database.open();
			ResultSet result = database.query(
					"select * from " + SolaryMarket.table + "_expirados where player='" + player.getName() + "';");
			while (result.next())
				size++;
			result = database
					.query("select * from " + SolaryMarket.table + "_mercado where player='" + player.getName() + "';");
			while (result.next())
				if (System.currentTimeMillis() >= result.getLong("tempo"))
					size++;
			database.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return size;
	}

	public void coletar(Player player)
	{
		Database database = SolaryMarket.database;
		boolean i = false;
		database.open();
		try {
			ResultSet result = database.query(
					"select * from " + SolaryMarket.table + "_expirados where player='" + player.getName() + "';");
			List<UUID> uuids = new ArrayList<>();
			while (result.next()) {
				if (!i)
					i = true;
				List<ItemStack> itens = Base64.fromBase64(result.getString("cache"));
				if (getEspaco(player.getInventory()) >= itens.size()) {
					for (ItemStack item : itens)
						player.getInventory().addItem(new ItemStack[] { item });
					uuids.add(UUID.fromString(result.getString("uuid")));
				} else {
					player.sendMessage(SolaryMarket.mensagens.get("INVENTORY_FULL"));
					break;
				}
			}

			for (UUID uuid : uuids)
				database.execute(
						"delete from " + SolaryMarket.table + "_expirados where uuid='" + uuid.toString() + "';");
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		database.close();
		if (!i)
			player.sendMessage(SolaryMarket.mensagens.get("EXPIRE_NOTFOUND"));
		else {
			player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 1.0F, 1.0F);
			player.sendMessage(SolaryMarket.mensagens.get("EXPIRE_COLLECT"));
		}
		expirados(player);
	}

	public void coletar(Player player, UUID uuid)
	{
		Database database = SolaryMarket.database;
		database.open();
		try {
			ResultSet result = database
					.query("select * from " + SolaryMarket.table + "_expirados where uuid='" + uuid + "';");

			if (result.next()) {
				List<ItemStack> itens = Base64.fromBase64(result.getString("cache"));
				if (getEspaco(player.getInventory()) >= itens.size()) {
					database.execute(
							"delete from " + SolaryMarket.table + "_expirados where uuid='" + uuid.toString() + "';");
					for (ItemStack item : itens)
						player.getInventory().addItem(new ItemStack[] { item });
				} else
					player.sendMessage(SolaryMarket.mensagens.get("INVENTORY_FULL"));
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		database.close();
		expirados(player);
	}

	public void coletar(Database database, Player player, UUID uuid)
	{
		try {
			ResultSet result = database
					.query("select * from " + SolaryMarket.table + "_expirados where uuid='" + uuid + "';");

			if (result.next()) {
				List<ItemStack> itens = Base64.fromBase64(result.getString("cache"));
				if (getEspaco(player.getInventory()) >= itens.size()) {
					database.execute(
							"delete from " + SolaryMarket.table + "_expirados where uuid='" + uuid.toString() + "';");
					for (ItemStack item : itens)
						player.getInventory().addItem(new ItemStack[] { item });
				} else
					player.sendMessage(SolaryMarket.mensagens.get("INVENTORY_FULL"));
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		expirados(player);
	}

	public void expirados(Player player)
	{
		Inventory inventory = Bukkit.createInventory(null, 54, "§0§0§8Itens expirados");

		Database database = SolaryMarket.database;
		database.open();
		try {
			ResultSet mercado_result = database
					.query("select * from " + SolaryMarket.table + "_mercado where player='" + player.getName() + "';");

			while (mercado_result.next()) {
				long tempo = mercado_result.getLong("tempo");
				if (System.currentTimeMillis() >= tempo) {
					UUID uuid = UUID.fromString(mercado_result.getString("uuid"));

					ResultSet result = database.query(
							"select * from " + SolaryMarket.table.concat("_mercado") + " where uuid='" + uuid + "';");
					if (result.next()) {
						String cache = result.getString("cache");
						database.execute("insert into " + SolaryMarket.table.concat("_expirados") + " values ('"
								+ player.getName() + "', '" + uuid + "', '" + cache + "');");
						database.execute("delete from " + SolaryMarket.table.concat("_mercado").concat(" where uuid='")
								.concat(uuid.toString()).concat("';"));
					}
				}
			}

			ResultSet result = database.query(
					"select * from " + SolaryMarket.table + "_expirados where player='" + player.getName() + "';");

			while (result.next()) {
				if (inventory.firstEmpty() == -1)
					break;
				List<ItemStack> itens = Base64.fromBase64(result.getString("cache"));
				String uuid = result.getString("uuid");
				ItemStack icone = itens.get(0).clone();
				if (itens.size() > 1)
					icone = new ItemBuilder("54 1 name:&6Baú_De_Itens").toItemStack();

				NBTItem itemnbt = new NBTItem(icone);
				NBTTagCompound itemtag = itemnbt.getTag();
				NBTTagCompound newtag = new NBTTagCompound();
				newtag.setString("type", "coletar");
				newtag.setString("uuid", uuid);
				itemtag.setCompound("huntersmarket", newtag);
				itemnbt.setTag(itemtag);
				icone = itemnbt.getItem();
				ItemMeta meta = icone.getItemMeta();
				List<String> lore = new ArrayList<>();
				if (meta.getLore() != null)
					lore = meta.getLore();
				lore.add("");
				lore.add("§aClique aqui para coletar esse item.");
				meta.setLore(lore);
				icone.setItemMeta(meta);
				inventory.addItem(new ItemStack[] { icone });
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		database.close();

		inventory.setItem(45, backIcon());
		inventory.setItem(53, coletarIcon());
		player.openInventory(inventory);
	}

	public ItemStack backIcon()
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

	public ItemStack coletarIcon()
	{
		ItemStack item = new ItemStack(Material.ENDER_PORTAL_FRAME);
		NBTItem itemnbt = new NBTItem(item);
		NBTTagCompound itemtag = itemnbt.getTag();
		NBTTagCompound newtag = new NBTTagCompound();
		newtag.setString("type", "coletar");
		itemtag.setCompound("huntersmarket", newtag);
		itemnbt.setTag(itemtag);
		item = itemnbt.getItem();

		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§6Coletar Itens");
		meta.setLore(Arrays.asList(new String[] { "§7Clique para coletar seus itens expirados." }));
		item.setItemMeta(meta);
		return item;
	}

	public void pessoal(Player player)
	{
		Categoria categoria = this.categoriasPessoais.get(player.getName());
		if (categoria != null)
			categoria.vizualizar(player);
		else {
			loadPessoal(player);
			categoria = this.categoriasPessoais.get(player.getName());
			categoria.vizualizar(player);
		}
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

	public Map<String, Categoria> getCategorias()
	{
		return this.categorias;
	}

	public void setCategorias(Map<String, Categoria> categorias)
	{
		this.categorias = categorias;
	}

	public Map<String, Categoria> getCategoriasPessoais()
	{
		return this.categoriasPessoais;
	}

	public void setCategoriasPessoais(Map<String, Categoria> categoriasPessoais)
	{
		this.categoriasPessoais = categoriasPessoais;
	}

	private ItemStack load(Player player, String type)
	{
		YamlConfiguration config = SolaryMarket.config.getYaml();
		boolean contagem = config.getBoolean("contagem");
		ItemStack item = null;
		switch (type.toLowerCase()) {
		case "pessoal":
			Categoria categoria = this.categoriasPessoais.get(player.getName());
			if (categoria != null)
				item = categoria.toItemStack(player, contagem);
			else {
				loadPessoal(player);
				item = load(player, type);
			}
			return item;
		case "expirado":
			int size = expiradosSize(player);

			item = new ItemStack(Material.ENDER_CHEST);
			NBTItem itemnbt = new NBTItem(item);
			NBTTagCompound itemtag = itemnbt.getTag();
			NBTTagCompound newtag = new NBTTagCompound();
			newtag.setString("type", "expirado");
			itemtag.setCompound("huntersmarket", newtag);
			itemnbt.setTag(itemtag);
			item = itemnbt.getItem();
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName("§6Itens Expirados");
			meta.setLore(Arrays.asList(new String[] { "", "§7Colete seus itens expirados.", "§7Itens: " + size }));
			item.setItemMeta(meta);
			if (contagem)
				item.setAmount((size >= 64) ? 64 : (size > 1 ? size : 1));
			return item;

		default:
			break;
		}
		return null;
	}

	public void loadPessoal(Player player)
	{
		Categoria categoria = this.categoriasPessoais.get(player.getName());
		if (categoria == null) {
			YamlConfiguration config = SolaryMarket.config.getYaml();
			int pessoalslot = config.getInt("menu.pessoalslot");
			ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
			SkullMeta meta = (SkullMeta) skull.getItemMeta();
			meta.setOwner(player.getName());
			meta.setDisplayName("§6Mercado Pessoal");
			skull.setItemMeta(meta);
			categoria = new Categoria(skull, "PESSOAL:" + player.getName(), player.getName(), pessoalslot, true);
			categoria.register();
			this.categoriasPessoais.put(player.getName(), categoria);
		}
	}

	@org.bukkit.event.EventHandler
	public void onClick(InventoryClickEvent event)
	{
		if ((event.getWhoClicked() instanceof Player)) {
			Player player = (Player) event.getWhoClicked();
			if (event.getInventory().getTitle().startsWith("§0§0§0§8Mercado")) {
				event.setCancelled(true);
				if ((event.getCurrentItem() == null) || (event.getCurrentItem().getType() == Material.AIR))
					return;
				NBTItem itemnbt = new NBTItem(event.getCurrentItem());
				NBTTagCompound itemtag = itemnbt.getTag();
				NBTTagCompound hunterstag = itemtag.getCompound("huntersmarket");
				if (hunterstag.getNbtTag() != null) {
					String type = hunterstag.getString("type");
					if (type.equalsIgnoreCase("expirado"))
						expirados(player);
					else if (type.startsWith("PESSOAL:"))
						pessoal(player);
					else {
						Categoria categoria = this.categorias.get(type);
						if (categoria != null)
							categoria.vizualizar(player);
					}
				}
			} else if (event.getInventory().getTitle().startsWith("§0§0§8Itens expirados")) {
				event.setCancelled(true);
				if ((event.getCurrentItem() == null) || (event.getCurrentItem().getType() == Material.AIR))
					return;
				NBTItem itemnbt = new NBTItem(event.getCurrentItem());
				NBTTagCompound itemtag = itemnbt.getTag();
				NBTTagCompound hunterstag = itemtag.getCompound("huntersmarket");
				if (hunterstag.getNbtTag() != null) {
					String type = hunterstag.getString("type");
					if (type.equalsIgnoreCase("back"))
						mercado(player);
					else if (type.equalsIgnoreCase("coletar"))
						if (hunterstag.has("uuid")) {
							this.coletar(player, UUID.fromString(hunterstag.getString("uuid")));
							player.sendMessage(SolaryMarket.mensagens.get("COLLECT_SUCESS"));
							player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 1.0F, 1.0F);
						} else
							this.coletar(player);
				}
			} else if (event.getInventory().getTitle().startsWith("§3§3§8Confirmar Compra")) {
				event.setCancelled(true);
				if ((event.getCurrentItem() == null) || (event.getCurrentItem().getType() == Material.AIR))
					return;
				NBTItem itemnbt = new NBTItem(event.getCurrentItem());
				NBTTagCompound itemtag = itemnbt.getTag();
				NBTTagCompound hunterstag = itemtag.getCompound("huntersmarket");
				if (hunterstag.getNbtTag() != null) {
					String type = hunterstag.getString("type");
					String uuid = hunterstag.getString("uuid");
					String categoria = null;
					String dono = null;
					double valor = 0.0D;
					List<ItemStack> itens = null;
					boolean disponivel = false;
					Database database = SolaryMarket.database;
					database.open();
					ResultSet result = database
							.query("select * from " + SolaryMarket.table + "_mercado where uuid='" + uuid + "';");
					try {
						if (result.next()) {
							disponivel = true;
							dono = result.getString("player");
							categoria = result.getString("categoria");
							valor = result.getDouble("preco");
							itens = Base64.fromBase64(result.getString("cache"));
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
					database.close();

					if (type.equalsIgnoreCase("confirmar")) {
						if (disponivel) {
							double money = SolaryMarket.vault.getEconomy().getBalance(player);
							if (money >= valor) {
								if (getEspaco(player.getInventory()) >= itens.size()) {
									database.open();
									database.execute("delete from " + SolaryMarket.table + "_mercado where uuid='"
											+ uuid + "';");
									database.close();
									for (ItemStack item : itens)
										player.getInventory().addItem(new ItemStack[] { item });
									SolaryMarket.vault.getEconomy().withdrawPlayer(player, valor);
									SolaryMarket.vault.getEconomy().depositPlayer(dono, valor);

									player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 1.0F, 1.0F);
									player.sendMessage(SolaryMarket.mensagens.get("BUY_SUCESS")
											.replace("{valor}", SolaryMarket.numberFormat(valor))
											.replace("{player}", dono));
									Player target = Bukkit.getPlayer(dono);
									if (target != null) {
										target.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 1.0F, 1.0F);
										target.sendMessage(SolaryMarket.mensagens.get("SELL_BUY_TARGET")
												.replace("{valor}", SolaryMarket.numberFormat(valor))
												.replace("{player}", player.getName()));
									}
								} else {
									player.sendMessage(SolaryMarket.mensagens.get("INVENTORY_FULL"));
									player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0F, 1.0F);
								}
							} else {
								player.sendMessage(SolaryMarket.mensagens.get("NO_MONEY"));
								player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0F, 1.0F);
							}
						} else {
							player.sendMessage(SolaryMarket.mensagens.get("ITEM_NOTFOUND"));
							player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0F, 1.0F);
						}
						Categoria cat = this.categorias.get(categoria);
						if (cat != null)
							cat.vizualizar(player);
						else
							mercado(player);
					}
					if (type.equalsIgnoreCase("cancelar")) {
						Categoria cat = this.categorias.get(categoria);
						if (cat != null)
							cat.vizualizar(player);
						else
							mercado(player);
						player.sendMessage(SolaryMarket.mensagens.get("CONFIRM_NO"));
					}
				}
			}
		}
	}
}
