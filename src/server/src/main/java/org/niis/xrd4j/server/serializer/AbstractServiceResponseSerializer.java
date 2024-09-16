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
package org.niis.xrd4j.server.serializer;

import org.niis.xrd4j.common.exception.XRd4JException;
import org.niis.xrd4j.common.message.ErrorMessage;
import org.niis.xrd4j.common.message.ErrorMessageType;
import org.niis.xrd4j.common.message.ServiceRequest;
import org.niis.xrd4j.common.message.ServiceResponse;
import org.niis.xrd4j.common.serializer.AbstractHeaderSerializer;
import org.niis.xrd4j.common.util.SOAPHelper;

import jakarta.xml.soap.Name;
import jakarta.xml.soap.Node;
import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPBodyElement;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * This abstract class serves as base class for serializer classes that
 * serialize ServiceResponse objects to SOAPMessage objects. All the subclasses
 * must implement the serializeResponse method which takes care of serializing
 * application specific response object to SOAP body's response element. This
 * class takes care of adding all the required SOAP headers.
 *
 * @author Petteri Kivimäki
 */
public abstract class AbstractServiceResponseSerializer extends AbstractHeaderSerializer implements ServiceResponseSerializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractServiceResponseSerializer.class);
    
    /**
     * Serializes the application specific response part to SOAP body's response
     * element. All the children under response element will use provider's
     * namespace. Namespace prefix is added automatically.
     *
     * @param response ServiceResponse holding the application specific response
     * object
     * @param soapResponse SOAPMessage's response object where the response
     * element is added
     * @param envelope SOAPMessage's SOAPEnvelope object
     * @throws SOAPException if there's a SOAP error
     */
    protected abstract void serializeResponse(ServiceResponse response, SOAPElement soapResponse, SOAPEnvelope envelope) throws SOAPException;

    /**
     * Serializes the given ServiceResponse object to SOAPMessage object.
     *
     * @param response ServiceResponse to be serialized
     * @param request ServiceRequest that initiated the service call
     * @return SOAPMessage representing the given ServiceRequest; null if the
     * operation fails
     */
    @Override
    public final SOAPMessage serialize(final ServiceResponse response, final ServiceRequest request) {
        try {
            // Response must process wrappers in the same way as in request.
            // Unit tests might use null request.
            if (request != null) {
                LOGGER.debug("Setting response to process wrappers in the same way as in request.");
                response.setProcessingWrappers(request.isProcessingWrappers());
            }

            LOGGER.debug("Serialize ServiceResponse message to SOAP.");

            SOAPMessage message = createNewMessage();
            response.setSoapMessage(message);

            // If response has SOAP Fault, skip header
            if (response.hasError() && response.getErrorMessage().getErrorMessageType() == ErrorMessageType.STANDARD_SOAP_ERROR_MESSAGE) {
                LOGGER.warn("Standard SOAP error detected. SOAP header is skipped.");
                this.serializeSOAPFault(response);
            } else {
                // Check request for null
                if (request == null) {
                    throw new NullPointerException("Request can not be null.");
                }
                // Generate header by copying it from the request
                // Request and response MUST have the same headers
                message = SOAPHelper.cloneSOAPMsgWithoutBody(request.getSoapMessage());
                response.setSoapMessage(message);
                try {
                    // Generate body
                    this.serializeBody(response, request.getSoapMessage());
                } catch (XRd4JException ex) {
                    // Producer namespace URI is missing, response can't be
                    // generated
                    LOGGER.error(ex.getMessage(), ex);
                    LOGGER.warn("Drop headers and return SOAP Fault.");
                    message = createNewMessage();
                    response.setSoapMessage(message);
                    ErrorMessage errorMessage = new ErrorMessage("SOAP-ENV:Server", "Internal server error.", "", "");
                    response.setErrorMessage(errorMessage);
                    this.serializeSOAPFault(response);
                }
            }

            LOGGER.debug("ServiceResponse message was serialized succesfully.");
            return message;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        LOGGER.warn("Failed to serialize ServiceResponse message to SOAP.");
        return null;
    }

    private SOAPMessage createNewMessage() throws SOAPException {
        return SOAPHelper.createSOAPMessage();
    }

    /**
     * Generates SOAP body, including the request and response elements.
     *
     * @param response ServiceResponse to be serialized
     * @param soapRequest request's SOAP message object that's used for copying
     * the request element
     * @throws SOAPException if there's a SOAP error
     */
    private void serializeBody(final ServiceResponse response, final SOAPMessage soapRequest) throws SOAPException, XRd4JException {
        LOGGER.debug("Generate SOAP body.");
        if (response.isAddNamespaceToServiceResponse() || response.isAddNamespaceToRequest() || response.isAddNamespaceToResponse()) {
            if (response.getProducer().getNamespaceUrl() == null || response.getProducer().getNamespaceUrl().isEmpty()) {
                throw new XRd4JException("Producer namespace URI can't be null or empty.");
            }
            LOGGER.debug("Producer namespace \"{}\".", response.getProducer().getNamespaceUrl());
        }

        // Body - Start
        SOAPEnvelope envelope = response.getSoapMessage().getSOAPPart().getEnvelope();
        SOAPBody body = envelope.getBody();
        Name bodyName;
        if (response.isAddNamespaceToServiceResponse()) {
            LOGGER.debug("Create service response with namespace.");
            bodyName = envelope.createName(response.getProducer().getServiceCode() + "Response",
                response.getProducer().getNamespacePrefix(), response.getProducer().getNamespaceUrl());
        } else {
            LOGGER.debug("Create service response without namespace.");
            bodyName = envelope.createName(response.getProducer().getServiceCode() + "Response");
        }
        SOAPBodyElement gltp = body.addBodyElement(bodyName);
        // Process SOAP body
        SOAPElement soapResponse = processBody(gltp, response, soapRequest, envelope);
        // Process the actual payload
        processBodyContent(soapResponse, response, envelope);
        LOGGER.debug("SOAP body was generated succesfully.");
    }

    private SOAPElement processBody(final SOAPBodyElement body, final ServiceResponse response,
                                    final SOAPMessage soapRequest, final SOAPEnvelope envelope) throws SOAPException {
        SOAPElement soapResponse;
        if (response.isProcessingWrappers()) {
            LOGGER.debug("Adding \"request\" and \"response\" wrappers to response message.");
            // Add request element
            processRequestNode(body, response, soapRequest, envelope);
            // Create response element
            soapResponse = body.addChildElement(envelope.createName("response"));
        } else {
            LOGGER.debug("Skipping addition of \"request\" and \"response\" wrappers to response message.");
            soapResponse = body;
        }
        return soapResponse;
    }

    private void processRequestNode(final SOAPBodyElement body, final ServiceResponse response,
                                    final SOAPMessage soapRequest, final SOAPEnvelope envelope) throws SOAPException {
        boolean requestFound = false;
        var list = childElementsByLocalName(soapRequest.getSOAPBody(), response.getProducer().getServiceCode());

        if (list.size() == 1) {
            // Copy request from soapRequest
            requestFound = copyRequestNode(list.get(0), body, response);
        }
        // It was not possible to copy the request element, so we must create it
        if (!requestFound) {
            SOAPElement temp = body.addChildElement(envelope.createName("request"));
            if (response.isAddNamespaceToRequest()) {
                LOGGER.debug("Add provider namespace to request element.");
                SOAPHelper.addNamespace(temp, response);
            }
        }
    }

    private List<SOAPElement> childElementsByLocalName(SOAPElement soapElement, String localName) {
        var childElementsIterator = soapElement.getChildElements();
        var stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(childElementsIterator, 0), false);
        return stream
                .filter(node -> node.getNodeType() == Node.ELEMENT_NODE)
                .filter(node -> node.getLocalName().equals(localName))
                .filter(node -> node instanceof SOAPElement)
                .map(node -> (SOAPElement) node)
                .collect(Collectors.toList());
    }

    private boolean copyRequestNode(final SOAPElement node, final SOAPBodyElement body, final ServiceResponse response) {
        for (Iterator<Node> it = node.getChildElements(); it.hasNext();) {
            var childNode = it.next();
            if (childNode.getNodeType() == Node.ELEMENT_NODE
                    && childNode instanceof SOAPElement
                    && "request".equals(childNode.getLocalName())) {
                try {
                    var childElement = (SOAPElement) childNode;
                    childElement = body.addChildElement(childElement);
                    if (response.isAddNamespaceToRequest()) {
                        LOGGER.debug("Add provider namespace to request element.");
                        SOAPHelper.addNamespace(childElement, response);
                    }
                    return true;
                } catch (SOAPException e) {
                    LOGGER.error("Failed to copy request element.", e);
                    throw new RuntimeException(e);
                }
            }
        }
        return false;
    }

    private void processBodyContent(final SOAPElement soapResponse, final ServiceResponse response, final SOAPEnvelope envelope) throws SOAPException {
        // Check if there's a non-technical SOAP error
        var sr = soapResponse;
        if (response.hasError()) {
            // Add namespace to the response element only, children excluded
            if (response.isAddNamespaceToResponse()) {
                LOGGER.debug("Add provider namespace to response element.");
                sr = SOAPHelper.addNamespace(sr, response);
            }
            LOGGER.warn("Non-technical SOAP error detected.");
            LOGGER.debug("Generate error message.");
            ErrorMessage errorMessage = response.getErrorMessage();
            if (errorMessage.getFaultCode() != null) {
                LOGGER.trace("Add \"faultcode\" element.");
                SOAPElement elem = sr.addChildElement(envelope.createName("faultcode"));
                elem.addTextNode(errorMessage.getFaultCode());
            }
            if (errorMessage.getFaultString() != null) {
                LOGGER.trace("Add \"faultstring\" element.");
                SOAPElement elem = sr.addChildElement(envelope.createName("faultstring"));
                elem.addTextNode(errorMessage.getFaultString());
            }
            LOGGER.debug("Error message was generated succesfully.");
        } else {
            LOGGER.trace("Passing processing to subclass implementing \"serializeResponse\" method.");
            // Generate response
            if (response.isAddNamespaceToResponse()) {
                LOGGER.debug("Add provider namespace to response element.");
                if (!response.isForceNamespaceToResponseChildren()) {
                    sr = SOAPHelper.addNamespace(sr, response);
                    this.serializeResponse(response, sr, envelope);
                } else {
                    LOGGER.debug("Add provider namespace to all the response element's child elements.");
                    this.serializeResponse(response, sr, envelope);
                    SOAPHelper.addNamespace(sr, response);
                }
            } else {
                LOGGER.debug("Don't add provider namespace to response element.");
                this.serializeResponse(response, sr, envelope);
            }
        }
    }

    /**
     * Serializes a standard SOAP error message to SOAP Fault.
     *
     * @param response ServiceResponse that contains the error
     * @throws SOAPException if there's a SOAP error
     */
    private void serializeSOAPFault(final ServiceResponse response) throws SOAPException {
        LOGGER.debug("Generate SOAP Fault.");
        SOAPEnvelope envelope = response.getSoapMessage().getSOAPPart().getEnvelope();
        SOAPBody body = envelope.getBody();
        Name bodyName = envelope.createName("Fault", "SOAP-ENV", "http://schemas.xmlsoap.org/soap/envelope/");
        SOAPBodyElement gltp = body.addBodyElement(bodyName);
        ErrorMessage errorMessage = response.getErrorMessage();
        if (errorMessage.getFaultCode() != null) {
            LOGGER.trace("Add \"faultcode\" element.");
            SOAPElement elem = gltp.addChildElement(envelope.createName("faultcode"));
            elem.addTextNode(errorMessage.getFaultCode());
        }
        if (errorMessage.getFaultString() != null) {
            LOGGER.trace("Add \"faultstring\" element.");
            SOAPElement elem = gltp.addChildElement(envelope.createName("faultstring"));
            elem.addTextNode(errorMessage.getFaultString());
        }
        if (errorMessage.getFaultActor() != null) {
            LOGGER.trace("Add \"faultactor\" element.");
            SOAPElement elem = gltp.addChildElement(envelope.createName("faultactor"));
            elem.addTextNode(errorMessage.getFaultActor());
        }
        if (errorMessage.getDetail() != null) {
            LOGGER.trace("Add \"detail\" element.");
            SOAPElement elem = gltp.addChildElement(envelope.createName("detail"));
            this.serializeSOAPFaultDetail(errorMessage, elem);
        }
        LOGGER.debug("SOAP Fault was generated succesfully.");
    }

    /**
     * Serializes SOAP Fault's detail element to String. If the detail element
     * contains a complex data type, this method must be overridden in a
     * subclass.
     *
     * @param errorMessage ErrorMessage that contains the detail element
     * @param faultDetail SOAPElement for the detail
     * @throws SOAPException if there's a SOAP error
     */
    protected void serializeSOAPFaultDetail(final ErrorMessage errorMessage, final SOAPElement faultDetail) throws SOAPException {
        LOGGER.trace("Using the default implementation for \"detail\" element.");
        faultDetail.addTextNode(errorMessage.getDetail().toString());
    }
}
