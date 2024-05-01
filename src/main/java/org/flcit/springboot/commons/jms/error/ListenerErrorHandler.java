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

package org.flcit.springboot.commons.jms.error;

import org.flcit.commons.core.util.ClassUtils;
import org.flcit.commons.core.util.ObjectUtils;
import org.flcit.commons.core.util.ReflectionUtils;
import org.flcit.commons.core.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionException;
import org.springframework.util.ErrorHandler;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
@Component
public class ListenerErrorHandler implements ErrorHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ListenerErrorHandler.class);

    @Override
    public void handleError(Throwable t) {
        // Exception sans cause
        if (t.getCause() == null) {
            LOG.error("EXCEPTION SANS CAUSE : " + t.getMessage(), t);
        }
        // Exception lors d'un appel à un service REST
        else if (ClassUtils.safeIsAssignableFrom("org.springframework.web.client.RestClientException", t.getCause().getClass())) {
            catchRestClientException((Exception) t.getCause());
        }
        // Exception lors d'un appel à un web service (SOAP)
        else if (ClassUtils.safeIsAssignableFrom("org.springframework.ws.WebServiceException", t.getCause().getClass())) {
            catchWebServiceException((Exception) t.getCause());
        }
        // Exception lors d'une transaction (ex: accès à la Base de données)
        else if (t.getCause() instanceof TransactionException) {
            catchTransactionException((TransactionException) t.getCause());
        }
        // Exception lors d'une opération sur la Base de données
        else if (ClassUtils.safeIsAssignableFrom("javax.persistence.PersistenceException", t.getCause().getClass())) {
            catchPersistenceException((Exception) t.getCause());
        }
        // Exception lors de la lecture et du parsing du message JMS
        else if(t.getCause() instanceof MessagingException) {
            catchMessagingException((MessagingException) t.getCause());
        }
        // Exception dont la cause n'est pas traitée
        else {
            if (LOG.isErrorEnabled()) {
                final String message = ObjectUtils.getOrDefault(t.getCause().getMessage(), t.getMessage());
                LOG.error("CAUSE NON TRAITÉE : {} => {}" + t.getCause().getClass() + " => " + message, t.getCause());
            }
        }
        ThreadUtils.sleep(30000);
    }

    private static final void catchMessagingException(MessagingException e) {
        LOG.error(e.getClass() + " : " + e.getMessage(), e);
    }

    private static final void catchTransactionException(TransactionException e) {
        LOG.error(e.getClass() + " : " + e.getMessage(), e);
    }

    private static final void catchPersistenceException(Exception e) {
        LOG.error(e.getClass() + " : " + e.getMessage(), e);
    }

    private static final void catchWebServiceException(Exception e) {
        //Exception client
        if (ClassUtils.safeIsAssignableFrom("org.springframework.ws.client.WebServiceClientException", e.getClass())) {
            LOG.error(e.getClass().getName() + " : " + e.getMessage(), e);
        }
        //Exception server
        else if (ClassUtils.safeIsAssignableFrom("org.springframework.ws.WebServiceMessageException", e.getClass())) {
            LOG.error(e.getClass().getName() + " : " + e.getMessage(), e);
        }
        //Exception autre
        else {
            LOG.error(e.getClass() + " : " + e.getMessage(), e);
        }
    }

    private static final void catchRestClientException(Exception e) {
        //Exception client
        if (ClassUtils.safeIsAssignableFrom("org.springframework.web.client.ResourceAccessException", e.getClass())) {
            LOG.error(e.getClass().getName() + " : " + e.getMessage(), e);
        }
        //Exception server
        else if (LOG.isErrorEnabled()
                && ClassUtils.safeIsAssignableFrom("org.springframework.web.client.RestClientResponseException", e.getClass())) {
            LOG.error(e.getClass().getName() + " : " + ReflectionUtils.getFieldValue(e, "rawStatusCode") + " " + ReflectionUtils.getFieldValue(e, "statusText") + " => " + ReflectionUtils.getSafeMethodValue(e, "getResponseBodyAsString"), e);
        }
        //Exception autre
        else {
            LOG.error(e.getClass() + " : " + e.getMessage(), e);
        }
    }

}