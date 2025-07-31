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
package org.niis.xrd4j.common.deserializer;

import org.niis.xrd4j.common.exception.XRd4JException;
import org.niis.xrd4j.common.exception.XRd4JMissingMemberException;
import org.niis.xrd4j.common.member.ConsumerMember;
import org.niis.xrd4j.common.member.ObjectType;
import org.niis.xrd4j.common.member.ProducerMember;
import org.niis.xrd4j.common.member.SecurityServer;
import org.niis.xrd4j.common.util.Constants;
import org.niis.xrd4j.common.util.SOAPHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import jakarta.xml.soap.Node;
import jakarta.xml.soap.SOAPHeader;

import java.util.Map;

/**
 * This abstract class contains methods for deserializing X-Road headers from
 * SOAP to application specific objects.
 *
 * @author Petteri Kivimäki
 */
public abstract class AbstractHeaderDeserializer {

    /**
     * Log writer.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHeaderDeserializer.class);
    private static final String DESERIALIZE_LOG_PATTERN = "Deserialize \"{}\".";
    private static final String ELEMENT_FOUND_LOG_PATTERN = "Element found : \"{}\"";
    private static final String NOT_FOUND_LOG_PATTERN = "\"{}\" was not found.";

    /**
     * Deserializes the client element of the SOAP header to a ConsumerMember
     * object.
     *
     * @param header SOAP header to be deserialized
     * @return ConsumerMember object
     * @throws XRd4JException if there's a XRd4J error
     * @throws XRd4JMissingMemberException if ConsumerMember is missing
     */
    protected final ConsumerMember deserializeConsumer(final SOAPHeader header)
            throws XRd4JException, XRd4JMissingMemberException {
        LOGGER.debug(DESERIALIZE_LOG_PATTERN, Constants.NS_XRD_ELEM_CLIENT);
        // Client headers
        Map<String, String> client;
        // Client object type
        ObjectType clientObjectType;

        NodeList list = header.getElementsByTagNameNS(Constants.NS_XRD_URL, Constants.NS_XRD_ELEM_CLIENT);
        if (list.getLength() == 1) {
            clientObjectType = this.deserializeObjectType((Node) list.item(0));
            client = SOAPHelper.nodesToMap(list.item(0).getChildNodes());
            LOGGER.trace(ELEMENT_FOUND_LOG_PATTERN, Constants.NS_XRD_ELEM_CLIENT);
            return this.getConsumerMember(client, clientObjectType);
        }
        LOGGER.warn("\"{}\" element missing from SOAP header.", Constants.NS_XRD_ELEM_CLIENT);
        throw new XRd4JMissingMemberException("Client element is missing from SOAP header.");
    }

    /**
     * Deserializes the service element of the SOAP header to a ProducerMember
     * object.
     *
     * @param header SOAP header to be deserialized
     * @return ProducerMember object
     * @throws XRd4JException if there's a XRd4J error
     * @throws XRd4JMissingMemberException if ProducerMember is missing
     */
    protected final ProducerMember deserializeProducer(final SOAPHeader header)
            throws XRd4JException, XRd4JMissingMemberException {
        LOGGER.debug(DESERIALIZE_LOG_PATTERN, Constants.NS_XRD_ELEM_SERVICE);
        // Service headers
        Map<String, String> service;
        // Service object type
        ObjectType serviceObjectType;

        NodeList list = header.getElementsByTagNameNS(Constants.NS_XRD_URL, Constants.NS_XRD_ELEM_SERVICE);
        if (list.getLength() == 1) {
            serviceObjectType = this.deserializeObjectType((Node) list.item(0));
            service = SOAPHelper.nodesToMap(list.item(0).getChildNodes());
            LOGGER.trace(ELEMENT_FOUND_LOG_PATTERN, Constants.NS_XRD_ELEM_SERVICE);
            return this.getProducerMember(service, serviceObjectType);
        }
        LOGGER.warn("\"{}\" element missing from SOAP header.", Constants.NS_XRD_ELEM_SERVICE);
        throw new XRd4JMissingMemberException("Service element is missing from SOAP header.");
    }

    /**
     * Deserializes the service element of the SOAP header to a SecurityServer
     * object.
     *
     * @param header SOAP header to be deserialized
     * @return SecurityServer object or null
     * @throws XRd4JException if there's a XRd4J error
     */
    protected final SecurityServer deserializeSecurityServer(final SOAPHeader header)
            throws XRd4JException {
        LOGGER.debug(DESERIALIZE_LOG_PATTERN, Constants.NS_XRD_ELEM_SECURITY_SERVER);
        // Security server headers
        Map<String, String> server;

        NodeList list = header.getElementsByTagNameNS(Constants.NS_XRD_URL, Constants.NS_XRD_ELEM_SECURITY_SERVER);
        if (list.getLength() == 1) {
            server = SOAPHelper.nodesToMap(list.item(0).getChildNodes());
            LOGGER.trace(ELEMENT_FOUND_LOG_PATTERN, Constants.NS_XRD_ELEM_SECURITY_SERVER);
            return this.getSecurityServer(server);
        }
        return null;
    }

    /**
     * Deserializes the id element of the SOAP header to a String.
     *
     * @param header SOAP header to be deserialized
     * @return id represented as a String
     */
    protected final String deserializeId(final SOAPHeader header) {
        LOGGER.debug(DESERIALIZE_LOG_PATTERN, Constants.NS_XRD_ELEM_ID);
        String id = null;
        NodeList list = header.getElementsByTagNameNS(Constants.NS_XRD_URL, Constants.NS_XRD_ELEM_ID);
        if (list.getLength() == 1) {
            id = list.item(0).getTextContent();
            LOGGER.trace(ELEMENT_FOUND_LOG_PATTERN, Constants.NS_XRD_ELEM_ID);
        }
        return id;
    }

    /**
     * Deserializes the userId element of the SOAP header to a String.
     *
     * @param header SOAP header to be deserialized
     * @return userId represented as a String
     */
    protected final String deserializeUserId(final SOAPHeader header) {
        LOGGER.debug(DESERIALIZE_LOG_PATTERN, Constants.NS_XRD_ELEM_USER_ID);
        String userId = null;
        NodeList list = header.getElementsByTagNameNS(Constants.NS_XRD_URL, Constants.NS_XRD_ELEM_USER_ID);
        if (list.getLength() == 1) {
            userId = list.item(0).getTextContent();
            LOGGER.trace(ELEMENT_FOUND_LOG_PATTERN, Constants.NS_XRD_ELEM_USER_ID);
        }
        return userId;
    }

    /**
     * Deserializes the issue element of the SOAP header to a String.
     *
     * @param header SOAP header to be deserialized
     * @return issue represented as a String
     */
    protected final String deserializeIssue(final SOAPHeader header) {
        LOGGER.debug(DESERIALIZE_LOG_PATTERN, Constants.NS_XRD_ELEM_ISSUE);
        String issue = null;
        NodeList list = header.getElementsByTagNameNS(Constants.NS_XRD_URL, Constants.NS_XRD_ELEM_ISSUE);
        if (list.getLength() == 1) {
            issue = list.item(0).getTextContent();
            LOGGER.trace(ELEMENT_FOUND_LOG_PATTERN, Constants.NS_XRD_ELEM_ISSUE);
        }
        return issue;
    }

    /**
     * Deserializes the requestHash element of the SOAP header to a String.
     *
     * @param header SOAP header to be deserialized
     * @return requestHash represented as a String
     */
    protected final String deserializeRequestHash(final SOAPHeader header) {
        LOGGER.debug(DESERIALIZE_LOG_PATTERN, Constants.NS_XRD_ELEM_REQUEST_HASH);
        String requestHash = null;
        NodeList list = header.getElementsByTagNameNS(Constants.NS_XRD_URL, Constants.NS_XRD_ELEM_REQUEST_HASH);
        if (list.getLength() == 1) {
            requestHash = list.item(0).getTextContent();
            LOGGER.trace(ELEMENT_FOUND_LOG_PATTERN, Constants.NS_XRD_ELEM_REQUEST_HASH);
        }
        return requestHash;
    }

    /**
     * Deserializes the algorithmId element of the SOAP header to a String.
     *
     * @param header SOAP header to be deserialized
     * @return algorithmId represented as a String
     */
    protected final String deserializeAlgorithmId(final SOAPHeader header) {
        LOGGER.debug(DESERIALIZE_LOG_PATTERN, Constants.ATTR_ALGORITHM_ID);
        String algorithmId = null;
        NodeList list = header.getElementsByTagNameNS(Constants.NS_XRD_URL, Constants.NS_XRD_ELEM_REQUEST_HASH);
        if (list.getLength() == 1) {
            algorithmId = list.item(0).getAttributes().getNamedItem(Constants.ATTR_ALGORITHM_ID).getNodeValue();
            LOGGER.trace("Attribute found : \"{}\"", Constants.ATTR_ALGORITHM_ID);
        }
        return algorithmId;
    }

    /**
     * Deserializes the protocol version element of the SOAP header to a String.
     *
     * @param header SOAP header to be deserialized
     * @return id represented as a String
     */
    protected final String deserializeProtocolVersion(final SOAPHeader header) {
        LOGGER.debug(DESERIALIZE_LOG_PATTERN, Constants.NS_XRD_ELEM_PROTOCOL_VERSION);
        String protocolVersion = null;
        NodeList list = header.getElementsByTagNameNS(Constants.NS_XRD_URL, Constants.NS_XRD_ELEM_PROTOCOL_VERSION);
        if (list.getLength() == 1) {
            protocolVersion = list.item(0).getTextContent();
            LOGGER.trace(ELEMENT_FOUND_LOG_PATTERN, Constants.NS_XRD_ELEM_PROTOCOL_VERSION);
        }
        return protocolVersion;
    }

    /**
     * Deserializes the security token element of the SOAP header to a String.
     *
     * @param header SOAP header to be deserialized
     * @return security token represented as a String
     */
    protected final String deserializeSecurityToken(final SOAPHeader header) {
        LOGGER.debug(DESERIALIZE_LOG_PATTERN, Constants.NS_EXT_ELEM_SECURITY_TOKEN);
        String securityToken = null;
        NodeList list = header.getElementsByTagNameNS(Constants.NS_EXT_SECURITY_TOKEN_URL, Constants.NS_EXT_ELEM_SECURITY_TOKEN);
        if (list.getLength() == 1) {
            securityToken = list.item(0).getTextContent();
            LOGGER.trace(ELEMENT_FOUND_LOG_PATTERN, Constants.NS_EXT_ELEM_SECURITY_TOKEN);
        }
        return securityToken;
    }

    /**
     * Deserializes the tokenType element from the given Node to a String.
     *
     * @param header SOAP header to be deserialized
     * @return security token represented as a String
     */
    protected String deserializeTokenType(final SOAPHeader header) {
        LOGGER.debug(DESERIALIZE_LOG_PATTERN, Constants.NS_EXT_ATTR_TOKEN_TYPE);
        String securityTokenType = null;
        NodeList list = header.getElementsByTagNameNS(Constants.NS_EXT_SECURITY_TOKEN_URL, Constants.NS_EXT_ELEM_SECURITY_TOKEN);
        if (list.getLength() == 1) {
            NamedNodeMap attrs = list.item(0).getAttributes();
            // Try to get token type without namespace.
            org.w3c.dom.Node tokenType = attrs.getNamedItem(Constants.NS_EXT_ATTR_TOKEN_TYPE);
            // If toke type is null, try with namespace.
            if (tokenType == null) {
                tokenType = attrs.getNamedItemNS(Constants.NS_EXT_SECURITY_TOKEN_URL, Constants.NS_EXT_ATTR_TOKEN_TYPE);
            }
            securityTokenType = tokenType != null ? tokenType.getNodeValue() : null;
        }
        return securityTokenType;
    }

    /**
     * Deserializes the objectType element from the given Node to an ObjectType
     * object.
     *
     * @param node Node to be deserialized
     * @return ObjectType
     */
    protected ObjectType deserializeObjectType(final org.w3c.dom.Node node) {
        LOGGER.debug(DESERIALIZE_LOG_PATTERN, Constants.NS_ID_ATTR_OBJECT_TYPE);
        NamedNodeMap attrs = node.getAttributes();
        org.w3c.dom.Node objectType = attrs.getNamedItemNS(Constants.NS_ID_URL, Constants.NS_ID_ATTR_OBJECT_TYPE);
        return Enum.valueOf(ObjectType.class, objectType.getNodeValue().toUpperCase());
    }

    /**
     * Creates a new ConsumerMember object.
     *
     * @param map Map containing instance variables as key-value-pairs
     * @param objectType ObjectType of the ConsumerMember object
     * @return new ConsumerMember object
     * @throws XRd4JException if there's a XRd4J error
     */
    protected ConsumerMember getConsumerMember(final Map<String, String> map, final ObjectType objectType)
            throws XRd4JException {
        LOGGER.debug("Create a new ConsumerMember.");
        String xRoadInstance = this.getXRoadInstance(map);
        String memberClass = this.getMemberClass(map);
        String memberCode = this.getMemberCode(map);
        String subsystemCode = this.getSubsystemCode(map);
        ConsumerMember consumer;
        if (objectType == ObjectType.MEMBER) {
            consumer = new ConsumerMember(xRoadInstance, memberClass, memberCode);
        } else {
            consumer = new ConsumerMember(xRoadInstance, memberClass, memberCode, subsystemCode);
        }
        consumer.setObjectType(objectType);
        LOGGER.debug("New ConsumerMember (\"{}\") was succesfully created : \"{}\"", objectType.toString(), consumer.toString());
        return consumer;
    }

    /**
     * Creates a new ProducerMember object.
     *
     * @param map Map containing instance variables as key-value-pairs
     * @param objectType ObjectType of the ProducerMember object
     * @return new ProducerMember object
     * @throws XRd4JException if there's a XRd4J error
     */
    protected ProducerMember getProducerMember(final Map<String, String> map, final ObjectType objectType)
            throws XRd4JException {
        LOGGER.debug("Create a new ProducerMember.");
        String xRoadInstance = this.getXRoadInstance(map);
        String memberClass = this.getMemberClass(map);
        String memberCode = this.getMemberCode(map);
        String subsystemCode = this.getSubsystemCode(map);
        String serviceCode = this.getServiceCode(map);
        String serviceVersion = this.getServiceVersion(map);
        ProducerMember producer;
        if (objectType == ObjectType.SERVICE) {
            producer = new ProducerMember(xRoadInstance, memberClass, memberCode, serviceCode);
            producer.setSubsystemCode(subsystemCode);
            producer.setServiceVersion(serviceVersion);
        } else {
            producer = new ProducerMember(xRoadInstance, serviceCode);
        }
        producer.setObjectType(objectType);
        LOGGER.debug("New ProducerMember (\"{}\") was succesfully created : \"{}\"", objectType.toString(), producer.toString());
        return producer;
    }

    /**
     * Creates a new SecurityServer object.
     *
     * @param map Map containing instance variables as key-value-pairs
     * @return new SecurityServer object
     * @throws XRd4JException if there's a XRd4J error
     */
    protected SecurityServer getSecurityServer(final Map<String, String> map)
            throws XRd4JException {
        LOGGER.debug("Create a new SecurityServer(.");
        String xRoadInstance = this.getXRoadInstance(map);
        String memberClass = this.getMemberClass(map);
        String memberCode = this.getMemberCode(map);
        String serverCode = this.getServerCode(map);
        SecurityServer server = new SecurityServer(xRoadInstance, memberClass, memberCode, serverCode);

        LOGGER.debug("New SecurityServer was succesfully created : \"{}\"", server.toString());
        return server;
    }

    /**
     * Reads the value of the "xRoadInstance" key from the given Map and returns
     * the value of that key . If no "xRoadInstance" key is found, null is
     * returned.
     *
     * @param map Map containing Member related variables as key-value-pairs
     * @return xRoadInstance or null
     * @throws XRd4JException if there's a XRd4J error
     */
    private String getXRoadInstance(final Map<String, String> map) {
        if (map.containsKey(Constants.NS_ID_ELEM_XROAD_INSTANCE)) {
            return map.get(Constants.NS_ID_ELEM_XROAD_INSTANCE);
        }
        LOGGER.warn(NOT_FOUND_LOG_PATTERN, Constants.NS_ID_ELEM_XROAD_INSTANCE);
        return null;
    }

    /**
     * Reads the value of the "memberClass" key from the given Map and returns
     * the value of that key . If no "memberClass" key is found, null is
     * returned.
     *
     * @param map Map containing Member related variables as key-value-pairs
     * @return MemberClass or null
     * @throws XRd4JException if there's a XRd4J error
     */
    private String getMemberClass(final Map<String, String> map)
            throws XRd4JException {
        if (map.containsKey(Constants.NS_ID_ELEM_MEMBER_CLASS)) {
            return map.get(Constants.NS_ID_ELEM_MEMBER_CLASS);
        }
        LOGGER.warn(NOT_FOUND_LOG_PATTERN, Constants.NS_ID_ELEM_MEMBER_CLASS);
        return null;
    }

    /**
     * Reads the value of the "memberCode" key from the given Map and returns
     * the value of that key . If no "memberCode" key is found, null is
     * returned.
     *
     * @param map Map containing Member related variables as key-value-pairs
     * @return member code as a String or null
     */
    private String getMemberCode(final Map<String, String> map) {
        if (map.containsKey(Constants.NS_ID_ELEM_MEMBER_CODE)) {
            return map.get(Constants.NS_ID_ELEM_MEMBER_CODE);
        }
        LOGGER.warn(NOT_FOUND_LOG_PATTERN, Constants.NS_ID_ELEM_MEMBER_CODE);
        return null;
    }

    /**
     * Reads the value of the "subsystemCode" key from the given Map and returns
     * the value of that key . If no "subsystemCode" key is found, null is
     * returned.
     *
     * @param map Map containing Member related variables as key-value-pairs
     * @return subsystem code as a String or null
     */
    private String getSubsystemCode(final Map<String, String> map) {
        if (map.containsKey(Constants.NS_ID_ELEM_SUBSYSTEM_CODE)) {
            return map.get(Constants.NS_ID_ELEM_SUBSYSTEM_CODE);
        }
        LOGGER.warn(NOT_FOUND_LOG_PATTERN, Constants.NS_ID_ELEM_SUBSYSTEM_CODE);
        return null;
    }

    /**
     * Reads the value of the "serverCode" key from the given Map and returns
     * the value of that key . If no "serverCode" key is found, null is
     * returned.
     *
     * @param map Map containing Member related variables as key-value-pairs
     * @return subsystem code as a String or null
     */
    private String getServerCode(final Map<String, String> map) {
        if (map.containsKey(Constants.NS_ID_ELEM_SERVER_CODE)) {
            return map.get(Constants.NS_ID_ELEM_SERVER_CODE);
        }
        LOGGER.warn(NOT_FOUND_LOG_PATTERN, Constants.NS_ID_ELEM_SERVER_CODE);
        return null;
    }

    /**
     * Reads the value of the "serviceCode" key from the given Map and returns
     * the value of that key . If no "serviceCode" key is found, null is
     * returned.
     *
     * @param map Map containing Member related variables as key-value-pairs
     * @return service code as a String or null
     */
    private String getServiceCode(final Map<String, String> map) {
        if (map.containsKey(Constants.NS_ID_ELEM_SERVICE_CODE)) {
            return map.get(Constants.NS_ID_ELEM_SERVICE_CODE);
        }
        LOGGER.warn(NOT_FOUND_LOG_PATTERN, Constants.NS_ID_ELEM_SERVICE_CODE);
        return null;
    }

    /**
     * Reads the value of the "serviceVersion" key from the given Map and
     * returns the value of that key . If no "serviceVersion" key is found, null
     * is returned.
     *
     * @param map Map containing Member related variables as key-value-pairs
     * @return service version as a String or null
     */
    private String getServiceVersion(final Map<String, String> map) {
        if (map.containsKey(Constants.NS_ID_ELEM_SERVICE_VERSION)) {
            return map.get(Constants.NS_ID_ELEM_SERVICE_VERSION);
        }
        LOGGER.info(NOT_FOUND_LOG_PATTERN, Constants.NS_ID_ELEM_SERVICE_VERSION);
        return null;
    }
}
