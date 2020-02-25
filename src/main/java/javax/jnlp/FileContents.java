

package javax.jnlp;

public interface FileContents {

  java.lang.String getName();

  java.io.InputStream getInputStream();

  java.io.OutputStream getOutputStream(boolean overwrite);

  long getLength();

  boolean canRead();

  boolean canWrite();

  JNLPRandomAccessFile getRandomAccessFile(java.lang.String mode);

  long getMaxLength();

  long setMaxLength(long maxlength);

}

