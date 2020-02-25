

package javax.jnlp;

public interface JNLPRandomAccessFile {

  void close();

  long length();

  long getFilePointer();

  int read();

  int read(byte[] b, int off, int len);

  int read(byte[] b);

  void readFully(byte[] b);

  void readFully(byte[] b, int off, int len);

  int skipBytes(int n);

  boolean readBoolean();

  byte readByte();

  int readUnsignedByte();

  short readShort();

  int readUnsignedShort();

  char readChar();

  int readInt();

  long readLong();

  float readFloat();

  double readDouble();

  java.lang.String readLine();

  java.lang.String readUTF();

  void seek(long pos);

  void setLength(long newLength);

  void write(int b);

  void write(byte[] b);

  void write(byte[] b, int off, int len);

  void writeBoolean(boolean v);

  void writeByte(int v);

  void writeShort(int v);

  void writeChar(int v);

  void writeInt(int v);

  void writeLong(long v);

  void writeFloat(float v);

  void writeDouble(double v);

  void writeBytes(java.lang.String s);

  void writeChars(java.lang.String s);

  void writeUTF(java.lang.String str);

}

