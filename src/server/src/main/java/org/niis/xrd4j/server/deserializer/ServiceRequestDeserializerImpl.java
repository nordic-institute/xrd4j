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
package org.niis.xrd4j.server.deserializer;

import org.niis.xrd4j.common.deserializer.AbstractHeaderDeserializer;
import org.niis.xrd4j.common.exception.XRd4JException;
import org.niis.xrd4j.common.exception.XRd4JMissingMemberException;
import org.niis.xrd4j.common.member.ConsumerMember;
import org.niis.xrd4j.common.member.ProducerMember;
import org.niis.xrd4j.common.member.SecurityServer;
import org.niis.xrd4j.common.message.ServiceRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPHeader;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.soap.SOAPPart;

/**
 * This class offers methods for deserializing SOAPMessages to ServiceRequest
 * objects. This class serializes only SOAP headers as SOAP body contains
 * application specific content, and therefore an application specific
 * deserializer is needed for SOAP body.
 *
 * This deserializer can be used by an adapter service for handling incoming
 * requests. SOAP header contains the information about the service that the
 * consumer is calling, and the information can be accessed through the
 * ServiceRequest object returned by this class. Application specific
 * deserializers must extend the abstract AbstractCustomRequestDeserializer
 * class.
 *
 * @author Petteri Kivimäki
 */
public class ServiceRequestDeserializerImpl extends AbstractHeaderDeserializer implements ServiceRequestDeserializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRequestDeserializerImpl.class);

    /**
     * Deserializes the given SOAPMessage object to ServiceRequest object. Only
     * SOAP header is deserialized. An application specific serializer is needed
     * for SOAP body.
     *
     * @param message SOAP message to be deserialized
     * @return ServiceRequest object that represents the given SOAPMessage
     * object or null if the operation failed
     * @throws SOAPException if there's a SOAP error
     * @throws XRd4JException if there's a XRd4J error
     */
    @Override
    public final ServiceRequest deserialize(final SOAPMessage message) throws XRd4JException, SOAPException {
        LOGGER.debug("Deserialize SOAP message.");
        SOAPPart mySPart = message.getSOAPPart();
        SOAPEnvelope envelope = mySPart.getEnvelope();

        // Desearialize header
        ServiceRequest request = this.deserializeHeader(envelope.getHeader());
        request.setSoapMessage(message);
        
        LOGGER.debug("SOAP message header was succesfully deserialized.");
        return request;
    }

    /**
     * Deserializes the given SOAPHeader object to ServiceRequest object.
     *
     * @param header SOAP header to be deserialized
     * @return ServiceRequest object that contains the given SOAP header
     * @throws SOAPException if there's a SOAP error
     * @throws XRd4JException if there's a XRd4J exception
     */
    private ServiceRequest deserializeHeader(final SOAPHeader header) throws SOAPException, XRd4JException {
        LOGGER.debug("Deserialize SOAP header.");
        // Check that SOAP header exists
        if (header == null || header.getChildNodes().getLength() == 0) {
            LOGGER.warn("No SOAP header or an empty SOAP header was found.");
            return new ServiceRequest();
        }
        // Client headers
        String id = super.deserializeId(header);
        String userId = super.deserializeUserId(header);
        String issue = super.deserializeIssue(header);
        String protocolVersion = super.deserializeProtocolVersion(header);
        String securityToken = super.deserializeSecurityToken(header);
        String securityTokenType = super.deserializeTokenType(header);

        // Create objects
        ConsumerMember consumer = null;
        ProducerMember producer = null;
        SecurityServer securityServer = null;
        try {
            consumer = super.deserializeConsumer(header);
        } catch (XRd4JMissingMemberException ex) {
            LOGGER.warn("Deserializing \"ConsumerMember\" failed.");
        }
        try {
            producer = super.deserializeProducer(header);
        } catch (XRd4JMissingMemberException ex) {
            LOGGER.warn("Deserializing \"ProducerMember\" failed.");
        }
        try {
            // Not mandatory - can be null
            securityServer = super.deserializeSecurityServer(header);
        } catch (XRd4JException ex) {
            LOGGER.warn("Deserializing \"SecurityServer\" failed.");
        }
        ServiceRequest request = new ServiceRequest(consumer, producer, id);
        request.setSecurityServer(securityServer);
        request.setUserId(userId);
        request.setIssue(issue);
        request.setProtocolVersion(protocolVersion);
        request.setSecurityToken(securityToken);
        request.setSecurityTokenType(securityTokenType);

        LOGGER.debug("SOAP header was succesfully deserialized.");
        // Return request
        return request;
    }
}
