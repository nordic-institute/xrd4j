/**
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
package org.niis.xrd4j.server;

import org.niis.xrd4j.common.exception.XRd4JException;
import org.niis.xrd4j.common.message.ErrorMessage;
import org.niis.xrd4j.common.message.ServiceRequest;
import org.niis.xrd4j.common.message.ServiceResponse;
import org.niis.xrd4j.common.util.Constants;
import org.niis.xrd4j.common.util.FileUtil;
import org.niis.xrd4j.common.util.SOAPHelper;
import org.niis.xrd4j.server.deserializer.ServiceRequestDeserializer;
import org.niis.xrd4j.server.deserializer.ServiceRequestDeserializerImpl;
import org.niis.xrd4j.server.serializer.AbstractServiceResponseSerializer;
import org.niis.xrd4j.server.serializer.ServiceResponseSerializer;
import org.niis.xrd4j.server.utils.AdapterUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jakarta.xml.soap.MimeHeaders;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * This an abstract base class for Servlets that implement SOAP message
 * processing.
 *
 * @author Petteri Kivimäki
 */
public abstract class AbstractAdapterServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAdapterServlet.class);
    private static final String FAULT_CODE_CLIENT = "SOAP-ENV:Client";
    private final ServiceRequestDeserializer deserializer;
    private final ServiceResponseSerializer serializer;
    private final String errGetNotSupportedStr;
    private final String errWsdlNotFoundStr;
    private final String errInternalServerErrStr;
    private final ErrorMessage errGetNotSupported = new ErrorMessage(FAULT_CODE_CLIENT, "HTTP GET method not implemented", null, null);
    private final ErrorMessage errWsdlNotFound = new ErrorMessage(FAULT_CODE_CLIENT, "WSDL not found", null, null);
    private final ErrorMessage errInternalServerErr = new ErrorMessage(FAULT_CODE_CLIENT, "500 Internal Server Error", null, null);
    private final ErrorMessage errUnknownServiceCode = new ErrorMessage(FAULT_CODE_CLIENT, "Unknown service code.", null, null);

    /**
     * Constructor
     */
    public AbstractAdapterServlet() {
        LOGGER.debug("Starting to initialize AbstractServlet.");
        LOGGER.debug("Initialize deserializer");
        this.deserializer = new ServiceRequestDeserializerImpl();
        LOGGER.debug("Initialize serializer");
        this.serializer = new DummyServiceResponseSerializer();
        LOGGER.debug("Initialize \"errGetNotSupportedStr\" error message.");
        this.errGetNotSupportedStr = SOAPHelper.toString(this.errorToSOAP(this.errGetNotSupported, null));
        LOGGER.debug("Initialize \"errWsdlNotFoundStr\" error message.");
        this.errWsdlNotFoundStr = SOAPHelper.toString(this.errorToSOAP(this.errWsdlNotFound, null));
        LOGGER.debug("Initialize \"errInternalServerErrStr\" error message.");
        this.errInternalServerErrStr = SOAPHelper.toString(this.errorToSOAP(this.errInternalServerErr, null));
        LOGGER.debug("AbstractServlet initialized.");
    }

    /**
     * Handles and processes the given request and returns a SOAP message as a
     * response.
     *
     * @param request ServiceRequest to be processed
     * @return ServiceResponse that contains the SOAP response
     * @throws SOAPException if there's a SOAP error
     * @throws XRd4JException if there's a XRd4J error
     */
    protected abstract ServiceResponse handleRequest(ServiceRequest request) throws SOAPException, XRd4JException;

    /**
     * Must return the path of the WSDL file.
     *
     * @return path of the WSDL file
     */
    protected abstract String getWSDLPath();

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String errString = "Invalid SOAP message.";
        LOGGER.debug("New request received.");
        SOAPMessage soapRequest = null;
        SOAPMessage soapResponse = null;

        // Log HTTP headers if debug is enabled
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(AdapterUtils.getHeaderInfo(request));
            LOGGER.debug("Request content length {} bytes.", request.getContentLength());
        }

        // Get incoming SOAP message
        if (request.getContentType().toLowerCase().startsWith(Constants.TEXT_XML)) {
            // Regular SOAP message without attachments
            LOGGER.info("Request's content type is \"{}\".", Constants.TEXT_XML);
            soapRequest = SOAPHelper.toSOAP(request.getInputStream());
        } else if (request.getContentType().toLowerCase().startsWith(Constants.MULTIPART_RELATED)) {
            // SOAP message with attachments
            LOGGER.info("Request's content type is \"{}\".", Constants.MULTIPART_RELATED);
            MimeHeaders mh = AdapterUtils.getHeaders(request);
            soapRequest = SOAPHelper.toSOAP(request.getInputStream(), mh);
            LOGGER.trace(AdapterUtils.getAttachmentsInfo(soapRequest));
        } else {
            // Invalid content type -> message is not processed
            LOGGER.warn("Invalid content type : \"{}\".", request.getContentType());
            errString = "Invalid content type : \"" + request.getContentType() + "\".";
        }

        // Conversion has failed if soapRequest is null. Return SOAP Fault.
        if (soapRequest == null) {
            LOGGER.warn("Unable to deserialize the request to SOAP. SOAP Fault is returned.");
            LOGGER.trace("Incoming message : \"{}\"", request.getInputStream().toString());
            ErrorMessage errorMessage = new ErrorMessage(FAULT_CODE_CLIENT, errString, "", "");
            soapResponse = this.errorToSOAP(errorMessage, null);
        }

        // Deserialize incoming SOAP message to ServiceRequest object
        if (soapResponse == null) {
            // Convert SOAP request to servive request
            ServiceRequest serviceRequest = this.fromSOAPToServiceRequest(soapRequest);
            // If conversion fails, return SOAP fault
            if (serviceRequest == null) {
                ErrorMessage errorMessage = new ErrorMessage(FAULT_CODE_CLIENT, "Invalid X-Road SOAP message. Unable to parse the request.", "", "");
                soapResponse = this.errorToSOAP(errorMessage, null);
            }

            // Process ServiceRequest object
            if (soapResponse == null) {
                // Process request and generate SOAP response
                soapResponse = this.processServiceRequest(serviceRequest);
            }
        }
        // Write the SOAP response to output stream
        writeResponse(soapResponse, response);
    }

    /**
     * Writes the given SOAP response to output stream. Sets the necessary HTTP
     * headers according to the content of the response.
     *
     * @param soapResponse SOAP response
     * @param response servlet response
     */
    private void writeResponse(SOAPMessage soapResponse, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            LOGGER.debug("Send response.");
            // SOAPMessage to String
            String responseStr = SOAPHelper.toString(soapResponse);
            // Set response headers
            if (responseStr != null && soapResponse != null && soapResponse.getAttachments().hasNext()) {
                // Get MIME boundary from SOAP message
                String boundary = AdapterUtils.getMIMEBoundary(responseStr);
                response.setContentType(Constants.MULTIPART_RELATED + "; type=\"text/xml\"; boundary=\"" + boundary + "\"; charset=UTF-8");
            } else {
                response.setContentType(Constants.TEXT_XML + "; charset=UTF-8");
            }
            LOGGER.debug("Response content type : \"{}\".", response.getContentType());
            // Get writer
            out = response.getWriter();
            // Send response
            if (responseStr != null) {
                out.println(responseStr);
                LOGGER.trace("SOAP response : \"{}\"", responseStr);
            } else {
                out.println(this.errInternalServerErrStr);
                LOGGER.warn("Internal serveri error. Message processing failed.");
                LOGGER.trace("SOAP response : \"{}\"", this.errInternalServerErrStr);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            if (out != null) {
                out.println(this.errInternalServerErrStr);
            }
        } finally {
            if (out != null) {
                out.close();
            }
            LOGGER.debug("Request was succesfully processed.");
        }
    }

    /**
     * Converts the give SOAPMessage to ServiceRequest object.
     *
     * @param soapRequest SOAPMessage to be converted
     * @return ServiceRequest or null
     */
    private ServiceRequest fromSOAPToServiceRequest(SOAPMessage soapRequest) {
        LOGGER.trace("Incoming SOAP message : \"{}\"", SOAPHelper.toString(soapRequest));
        ServiceRequest serviceRequest = null;
        try {
            // Try to deserialize SOAP Message to ServiceRequest
            serviceRequest = deserializer.deserialize(soapRequest);
            LOGGER.debug("SOAP message header was succesfully deserialized to ServiceRequest.");
        } catch (Exception ex) {
            // If deserializing SOAP Message fails, return SOAP Fault
            LOGGER.error("Deserializing SOAP message header to ServiceRequest failed. Return SOAP Fault.");
            LOGGER.error(ex.getMessage(), ex);
        }
        return serviceRequest;
    }

    /**
     * Processes the given ServiceRequest object and generates SOAPMessage
     * object that's used as a response.
     *
     * @param serviceRequest ServiceRequest object to be processed
     * @return SOAPMessage representing the service response
     */
    private SOAPMessage processServiceRequest(ServiceRequest serviceRequest) {
        try {
            // Process application specific requests
            LOGGER.debug("Process ServiceRequest.");
            ServiceResponse serviceResponse = this.handleRequest(serviceRequest);
            if (serviceResponse == null) {
                LOGGER.warn("ServiceRequest was not processed. Unknown service code.");
                return this.errorToSOAP(this.errUnknownServiceCode, null);
            } else {
                SOAPMessage soapResponse = serviceResponse.getSoapMessage();
                LOGGER.debug("ServiceRequest was processed succesfully.");
                return soapResponse;
            }
        } catch (XRd4JException ex) {
            LOGGER.error(ex.getMessage(), ex);
            if (serviceRequest.hasError()) {
                return this.errorToSOAP(this.cloneErrorMessage(serviceRequest.getErrorMessage()), null);
            } else {
                return this.errorToSOAP(this.errInternalServerErr, null);
            }
        } catch (SOAPException | NullPointerException ex) {
            LOGGER.error(ex.getMessage(), ex);
            return this.errorToSOAP(this.errInternalServerErr, null);
        }
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType(Constants.TEXT_XML + ";charset=UTF-8");

        try (PrintWriter responseWriter = response.getWriter()) {
            if (request.getParameter("wsdl") != null) {
                LOGGER.debug("WSDL file request received.");
                String path = this.getWSDLPath();

                // Read WSDL file
                String wsdl = FileUtil.read(path);

                if (!wsdl.isEmpty()) {
                    responseWriter.println(wsdl);
                    LOGGER.trace("WSDL file was found and returned to the requester.");
                } else {
                    responseWriter.println(this.errWsdlNotFoundStr);
                    LOGGER.warn("WSDL file was not found. SOAP Fault was returned.");
                }

                LOGGER.debug("WSDL file request processed.");
            } else {
                LOGGER.warn("New GET request received. Not supported. SOAP Fault is returned.");
                responseWriter.println(errGetNotSupportedStr);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Converts the given ErrorMessage to standard SOAP Fault message or
     * non-technical SOAP error message based on the type of the given error.
     *
     * @param error ErrorMessage object that contains the error details
     * @param serviceRequest ServiceRequest object related to the error
     * @return SOAPMessage object containing ErrorMessage details
     */
    protected SOAPMessage errorToSOAP(ErrorMessage error, ServiceRequest serviceRequest) {
        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setErrorMessage(error);
        return this.serializer.serialize(serviceResponse, serviceRequest);
    }

    /**
     * Clones the given error message by using the constructor with four
     * arguments. In this way it's sure that the error's type is
     * "STANDARD_SOAP_ERROR_MESSAGE".
     *
     * @param errorMsg ErrorMessage object to be cloned
     * @return new ErrorMessage object
     */
    private ErrorMessage cloneErrorMessage(ErrorMessage errorMsg) {
        return new ErrorMessage(errorMsg.getFaultCode(), errorMsg.getFaultString(), errorMsg.getFaultActor(), errorMsg.getDetail());
    }

    /**
     * This is a dummy implementation of the AbstractServiceResponseSerializer
     * class. It's needed only for generating SOAP Fault messages.
     * SerializeResponse method gets never called.
     */
    private class DummyServiceResponseSerializer extends AbstractServiceResponseSerializer {

        @Override
        public void serializeResponse(ServiceResponse response, SOAPElement soapResponse, SOAPEnvelope envelope) throws SOAPException {
            /**
             * This is needed only for generating SOAP Fault messages.
             * SerializeResponse method gets never called.
             */
        }
    }
}
