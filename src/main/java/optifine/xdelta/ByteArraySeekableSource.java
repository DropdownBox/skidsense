package optifine.xdelta;

public class ByteArraySeekableSource implements SeekableSource
{
    byte[] source;
    long lastPos = 0L;

    public ByteArraySeekableSource(byte[] source)
    {
        this.source = source;
    }

    public void seek(long pos) {
        this.lastPos = pos;
    }

    public int read(byte[] b, int off, int len) {
        int i = this.source.length - (int) this.lastPos;

        if (i <= 0) {
            return -1;
        } else {
            if (i < len) {
                len = i;
            }

            System.arraycopy(this.source, (int) this.lastPos, b, off, len);
            this.lastPos += len;
            return len;
        }
    }

    public long length() {
        return this.source.length;
    }

    public void close() {
        this.source = null;
    }
}
