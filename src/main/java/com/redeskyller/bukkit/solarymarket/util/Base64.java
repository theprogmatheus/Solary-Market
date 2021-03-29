package com.redeskyller.bukkit.solarymarket.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class Base64 {
	public static String toBase64(List<ItemStack> itens)
	{
		String base64 = "";
		try {
			if ((itens != null) && (!itens.isEmpty())) {
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
				for (ItemStack item : itens)
					if (item != null)
						dataOutput.writeObject(item);
				dataOutput.close();
				base64 = Base64Coder.encodeLines(outputStream.toByteArray());
			}
		} catch (Exception erro) {
			erro.printStackTrace();
		}
		return base64;
	}

	public static List<ItemStack> fromBase64(String base64)
	{
		List<ItemStack> itens = new ArrayList<>();
		try {
			if ((base64 != null) && (!base64.isEmpty()) && (!base64.equalsIgnoreCase("null"))) {
				ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(base64));
				BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
				ItemStack item = (ItemStack) dataInput.readObject();
				while (item != null) {
					itens.add(item);
					try {
						item = (ItemStack) dataInput.readObject();
					} catch (Exception e) {
						item = null;
					}
				}
				dataInput.close();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return itens;
	}
}
