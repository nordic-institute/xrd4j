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

import org.niis.xrd4j.common.exception.XRd4JException;
import org.niis.xrd4j.common.message.ErrorMessage;
import org.niis.xrd4j.common.message.ServiceRequest;
import org.niis.xrd4j.common.util.SOAPHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;

import jakarta.xml.soap.Node;
import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.soap.SOAPPart;

/**
 * This abstract class serves as a base class for all the application specific
 * request deserializers. Each adapter server must implement application
 * specific request deserializer for each supported request type. Application
 * specific deserializers take care of deserializing SOAP body's request
 * element to application specific object that can be used for handling
 * the request. Each application specific deserializer must implement the
 * abstract deserializeRequest method defined in this class,
 *
 * @param <T> runtime type of the request data
 * @author Petteri Kivimäki
 */
public abstract class AbstractCustomRequestDeserializer<T> implements CustomRequestDeserializer<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCustomRequestDeserializer.class);

    /**
     * Deserializes SOAP body's request element to application specific
     * object. The given Node contains the request element to be deserialized.
     * @param requestNode Node which the request element to be deserialized
     * @param message SOAPMessage object that contains the whole SOAP request
     * @return application specific object that represents the request element
     * @throws SOAPException if there's a SOAP error
     */
    protected abstract T deserializeRequest(Node requestNode, SOAPMessage message) throws SOAPException;

    /**
     * Deserializes SOAP body's request element to application specific
     * object.
     * @param request ServiceRequest holding the SOAPMessage object
     * @throws SOAPException if there's a SOAP error
     * @throws XRd4JException if there's a XRd4J error
     */
    @Override
    public final void deserialize(final ServiceRequest<T> request) throws SOAPException, XRd4JException {
        this.deserialize(request, "*");
    }

    /**
     * Deserializes SOAP body's request element to application specific
     * object. If service producer's namespace URI is given, then it's used for
     * finding the response from the SOAP mesagge's body. Value "*" means
     * that the namespace is ignored.
     * @param request ServiceRequest holding the SOAPMessage object
     * @param producerNamespaceURI service producer's namespace URI
     * @throws SOAPException if there's a SOAP error
     * @throws XRd4JException if there's a XRd4J error
     */
    @Override
    public final void deserialize(final ServiceRequest<T> request, final String producerNamespaceURI) throws SOAPException, XRd4JException {
        LOGGER.debug("Deserialize SOAP body. Use \"{}\" namespace URI.", producerNamespaceURI);

        SOAPPart mySPart = request.getSoapMessage().getSOAPPart();
        SOAPEnvelope envelope = mySPart.getEnvelope();
        SOAPBody body = envelope.getBody();
        // Get request
        NodeList list = body.getElementsByTagNameNS(producerNamespaceURI, request.getProducer().getServiceCode());
        if (list.getLength() == 1) {
            Node requestNode;
            LOGGER.debug("Found service request element.");
            // Check if it is needed to process "request" and "response" wrappers
            if (request.isProcessingWrappers()) {
                LOGGER.debug("Processing \"request\" wrapper in request message.");
                requestNode = SOAPHelper.getNode((Node) list.item(0), "request");
            } else {
                LOGGER.debug("Skipping procession of \"request\" wrapper in request message.");
                requestNode = (Node) list.item(0);
            }
            LOGGER.debug("Deserialize request element.");
            T requestData = this.deserializeRequest(requestNode, request.getSoapMessage());
            request.setRequestData(requestData);
            LOGGER.debug("Request element was succesfully deserialized.");
            request.getProducer().setNamespaceUrl(list.item(0).getNamespaceURI());
            request.getProducer().setNamespacePrefix(list.item(0).getPrefix());
            LOGGER.debug("SOAP body was succesfully deserialized.");
        } else {
            String msg = "Request body is missing.";
            if (!"*".equals(producerNamespaceURI)) {
                LOGGER.debug("No service request element was found. Try again without namepsace URI.");
                list = body.getElementsByTagNameNS("*", request.getProducer().getServiceCode());
                if (list.getLength() == 1) {
                    LOGGER.warn("Service request element was found, but with wrong namespace URI : \"{}\".", list.item(0).getNamespaceURI());
                    msg = "Wrong namespace URI.";
                }
            }
            LOGGER.warn("Service request element was not deserialized. SOAP Fault is set.");
            ErrorMessage errorMessage = new ErrorMessage("SOAP-ENV:Client", msg, null, null);
            request.setErrorMessage(errorMessage);
            throw new XRd4JException("Request body was not found.");
        }
    }
}
