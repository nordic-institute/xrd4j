/*
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
package org.niis.xrd4j.common.util;

import org.niis.xrd4j.common.member.ConsumerMember;
import org.niis.xrd4j.common.member.ProducerMember;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class offers helper methods for handling configuration related
 * operations.
 *
 * @author Petteri Kivimäki
 */
public final class ConfigurationHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationHelper.class);

    private static final int IDX_INSTANCE = 0;
    private static final int IDX_MEMBERCLASS = 1;
    private static final int IDX_MEMBERCODE = 2;
    private static final int IDX_SUBSYSTEM = 3;
    private static final int IDX_SERVICE_WITHOUT_SUBSYSTEM = 3;
    private static final int IDX_SERVICE = 4;
    private static final int IDX_VERSION_WITHOUT_SUBSYSTEM = 4;
    private static final int IDX_VERSION = 5;

    private static final int MIN_LENGTH_CLIENT_ID = 3;
    private static final int MAX_LENGTH_CLIENT_ID = 4;

    private static final int MIN_LENGTH_SERVICE_ID = 4;
    private static final int MID_LENGTH_SERVICE_ID = 5;
    private static final int MAX_LENGTH_SERVICE_ID = 6;

    /**
     * Constructs and initializes a new ConfigurationHelper object. Should never
     * be used.
     */
    private ConfigurationHelper() {
    }

    /**
     * Copies the client id string into an array. If the structure of the
     * string is not correct, null is returned.
     *
     * @param clientId client id string
     * @return client id in an array
     */
    private static String[] clientIdToArr(String clientId) {
        if (clientId == null) {
            return null;
        }
        String[] clientArr = clientId.split("\\.");
        if (clientArr.length == MIN_LENGTH_CLIENT_ID || clientArr.length == MAX_LENGTH_CLIENT_ID) {
            return clientArr;
        }
        return null;
    }

    /**
     * Parses the given client id string and creates a new ConsumerMember
     * according to its value. Null is returned if the given string doesn't
     * contain a valid client id.
     *
     * @param clientId String containing a client id
     * @return new ProducerMember object or null
     */
    public static ConsumerMember parseConsumerMember(String clientId) {
        String[] clientIdArr = ConfigurationHelper.clientIdToArr(clientId);
        if (clientIdArr == null) {
            LOGGER.warn("Client can not be null.");
            return null;
        } else {
            try {
                ConsumerMember consumer = null;
                String instance = clientIdArr[IDX_INSTANCE];
                String memberClass = clientIdArr[IDX_MEMBERCLASS];
                String memberCode = clientIdArr[IDX_MEMBERCODE];
                if (clientIdArr.length == MIN_LENGTH_CLIENT_ID) {
                    consumer = new ConsumerMember(instance, memberClass, memberCode);
                    LOGGER.debug("Consumer member succesfully created. Identifier format : \"instance.memberClass.memberCode\".");
                } else if (clientIdArr.length == MAX_LENGTH_CLIENT_ID) {
                    String subsystem = clientIdArr[IDX_SUBSYSTEM];
                    consumer = new ConsumerMember(instance, memberClass, memberCode, subsystem);
                    LOGGER.debug("Consumer member succesfully created. Identifier format : \"instance.memberClass.memberCode.subsystem\".");
                }
                return consumer;
            } catch (Exception ex) {
                LOGGER.warn("Creating consumer member failed.");
                return null;
            }
        }
    }

    /**
     * Copies the client id string into an array. If the structure of the string is not correct, null is returned.
     *
     * @param serviceId service id string
     * @return service id in an array
     */
    private static String[] serviceIdToArr(String serviceId) {
        if (serviceId == null) {
            return null;
        }
        String[] serviceArr = serviceId.split("\\.");
        if (serviceArr.length >= MIN_LENGTH_SERVICE_ID && serviceArr.length <= MAX_LENGTH_SERVICE_ID) {
            return serviceArr;
        }
        return null;
    }

    /**
     * Parses the given service id string and creates a new ProducerMember
     * according to its value. Null is returned if the given string doesn't
     * contain a valid service id.
     *
     * @param serviceId String containing a service id
     * @return new ProducerMember object or null
     */
    public static ProducerMember parseProducerMember(String serviceId) {
        String[] serviceIdArr = ConfigurationHelper.serviceIdToArr(serviceId);
        if (serviceIdArr == null) {
            LOGGER.warn("Service can not be null.");
            return null;
        } else {
            return parseProducerMember(serviceIdArr);
        }
    }

    /**
     * Creates a new ProducerMember using the service id stored in the given
     * String array.
     *
     * @param serviceIdArr producer member service id
     * @return new ProducerMember object or null
     */
    private static ProducerMember parseProducerMember(String[] serviceIdArr) {
        try {
            ProducerMember producer = null;
            String instance = serviceIdArr[IDX_INSTANCE];
            String memberClass = serviceIdArr[IDX_MEMBERCLASS];
            String memberCode = serviceIdArr[IDX_MEMBERCODE];
            if (serviceIdArr.length == MIN_LENGTH_SERVICE_ID) {
                String service = serviceIdArr[IDX_SERVICE_WITHOUT_SUBSYSTEM];
                producer = new ProducerMember(instance, memberClass, memberCode, service);
                LOGGER.debug("Producer member succesfully created. Identifier format : \"instance.memberClass.memberCode.service\".");
            } else if (serviceIdArr.length == MID_LENGTH_SERVICE_ID) {
                // Last element is considered as version number if it
                // starts with the letters [vV] and besides that contains
                // only numbers, or if the element contains only numbers.
                // Also characters [-_] are allowed.
                if (serviceIdArr[MID_LENGTH_SERVICE_ID - 1].matches("(v|V|)[\\d_-]+")) {
                    String service = serviceIdArr[IDX_SERVICE_WITHOUT_SUBSYSTEM];
                    String version = serviceIdArr[IDX_VERSION_WITHOUT_SUBSYSTEM];
                    producer = new ProducerMember(instance, memberClass, memberCode, "subsystem", service, version);
                    producer.setSubsystemCode(null);
                    LOGGER.debug("Producer member succesfully created. Identifier format : \"instance.memberClass.memberCode.service.version\".");
                } else {
                    String subsystem = serviceIdArr[IDX_SUBSYSTEM];
                    String service = serviceIdArr[IDX_SERVICE];
                    producer = new ProducerMember(instance, memberClass, memberCode, subsystem, service);
                    LOGGER.debug("Producer member succesfully created. Identifier format : \"instance.memberClass.memberCode.subsystem.service\".");
                }
            } else if (serviceIdArr.length == MAX_LENGTH_SERVICE_ID) {
                String subsystem = serviceIdArr[IDX_SUBSYSTEM];
                String service = serviceIdArr[IDX_SERVICE];
                String version = serviceIdArr[IDX_VERSION];
                producer = new ProducerMember(instance, memberClass, memberCode, subsystem, service, version);
                LOGGER.debug("Producer member succesfully created. Identifier format : \"instance.memberClass.memberCode.subsystem.service.version\".");
            }
            return producer;
        } catch (Exception ex) {
            LOGGER.warn("Creating producer member failed.");
            return null;
        }
    }
}
