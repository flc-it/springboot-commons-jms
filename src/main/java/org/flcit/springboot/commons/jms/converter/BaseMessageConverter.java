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

package org.flcit.springboot.commons.jms.converter;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.util.CollectionUtils;

import org.flcit.springboot.commons.jms.domain.JmsWrapper;
import org.flcit.springboot.commons.jms.streaming.InputStreamMessage;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public abstract class BaseMessageConverter implements MessageConverter {

    /**
     * @param <T>
     * @param bytes
     * @param clazz
     * @return
     * @throws MessageConversionException
     */
    public abstract <T> T fromBytes(byte[] bytes, Class<T> clazz) throws MessageConversionException;
    /**
     * @param <T>
     * @param string
     * @param clazz
     * @return
     * @throws MessageConversionException
     */
    public abstract <T> T fromString(String string, Class<T> clazz) throws MessageConversionException;
    /**
     * @param <T>
     * @param inputStream
     * @param clazz
     * @return
     * @throws MessageConversionException
     */
    public abstract <T> T fromStream(InputStream inputStream, Class<T> clazz) throws MessageConversionException;
    /**
     * @param <T>
     * @param values
     * @param clazz
     * @return
     * @throws MessageConversionException
     */
    public abstract <T> T fromMap(Map<?, ?> values, Class<T> clazz) throws MessageConversionException;
    /**
     * @param object
     * @return
     * @throws MessageConversionException
     */
    public abstract byte[] toBytes(Object object) throws MessageConversionException;
    /**
     * @param object
     * @return
     * @throws MessageConversionException
     */
    public abstract String toString(Object object) throws MessageConversionException;
    /**
     * @param message
     * @return
     * @throws JMSException
     */
    public abstract Class<?> getClassResult(Message message) throws JMSException;

    @Override
    public Object fromMessage(Message message) throws JMSException, MessageConversionException {
        final Class<?> classResult = getClassResult(message);
        if (classResult == JmsWrapper.class) {
            final JmsWrapper<Object> wrapper = new JmsWrapper<>();
            wrapper.setObject(getResult(message, classResult));
            wrapper.setHeaders(readHeaders(message));
            return wrapper;
        } else {
            return getResult(message, classResult);
        }
    }

    private <T> T getResult(final Message message, final Class<T> classResult) throws JMSException {
        if (message instanceof TextMessage) {
            return fromString(((TextMessage) message).getText(), classResult);
        } else if (message instanceof BytesMessage) {
            final byte[] bytes = new byte[(int) ((BytesMessage) message).getBodyLength()];
            ((BytesMessage) message).readBytes(bytes);
            return fromBytes(bytes, classResult);
        } else if (message instanceof StreamMessage) {
            try (InputStreamMessage is = new InputStreamMessage((StreamMessage) message)) {
                return fromStream(is, classResult);
            }
        } else if (message instanceof MapMessage) {
            return fromMap(((MapMessage) message).getBody(Map.class), classResult);
        }
        throw new IllegalStateException("Message type is not supported : " + message.getClass().getName());
    }

    @Override
    public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
        final BytesMessage message = session.createBytesMessage();
        if (object instanceof JmsWrapper<?>) {
            message.writeBytes(toBytes(((JmsWrapper<?>) object).getObject()));
            writeHeaders(message, ((JmsWrapper<?>) object).getHeaders());
        } else {
            message.writeBytes(toBytes(object));
        }
        return message;
    }

    private static final Message writeHeaders(final Message message, final Map<String, Object> headers) throws JMSException {
        if (CollectionUtils.isEmpty(headers)) {
            return message;
        }
        for (Entry<String, Object> entry : headers.entrySet()) {
            message.setObjectProperty(entry.getKey(), entry.getValue());
        }
        return message;
    }

    private static final Map<String, Object> readHeaders(final Message message) throws JMSException {
        final Enumeration<?> enumeration = message.getPropertyNames();
        final Map<String, Object> headers = new HashMap<>(0);
        while (enumeration.hasMoreElements()) {
            final String name = (String) enumeration.nextElement();
            headers.put(name, message.getObjectProperty(name));
        }
        return headers;
    }

}
