package com.garliclord.spalk;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class PositionalBufferedInputStream extends BufferedInputStream {
    private long position = 0;
    private long mark = 0;

    public PositionalBufferedInputStream(InputStream in) {
        super(in);
    }

    public synchronized long getPosition() {
        return position;
    }

    @Override
    public synchronized int read() throws IOException {
        int aByte = super.read();
        if (aByte > -1) {
            position ++;
        }
        return aByte;
    }

    @Override
    public synchronized int read(byte[] b, int off, int len)
            throws IOException
    {
        int byteCount = super.read(b, off, len);
        if (byteCount > -1) {
            position += byteCount;
        }
        return byteCount;
    }

    @Override
    public synchronized long skip(long skip)
            throws IOException
    {
        long byteCount = super.skip(skip);
        position += byteCount;
        return byteCount;
    }

    @Override
    public synchronized void mark(int readlimit)
    {
        super.mark(readlimit);
        mark = position;
    }

    @Override
    public synchronized void reset()
            throws IOException
    {
        super.reset();
        position = mark;
    }
}
