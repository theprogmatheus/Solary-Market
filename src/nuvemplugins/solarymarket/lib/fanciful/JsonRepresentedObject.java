package nuvemplugins.solarymarket.lib.fanciful;

import java.io.IOException;

import com.google.gson.stream.JsonWriter;

abstract interface JsonRepresentedObject
{
	public abstract void writeJson(JsonWriter paramJsonWriter) throws IOException;
}
