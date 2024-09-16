/**
 * The MIT License
 * Copyright © 2018 Nordic Institute for Interoperability Solutions (NIIS)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.niis.xrd4j.exampleadapter.mtom;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class LazyStream extends InputStream {
    private volatile long size;
    private final Random rnd = new Random();

    public LazyStream(long size) {
        this.size = size;
    }

    @Override
    public int read() throws IOException {
        if (size > 0) {
            size--;
            return rnd.nextInt(256);
        }

        return -1;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if ( size < 1 ) return -1;

        final int bytesAvailable = ( len > size ) ? (int)size : len;
        size -= bytesAvailable;

        if ( off == 0 && bytesAvailable == b.length ) {
            // fast path
            rnd.nextBytes(b);
        } else {
            for ( int i = off; i < off + bytesAvailable; i++) {
                b[i] = (byte)rnd.nextInt(256);
            }
        }
        return bytesAvailable;
    }
}
