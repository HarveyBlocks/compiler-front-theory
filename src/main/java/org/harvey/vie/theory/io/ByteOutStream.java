package org.harvey.vie.theory.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * A utility class for building byte arrays. It provides methods to write
 * byte sequences and to "flatten" multiple byte arrays into a single contiguous array.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-25 16:43
 */
public class ByteOutStream {
    private final ByteArrayOutputStream os = new ByteArrayOutputStream();

    public byte[] toByteArray() {
        return os.toByteArray();
    }

    public void write(byte[] row, int off, int len) {
        os.write(row, off, len);
    }
    public void write(byte[] row) throws IOException {
        os.write(row);
    }

    @Override
    public String toString() {
        return os.toString();
    }
    public static byte[] flap(byte[][] data){
        ByteOutStream os = new ByteOutStream();
        Arrays.stream(data).forEach(e->os.write(e,0,e.length));
        return os.toByteArray();
    }
}
