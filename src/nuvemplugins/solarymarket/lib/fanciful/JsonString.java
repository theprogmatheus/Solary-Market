package nuvemplugins.solarymarket.lib.fanciful;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.stream.JsonWriter;

final class JsonString
		implements JsonRepresentedObject, org.bukkit.configuration.serialization.ConfigurationSerializable
{
	private String _value;

	public JsonString(CharSequence value) {
		this._value = (value == null ? null : value.toString());
	}

	@Override
	public void writeJson(JsonWriter writer) throws java.io.IOException
	{
		writer.value(this.getValue());
	}

	public String getValue()
	{
		return this._value;
	}

	@Override
	public Map<String, Object> serialize()
	{
		HashMap<String, Object> theSingleValue = new HashMap<>();
		theSingleValue.put("stringValue", this._value);
		return theSingleValue;
	}

	public static JsonString deserialize(Map<String, Object> map)
	{
		return new JsonString(map.get("stringValue").toString());
	}

	@Override
	public String toString()
	{
		return this._value;
	}
}
