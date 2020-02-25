
package javax.jnlp;

public interface PersistenceService {

	int CACHED = 0;
	int TEMPORARY = 1;
	int DIRTY = 2;

	long create(java.net.URL url, long maxsize);

	FileContents get(java.net.URL url);

	void delete(java.net.URL url);

	java.lang.String[] getNames(java.net.URL url);

	int getTag(java.net.URL url);

	void setTag(java.net.URL url, int tag);

}

