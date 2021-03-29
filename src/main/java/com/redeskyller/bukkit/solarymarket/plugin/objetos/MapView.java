package com.redeskyller.bukkit.solarymarket.plugin.objetos;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.redeskyller.bukkit.solarymarket.app.SolaryMarket;
import com.redeskyller.bukkit.solarymarket.database.Database;
import com.redeskyller.bukkit.solarymarket.lib.itembuilder.ItemBuilder;
import com.redeskyller.bukkit.solarymarket.lib.nbt.NBTItem;
import com.redeskyller.bukkit.solarymarket.lib.nbt.NBTTagCompound;

public class MapView {
	private Map<Integer, Inventory> inventorys;
	private int atual;
	private String title;
	private Player player;
	private ItemStack icone;

	public MapView(ItemStack icone, Player player, String title)
	{
		this.player = player;
		this.title = title;
		this.inventorys = new HashMap<>();
		this.icone = icone;
		this.inventorys.put(Integer.valueOf(1), newPage(1));
		this.atual = 1;
	}

	public void visualizar()
	{
		Inventory page = this.inventorys.get(Integer.valueOf(1));
		if (page != null)
			this.player.openInventory(page);
	}

	public void nextPage()
	{
		Inventory nextpage = this.inventorys.get(Integer.valueOf(this.atual + 1));
		if (nextpage != null) {
			this.player.openInventory(nextpage);
			this.atual += 1;
		}
	}

	public void backPage()
	{
		Inventory nextpage = this.inventorys.get(Integer.valueOf(this.atual - 1));
		if (nextpage != null) {
			this.player.openInventory(nextpage);
			this.atual -= 1;
		}
	}

	public void add(ItemStack item)
	{
		Inventory inv = this.inventorys.get(Integer.valueOf(this.inventorys.size()));
		if (inv.firstEmpty() >= 46) {
			this.inventorys.put(Integer.valueOf(this.inventorys.size() + 1), newPage(this.inventorys.size() + 1));
			inv = this.inventorys.get(Integer.valueOf(this.inventorys.size()));
		}
		inv.addItem(new ItemStack[] { item });
	}

	public Map<Integer, Inventory> getInventorys()
	{
		return this.inventorys;
	}

	public void setInventorys(Map<Integer, Inventory> inventorys)
	{
		this.inventorys = inventorys;
	}

	public String getTitle()
	{
		return this.title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	private Inventory newPage(int id)
	{
		Inventory inventory = Bukkit.createInventory(null, 54, this.title.concat(" - " + id));
		inventory.setItem(45, backIcon());
		inventory.setItem(48, backPageIcon());
		inventory.setItem(49, refreshIcon());
		inventory.setItem(50, nextPageIcon());
		inventory.setItem(53, expiradosIcon());
		return inventory;
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

	private ItemStack backPageIcon()
	{
		ItemStack item = new ItemBuilder("339 1  unbreaking:1 glow:true").toItemStack();
		NBTItem itemnbt = new NBTItem(item);
		NBTTagCompound itemtag = itemnbt.getTag();
		NBTTagCompound newtag = new NBTTagCompound();
		newtag.setString("type", "back-page");
		itemtag.setCompound("huntersmarket", newtag);
		itemnbt.setTag(itemtag);
		item = itemnbt.getItem();
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§e<--");
		meta.setLore(Arrays.asList(new String[] { "§7Voltar para a página anterior." }));
		item.setItemMeta(meta);
		return item;
	}

	private ItemStack refreshIcon()
	{
		ItemStack item = this.icone.clone();
		NBTItem itemnbt = new NBTItem(item);
		NBTTagCompound itemtag = itemnbt.getTag();
		NBTTagCompound newtag = new NBTTagCompound();
		newtag.setString("type", "refresh");
		itemtag.setCompound("huntersmarket", newtag);
		itemnbt.setTag(itemtag);
		item = itemnbt.getItem();
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§6Atualizar");
		meta.setLore(Arrays.asList(new String[] { "§7Clique para atualizar o mercado." }));
		item.setItemMeta(meta);
		return item;
	}

	private ItemStack nextPageIcon()
	{
		ItemStack item = new ItemBuilder("339 1  unbreaking:1 glow:true").toItemStack();
		NBTItem itemnbt = new NBTItem(item);
		NBTTagCompound itemtag = itemnbt.getTag();
		NBTTagCompound newtag = new NBTTagCompound();
		newtag.setString("type", "next-page");
		itemtag.setCompound("huntersmarket", newtag);
		itemnbt.setTag(itemtag);
		item = itemnbt.getItem();
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§e-->");
		meta.setLore(Arrays.asList(new String[] { "§7Avançar para a próxima página." }));
		item.setItemMeta(meta);
		return item;
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
			database.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return size;
	}

	private ItemStack expiradosIcon()
	{
		ItemStack item = new ItemStack(Material.ENDER_CHEST);
		int size = expiradosSize(this.player);
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
		return item;
	}
}
