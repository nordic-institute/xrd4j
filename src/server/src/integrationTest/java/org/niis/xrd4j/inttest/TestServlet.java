package org.niis.xrd4j.inttest;

import org.niis.xrd4j.common.exception.XRd4JException;
import org.niis.xrd4j.common.message.ErrorMessage;
import org.niis.xrd4j.common.message.ServiceRequest;
import org.niis.xrd4j.common.message.ServiceResponse;
import org.niis.xrd4j.server.AbstractAdapterServlet;
import org.niis.xrd4j.server.deserializer.AbstractCustomRequestDeserializer;
import org.niis.xrd4j.server.deserializer.CustomRequestDeserializer;
import org.niis.xrd4j.server.serializer.AbstractServiceResponseSerializer;
import org.niis.xrd4j.server.serializer.ServiceResponseSerializer;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.xml.soap.Node;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * Test servlet implementation copied and modified from example-adapter.
 */
class TestServlet extends AbstractAdapterServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(TestServlet.class);
    private final String namespaceSerialize = "http://test.x-road.global/consumer";
    private final String namespaceDeserialize = "http://test.x-road.global/producer";
    private final String prefix = "xrdtest";


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.debug("GET request received.");
        super.doGet(request, response);
    }

    @Override
    protected ServiceResponse<?, ?> handleRequest(ServiceRequest request) throws SOAPException, XRd4JException {
        ServiceResponseSerializer serializer;
        if ("helloService".equals(request.getProducer().getServiceCode())) {
            // Process "helloService" service
            logger.info("Process \"helloService\" service.");
            // Create a new response serializer that serializes the response
            // to SOAP
            serializer = new HelloServiceResponseSerializer();
            // Create a custom request deserializer that parses the request
            // data from the SOAP request
            CustomRequestDeserializer customDeserializer = new CustomRequestDeserializerImpl();
            // Parse the request data from the request
            customDeserializer.deserialize(request, this.namespaceDeserialize);
            // Create a new ServiceResponse object
            ServiceResponse<String, String> response = createResponse(request);
            // Set namespace of the SOAP response
            response.getProducer().setNamespaceUrl(this.namespaceSerialize);
            response.getProducer().setNamespacePrefix(this.prefix);
            logger.debug("Do message prosessing...");
            if (request.getRequestData() != null) {
                // If request data is not null, add response data to the
                // response object
                response.setResponseData("Hello " + request.getRequestData() + "!");
            } else {
                // No request data is found - an error message is returned
                logger.warn("No \"name\" parameter found. Return a non-techinal error message.");
                ErrorMessage error = new ErrorMessage("422", "422 Unprocessable Entity. Missing \"name\" element.");
                response.setErrorMessage(error);
            }
            logger.debug("Message prosessing done!");
            // Serialize the response to SOAP
            serializer.serialize(response, request);
            // Return the response - AbstractAdapterServlet takes care of
            // the rest
            return response;
        }

        return null;
    }


    @Override
    protected String getWSDLPath() {
        return "test-servlet.wsdl";
    }

    private <T1, T2> ServiceResponse<T1, T2> createResponse(ServiceRequest<?> request) throws XRd4JException {
        return new ServiceResponse<>(request.getConsumer(), request.getProducer(), request.getId());
    }

    /**
     * This class is responsible for serializing response data of helloService
     * service responses.
     */
    private static class HelloServiceResponseSerializer extends AbstractServiceResponseSerializer {

        /**
         * Serializes the response data.
         *
         * @param response ServiceResponse holding the application specific
         * response object
         * @param soapResponse SOAPMessage's response object where the response
         * element is added
         * @param envelope SOAPMessage's SOAPEnvelope object
         */
        @Override
        public void serializeResponse(ServiceResponse response, SOAPElement soapResponse, SOAPEnvelope envelope) throws SOAPException {
            // Add "message" element
            SOAPElement data = soapResponse.addChildElement(envelope.createName("message"));
            // Put response data inside the "message" element
            data.addTextNode((String) response.getResponseData());
        }
    }

    /**
     * This class is responsible for deserializing request data of helloService
     * service requests. The type declaration "<String>" defines the type of the
     * request data, which in this case is String.
     */
    private static class CustomRequestDeserializerImpl extends AbstractCustomRequestDeserializer<String> {

        /**
         * Deserializes the "request" element.
         *
         * @param requestNode request element
         * @return content of the request element
         */
        @Override
        protected String deserializeRequest(Node requestNode, SOAPMessage message) {
            if (requestNode == null) {
                logger.warn("\"requestNode\" is null. Null is returned.");
                return null;
            }
            for (int i = 0; i < requestNode.getChildNodes().getLength(); i++) {
                // Request data is inside of "name" element
                if (requestNode.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE
                        && "name".equals(requestNode.getChildNodes().item(i).getLocalName())) {
                    logger.debug("Found \"name\" element.");
                    // "name" element was found - return the text content
                    return requestNode.getChildNodes().item(i).getTextContent();
                }
            }
            logger.warn("No \"name\" element found. Null is returned.");
            return null;
        }
    }
}
