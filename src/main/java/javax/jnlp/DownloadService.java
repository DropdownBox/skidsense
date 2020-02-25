

package javax.jnlp;

public interface DownloadService {

	boolean isResourceCached(java.net.URL ref, java.lang.String version);

	boolean isPartCached(java.lang.String part);

	boolean isPartCached(java.lang.String[] parts);

	boolean isExtensionPartCached(java.net.URL ref, java.lang.String version, java.lang.String part);

	boolean isExtensionPartCached(java.net.URL ref, java.lang.String version, java.lang.String[] parts);

	void loadResource(java.net.URL ref, java.lang.String version, DownloadServiceListener progress);

	void loadPart(java.lang.String part, DownloadServiceListener progress);

	void loadPart(java.lang.String[] parts, DownloadServiceListener progress);

	void loadExtensionPart(java.net.URL ref, java.lang.String version, java.lang.String part, DownloadServiceListener progress);

	void loadExtensionPart(java.net.URL ref, java.lang.String version, java.lang.String[] parts, DownloadServiceListener progress);

	void removeResource(java.net.URL ref, java.lang.String version);

	void removePart(java.lang.String part);

	void removePart(java.lang.String[] parts);

	void removeExtensionPart(java.net.URL ref, java.lang.String version, java.lang.String part);

	void removeExtensionPart(java.net.URL ref, java.lang.String version, java.lang.String[] parts);

	DownloadServiceListener getDefaultProgressWindow();

}

