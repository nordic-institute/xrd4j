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
package org.niis.xrd4j.common.serializer;

import org.niis.xrd4j.common.member.ObjectType;
import org.niis.xrd4j.common.message.AbstractMessage;
import org.niis.xrd4j.common.message.ErrorMessageType;
import org.niis.xrd4j.common.util.Constants;
import org.niis.xrd4j.common.util.MessageHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPHeader;

/**
 * This abstract class contains methods for adding valid X-Road version 6 SOAP
 * headers to SOAP messages.
 *
 * @author Petteri Kivimäki
 */
public abstract class AbstractHeaderSerializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHeaderSerializer.class);
    private static final String GENERATE_ELEMENT = "Generate \"{}\" element.";
    private static final String ELEMENT_GENERATED = "\"{}\" element was succesfully generated.";

    /**
     * Adds X-Road version 6 SOAP headers to the given SOAP envelope. The given
     * message holds the actual data used in the headers.
     *
     * @param message request or response message that holds the data for the
     * headers
     * @param envelope SOAP envelope where SOAP headers are added
     * @throws SOAPException if there's a SOAP error
     */
    protected final void serializeHeader(final AbstractMessage message, final SOAPEnvelope envelope) throws SOAPException {
        LOGGER.debug("Generate SOAP header.");
        if (message.hasError() && message.getErrorMessage().getErrorMessageType() == ErrorMessageType.STANDARD_SOAP_ERROR_MESSAGE) {
            LOGGER.warn("Standard SOAP error detected. SOAP header is skipped.");
            return;
        }
        envelope.addNamespaceDeclaration(Constants.NS_ID_PREFIX, Constants.NS_ID_URL);
        envelope.addNamespaceDeclaration(Constants.NS_XRD_PREFIX, Constants.NS_XRD_URL);

        // Header - Start
        SOAPHeader header = envelope.getHeader();
        // Client - Start
        this.serializeClient(header, message, envelope);
        // Client - End
        // Service - Start
        this.serializeService(header, message, envelope);
        // Service - End
        // Security Server - Start
        if (message.getSecurityServer() != null) {
            this.serializeSecurityServer(header, message, envelope);
        }
        // SecurityServer - End
        if (message.getUserId() != null && !message.getUserId().isEmpty()) {
            LOGGER.debug(GENERATE_ELEMENT, Constants.NS_XRD_ELEM_USER_ID);
            SOAPElement userId = header.addChildElement(Constants.NS_XRD_ELEM_USER_ID, Constants.NS_XRD_PREFIX);
            userId.addTextNode(message.getUserId());
            LOGGER.debug(ELEMENT_GENERATED, Constants.NS_XRD_ELEM_USER_ID);
        }
        LOGGER.debug(GENERATE_ELEMENT, Constants.NS_XRD_ELEM_ID);
        SOAPElement id = header.addChildElement(Constants.NS_XRD_ELEM_ID, Constants.NS_XRD_PREFIX);
        id.addTextNode(message.getId());
        LOGGER.debug(ELEMENT_GENERATED, Constants.NS_XRD_ELEM_ID);
        if (message.getIssue() != null && !message.getIssue().isEmpty()) {
            LOGGER.debug(GENERATE_ELEMENT, Constants.NS_XRD_ELEM_ISSUE);
            SOAPElement issue = header.addChildElement(Constants.NS_XRD_ELEM_ISSUE, Constants.NS_XRD_PREFIX);
            issue.addTextNode(message.getIssue());
            LOGGER.debug(ELEMENT_GENERATED, Constants.NS_XRD_ELEM_ISSUE);
        }
        if (message.getSecurityToken() != null && !message.getSecurityToken().isEmpty()) {
            envelope.addNamespaceDeclaration(Constants.NS_EXT_SECURITY_TOKEN_PREFIX, Constants.NS_EXT_SECURITY_TOKEN_URL);
            LOGGER.debug(GENERATE_ELEMENT, Constants.NS_EXT_ELEM_SECURITY_TOKEN);
            SOAPElement securityToken = header.addChildElement(Constants.NS_EXT_ELEM_SECURITY_TOKEN, Constants.NS_EXT_SECURITY_TOKEN_PREFIX);
            if (message.getSecurityTokenType() != null && !message.getSecurityTokenType().isEmpty()) {
                securityToken.addAttribute(
                        envelope.createQName(Constants.NS_EXT_ATTR_TOKEN_TYPE, Constants.NS_EXT_SECURITY_TOKEN_PREFIX),
                        message.getSecurityTokenType()
                );
            }
            securityToken.addTextNode(message.getSecurityToken());
            LOGGER.debug(ELEMENT_GENERATED, Constants.NS_EXT_ELEM_SECURITY_TOKEN);
        }
        LOGGER.debug(GENERATE_ELEMENT, Constants.NS_XRD_ELEM_PROTOCOL_VERSION);
        SOAPElement protocolVersion = header.addChildElement(Constants.NS_XRD_ELEM_PROTOCOL_VERSION, Constants.NS_XRD_PREFIX);
        protocolVersion.addTextNode(message.getProtocolVersion());
        LOGGER.debug(ELEMENT_GENERATED, Constants.NS_XRD_ELEM_PROTOCOL_VERSION);
        // Header - End
        LOGGER.debug("SOAP header was generated succesfully.");
    }

    private void serializeClient(final SOAPHeader header, final AbstractMessage message, final SOAPEnvelope envelope) throws SOAPException {
        LOGGER.debug("Generate \"Client\" element.");
        ObjectType clientObjectType = MessageHelper.getObjectType(message.getConsumer());
        SOAPElement clientHeader = header.addChildElement(Constants.NS_XRD_ELEM_CLIENT, Constants.NS_XRD_PREFIX);
        clientHeader.addAttribute(envelope.createQName(Constants.NS_ID_ATTR_OBJECT_TYPE, Constants.NS_ID_PREFIX), clientObjectType.toString());
        SOAPElement xRoadInstance = clientHeader.addChildElement(Constants.NS_ID_ELEM_XROAD_INSTANCE, Constants.NS_ID_PREFIX);
        xRoadInstance.addTextNode(message.getConsumer().getXRoadInstance());
        SOAPElement memberClass = clientHeader.addChildElement(Constants.NS_ID_ELEM_MEMBER_CLASS, Constants.NS_ID_PREFIX);
        memberClass.addTextNode(message.getConsumer().getMemberClass());
        SOAPElement memberCode = clientHeader.addChildElement(Constants.NS_ID_ELEM_MEMBER_CODE, Constants.NS_ID_PREFIX);
        memberCode.addTextNode(message.getConsumer().getMemberCode());
        if (clientObjectType == ObjectType.SUBSYSTEM) {
            SOAPElement subsystem = clientHeader.addChildElement(Constants.NS_ID_ELEM_SUBSYSTEM_CODE, Constants.NS_ID_PREFIX);
            subsystem.addTextNode(message.getConsumer().getSubsystemCode());
        }
        LOGGER.debug("\"Client\" element was succesfully generated.");
    }

    private void serializeService(final SOAPHeader header, final AbstractMessage message, final SOAPEnvelope envelope) throws SOAPException {
        LOGGER.debug("Generate \"Service\" element.");
        ObjectType serviceObjectType = MessageHelper.getObjectType(message.getProducer());
        SOAPElement serviceHeader = header.addChildElement(Constants.NS_XRD_ELEM_SERVICE, Constants.NS_XRD_PREFIX);
        serviceHeader.addAttribute(envelope.createQName(Constants.NS_ID_ATTR_OBJECT_TYPE, Constants.NS_ID_PREFIX), serviceObjectType.toString());
        SOAPElement xRoadInstance = serviceHeader.addChildElement(Constants.NS_ID_ELEM_XROAD_INSTANCE, Constants.NS_ID_PREFIX);
        xRoadInstance.addTextNode(message.getProducer().getXRoadInstance());
        if (serviceObjectType == ObjectType.SERVICE) {
            SOAPElement memberClass = serviceHeader.addChildElement(Constants.NS_ID_ELEM_MEMBER_CLASS, Constants.NS_ID_PREFIX);
            memberClass.addTextNode(message.getProducer().getMemberClass());
            SOAPElement memberCode = serviceHeader.addChildElement(Constants.NS_ID_ELEM_MEMBER_CODE, Constants.NS_ID_PREFIX);
            memberCode.addTextNode(message.getProducer().getMemberCode());
        }
        if (message.getProducer().getSubsystemCode() != null && !message.getProducer().getSubsystemCode().isEmpty()) {
            SOAPElement subsystem = serviceHeader.addChildElement(Constants.NS_ID_ELEM_SUBSYSTEM_CODE, Constants.NS_ID_PREFIX);
            subsystem.addTextNode(message.getProducer().getSubsystemCode());
        }
        SOAPElement serviceCode = serviceHeader.addChildElement(Constants.NS_ID_ELEM_SERVICE_CODE, Constants.NS_ID_PREFIX);
        serviceCode.addTextNode(message.getProducer().getServiceCode());
        if (message.getProducer().getServiceVersion() != null && !message.getProducer().getServiceVersion().isEmpty()) {
            SOAPElement serviceVersion = serviceHeader.addChildElement(Constants.NS_ID_ELEM_SERVICE_VERSION, Constants.NS_ID_PREFIX);
            serviceVersion.addTextNode(message.getProducer().getServiceVersion());
        }
        LOGGER.debug("\"Service\" element was succesfully generated.");
    }

    private void serializeSecurityServer(final SOAPHeader header, final AbstractMessage message, final SOAPEnvelope envelope) throws SOAPException {
        LOGGER.debug("Generate \"SecurityServer\" element.");
        SOAPElement securityServerHeader = header.addChildElement(Constants.NS_XRD_ELEM_SECURITY_SERVER, Constants.NS_XRD_PREFIX);
        securityServerHeader.addAttribute(envelope.createQName(Constants.NS_ID_ATTR_OBJECT_TYPE, Constants.NS_ID_PREFIX), ObjectType.SERVER.toString());
        SOAPElement xRoadInstance = securityServerHeader.addChildElement(Constants.NS_ID_ELEM_XROAD_INSTANCE, Constants.NS_ID_PREFIX);
        xRoadInstance.addTextNode(message.getSecurityServer().getXRoadInstance());
        SOAPElement memberClass = securityServerHeader.addChildElement(Constants.NS_ID_ELEM_MEMBER_CLASS, Constants.NS_ID_PREFIX);
        memberClass.addTextNode(message.getSecurityServer().getMemberClass());
        SOAPElement memberCode = securityServerHeader.addChildElement(Constants.NS_ID_ELEM_MEMBER_CODE, Constants.NS_ID_PREFIX);
        memberCode.addTextNode(message.getSecurityServer().getMemberCode());
        SOAPElement serverCode = securityServerHeader.addChildElement(Constants.NS_ID_ELEM_SERVER_CODE, Constants.NS_ID_PREFIX);
        serverCode.addTextNode(message.getSecurityServer().getServerCode());
        LOGGER.debug("\"SecurityServer\" element was succesfully generated.");
    }
}
