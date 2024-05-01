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

package org.flcit.springboot.commons.jms.domain;

import java.util.Map;

/**
 * @param <T>
 * @since 
 * @author Florian Lestic
 */
public class JmsWrapper<T extends Object> {

    private Map<String, Object> headers;
    private T object;

    /**
     * @return
     */
    public Map<String, Object> getHeaders() {
        return headers;
    }

    /**
     * @param headers
     */
    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers;
    }

    /**
     * @return
     */
    public T getObject() {
        return object;
    }

    /**
     * @param object
     */
    public void setObject(T object) {
        this.object = object;
    }

}
