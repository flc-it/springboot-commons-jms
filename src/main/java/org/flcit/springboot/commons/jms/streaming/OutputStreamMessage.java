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
import java.io.OutputStream;

import javax.jms.JMSException;
import javax.jms.StreamMessage;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public class OutputStreamMessage extends OutputStream implements AutoCloseable {

    private final StreamMessage streamMessage;

    /**
     * @param streamMessage
     */
    public OutputStreamMessage(StreamMessage streamMessage) {
        this.streamMessage = streamMessage;
    }

    /**
     * @param b
     * @throws IOException
     */
    public void writeInt(int b) throws IOException {
        try {
            streamMessage.writeInt(b);
        } catch (JMSException e) { throw new IOException(e); }
    }

    @Override
    public void write(int b) throws IOException {
        write(new byte[] { (byte) b });
    }

    /**
     * @param b
     * @throws IOException
     */
    public void write(byte b) throws IOException {
        try {
            streamMessage.writeByte(b);
        } catch (JMSException e) { throw new IOException(e); }
    }

    @Override
    public void write(byte[] b) throws IOException {
        try {
            streamMessage.writeBytes(b);
        } catch (JMSException e) { throw new IOException(e); }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        try {
            streamMessage.writeBytes(b, off, len);
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
