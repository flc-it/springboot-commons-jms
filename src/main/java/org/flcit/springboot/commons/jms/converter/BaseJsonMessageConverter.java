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

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.springframework.jms.support.converter.MessageConversionException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public abstract class BaseJsonMessageConverter extends BaseMessageConverter {

    private static final String IOEXCEPTION_MESSAGE = "Une erreur s'est produite lors de la convertion du message";

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .setSerializationInclusion(Include.NON_EMPTY)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);

    @Override
    public <T> T fromBytes(byte[] bytes, Class<T> clazz) throws MessageConversionException {
        try {
            return objectMapper.readValue(bytes, clazz);
        } catch (IOException e) {
            throw new MessageConversionException(IOEXCEPTION_MESSAGE, e);
        }
    }

    @Override
    public <T> T fromString(String string, Class<T> clazz) throws MessageConversionException {
        try {
            return objectMapper.readValue(string, clazz);
        } catch (IOException e) {
            throw new MessageConversionException(IOEXCEPTION_MESSAGE, e);
        }
    }

    @Override
    public <T> T fromStream(InputStream inputStream, Class<T> clazz) throws MessageConversionException {
        try {
            return objectMapper.readValue(inputStream, clazz);
        } catch (IOException e) {
            throw new MessageConversionException(IOEXCEPTION_MESSAGE, e);
        }
    }

    @Override
    public <T> T fromMap(Map<?, ?> values, Class<T> clazz) throws MessageConversionException {
        return objectMapper.convertValue(values, clazz);
    }

    @Override
    public byte[] toBytes(Object object) throws MessageConversionException {
        try {
            return objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            throw new MessageConversionException(IOEXCEPTION_MESSAGE, e);
        }
    }

    @Override
    public String toString(Object object) throws MessageConversionException {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new MessageConversionException(IOEXCEPTION_MESSAGE, e);
        }
    }

}