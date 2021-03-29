package com.redeskyller.bukkit.solarymarket.lib.fanciful;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.gson.stream.JsonWriter;

final class MessagePart implements JsonRepresentedObject, ConfigurationSerializable, Cloneable {
	ChatColor color = ChatColor.WHITE;
	ArrayList<ChatColor> styles = new ArrayList<>();
	String clickActionName = null;
	String clickActionData = null;
	String hoverActionName = null;
	JsonRepresentedObject hoverActionData = null;
	TextualComponent text = null;
	String insertionData = null;
	ArrayList<JsonRepresentedObject> translationReplacements = new ArrayList<>();
	static final BiMap<ChatColor, String> stylesToNames;

	MessagePart(TextualComponent text)
	{
		this.text = text;
	}

	MessagePart()
	{
		this.text = null;
	}

	boolean hasText()
	{
		return this.text != null;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public MessagePart clone() throws CloneNotSupportedException
	{
		MessagePart obj = (MessagePart) super.clone();
		obj.styles = ((ArrayList<ChatColor>) this.styles.clone());
		if ((this.hoverActionData instanceof JsonString))
			obj.hoverActionData = new JsonString(((JsonString) this.hoverActionData).getValue());
		else if ((this.hoverActionData instanceof FancyMessage))
			obj.hoverActionData = ((FancyMessage) this.hoverActionData).clone();
		obj.translationReplacements = ((ArrayList) this.translationReplacements.clone());
		return obj;
	}

	@Override
	public void writeJson(JsonWriter json)
	{
		try {
			json.beginObject();
			this.text.writeJson(json);
			json.name("color").value(this.color.name().toLowerCase());
			for (ChatColor style : this.styles)
				json.name(stylesToNames.get(style)).value(true);
			if ((this.clickActionName != null) && (this.clickActionData != null))
				json.name("clickEvent").beginObject().name("action").value(this.clickActionName).name("value")
						.value(this.clickActionData).endObject();
			if ((this.hoverActionName != null) && (this.hoverActionData != null)) {
				json.name("hoverEvent").beginObject().name("action").value(this.hoverActionName).name("value");
				this.hoverActionData.writeJson(json);
				json.endObject();
			}
			if (this.insertionData != null)
				json.name("insertion").value(this.insertionData);
			if ((this.translationReplacements.size() > 0) && (this.text != null)
					&& (TextualComponent.isTranslatableText(this.text))) {
				json.name("with").beginArray();
				for (JsonRepresentedObject obj : this.translationReplacements)
					obj.writeJson(json);
				json.endArray();
			}
			json.endObject();
		} catch (IOException e) {
			org.bukkit.Bukkit.getLogger().log(Level.WARNING, "A problem occured during writing of JSON string", e);
		}
	}

	@Override
	public Map<String, Object> serialize()
	{
		HashMap<String, Object> map = new HashMap<>();
		map.put("text", this.text);
		map.put("styles", this.styles);
		map.put("color", Character.valueOf(this.color.getChar()));
		map.put("hoverActionName", this.hoverActionName);
		map.put("hoverActionData", this.hoverActionData);
		map.put("clickActionName", this.clickActionName);
		map.put("clickActionData", this.clickActionData);
		map.put("insertion", this.insertionData);
		map.put("translationReplacements", this.translationReplacements);
		return map;
	}

	@SuppressWarnings("unchecked")
	public static MessagePart deserialize(Map<String, Object> serialized)
	{
		MessagePart part = new MessagePart((TextualComponent) serialized.get("text"));
		part.styles = ((ArrayList<ChatColor>) serialized.get("styles"));
		part.color = ChatColor.getByChar(serialized.get("color").toString());
		part.hoverActionName = ((String) serialized.get("hoverActionName"));
		part.hoverActionData = ((JsonRepresentedObject) serialized.get("hoverActionData"));
		part.clickActionName = ((String) serialized.get("clickActionName"));
		part.clickActionData = ((String) serialized.get("clickActionData"));
		part.insertionData = ((String) serialized.get("insertion"));
		part.translationReplacements = ((ArrayList<JsonRepresentedObject>) serialized.get("translationReplacements"));
		return part;
	}

	static {
		ImmutableBiMap.Builder<ChatColor, String> builder = ImmutableBiMap.builder();
		ChatColor[] arrayOfChatColor;
		int j = (arrayOfChatColor = ChatColor.values()).length;
		for (int i = 0; i < j; i++) {
			ChatColor style = arrayOfChatColor[i];
			if (style.isFormat()) {
				String styleName;
				switch (style) {
				case GREEN:
					styleName = "obfuscated";
					break;
				case UNDERLINE:
					styleName = "underlined";
					break;
				case ITALIC:
				case LIGHT_PURPLE:
				case MAGIC:
				case RED:
				case RESET:
				case STRIKETHROUGH:
				case WHITE:
				case YELLOW:
				default:
					styleName = style.name().toLowerCase();
				}
				builder.put(style, styleName);
			}
		}
		stylesToNames = builder.build();

		ConfigurationSerialization.registerClass(MessagePart.class);
	}
}
