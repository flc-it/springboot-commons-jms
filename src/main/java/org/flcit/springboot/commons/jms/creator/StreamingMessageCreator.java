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

package org.flcit.springboot.commons.jms.creator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.StreamMessage;

import org.springframework.jms.core.MessageCreator;
import org.springframework.util.StreamUtils;

import org.flcit.springboot.commons.jms.functional.ConsumerJmsException;
import org.flcit.springboot.commons.jms.streaming.OutputStreamMessage;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public class StreamingMessageCreator implements MessageCreator {

    private final ConsumerJmsException<OutputStream> consumer;

    /**
     * @param inputStream
     */
    public StreamingMessageCreator(InputStream inputStream) {
        this(outputStream -> {
            try {
                StreamUtils.copy(inputStream, outputStream);
            } catch (IOException e) {
                final JMSException exc = new JMSException(e.getClass().getSimpleName());
                exc.setLinkedException(e);
                throw exc;
            }
        });
    }

    private StreamingMessageCreator(ConsumerJmsException<OutputStream> consumer) {
        this.consumer = consumer;
    }

    @Override
    public Message createMessage(Session session) throws JMSException {
        final StreamMessage streamMessage = session.createStreamMessage();
        consumer.accept(new OutputStreamMessage(streamMessage));
        return streamMessage;
    }

}
