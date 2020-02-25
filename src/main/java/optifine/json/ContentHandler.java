package optifine.json;

public interface ContentHandler
{
    void startJSON();

	void endJSON();

	boolean startObject();

	boolean endObject();

	boolean startObjectEntry(String var1);

	boolean endObjectEntry();

	boolean startArray();

	boolean endArray();

	boolean primitive(Object var1);
}
