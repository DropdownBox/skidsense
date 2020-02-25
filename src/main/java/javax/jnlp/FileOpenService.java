

package javax.jnlp;

public interface FileOpenService {

  FileContents openFileDialog(java.lang.String pathHint, java.lang.String[] extensions);

  FileContents[] openMultiFileDialog(java.lang.String pathHint, java.lang.String[] extensions);

}

