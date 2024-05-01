/*
 * Copyright 2002-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.flcit.springboot.commons.jms.streaming;

import java.io.IOException;
import java.io.InputStream;

import javax.jms.JMSException;
import javax.jms.MessageEOFException;
import javax.jms.StreamMessage;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public class InputStreamMessage extends InputStream implements AutoCloseable {

    private final StreamMessage streamMessage;

    /**
     * @param streamMessage
     */
    public InputStreamMessage(StreamMessage streamMessage) {
        this.streamMessage = streamMessage;
    }

    @Override
    public int read() throws IOException {
        byte[] bytes = new byte[1];
        if (read(bytes) != -1) {
            return bytes[0] & 0xFF;
        } else {
            return -1;
        }
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }
        byte[] buff = new byte[len];
        int i = read(buff);
        if (i > 0) {
            System.arraycopy(buff, 0, b, off, i);
        }
        return i;
    }

    @Override
    public int read(byte[] b) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        }
        try {
            return streamMessage.readBytes(b);
        } catch (MessageEOFException e) { return -1; }
        catch (JMSException e) { throw new IOException(e); }
    }

    @Override
    public synchronized void reset() throws IOException {
        try {
            streamMessage.reset();
        } catch (JMSException e) { throw new IOException(e); }
    }

    @Override
    public void close() {
        try {
            super.close();
        } catch (IOException e) {
            // DO NOTHING
        }
    }

}
