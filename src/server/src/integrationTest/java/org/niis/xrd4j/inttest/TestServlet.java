package org.niis.xrd4j.inttest;

import org.niis.xrd4j.common.exception.XRd4JException;
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
import jakarta.xml.soap.AttachmentPart;
import jakarta.xml.soap.Node;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
            response.setResponseData("Hello " + request.getRequestData() + "!");
            // Serialize the response to SOAP
            serializer.serialize(response, request);
            // Return the response - AbstractAdapterServlet takes care of
            // the rest
            return response;
        } else if ("getAttachments".equals(request.getProducer().getServiceCode())) {
            logger.info("Process \"getAttachments\" service.");
            // Create a new response serializer that serializes the response
            // to SOAP
            serializer = new AttachmentsResponseSerializer();
            // Create a custom request deserializer that parses the request
            // data from the SOAP request
            CustomRequestDeserializer customDeserializer = new GetAttachmentsRequestDeserializer();
            // Parse the request data from the request
            customDeserializer.deserialize(request, this.namespaceDeserialize);
            // Create a new ServiceResponse object
            ServiceResponse<String, Map<String,Integer>> response = createResponse(request);
            // Set namespace of the SOAP response
            response.getProducer().setNamespaceUrl(this.namespaceSerialize);
            response.getProducer().setNamespacePrefix(this.prefix);
            logger.debug("Do message prosessing...");

            Map<String, Integer> attachments =  new LinkedHashMap<>();
            List<Integer> requestedSizes = (List<Integer>) request.getRequestData();
            for (int i = 0; i < requestedSizes.size(); i++) {
                attachments.put(String.format("attachment_%d", i), requestedSizes.get(i));
            }
            response.setResponseData(attachments);

            logger.debug("Message prosessing done!");
            // Serialize the response to SOAP
            serializer.serialize(response, request);
            // add the attachment parts
            for (Map.Entry<String, Integer> file: attachments.entrySet()) {
                AttachmentPart attachmentPart = response.getSoapMessage().createAttachmentPart(generateCharacters(file.getValue()),
                        "application/octet-stream");
                attachmentPart.setContentId(file.getKey());
                response.getSoapMessage().addAttachmentPart(attachmentPart);
            }
            return response;
        } else if ("storeAttachments".equals(request.getProducer().getServiceCode())) {
            logger.info("Process \"storeAttachments\" service.");
            // Create a new response serializer that serializes the response
            // to SOAP
            serializer = new AttachmentsResponseSerializer();
            // Create a new ServiceResponse object
            ServiceResponse<String, Map<String, Integer>> response = createResponse(request);
            // Set namespace of the SOAP response
            response.getProducer().setNamespaceUrl(this.namespaceSerialize);
            response.getProducer().setNamespacePrefix(this.prefix);
            logger.debug("Do message prosessing...");

            Map<String, Integer> attachments = new LinkedHashMap<>();
            Iterator it = request.getSoapMessage().getAttachments();
            if (it != null) {
                while (it.hasNext()) {
                    AttachmentPart attachment = (AttachmentPart) it.next();
                    attachments.put(attachment.getContentId(), attachment.getSize());
                }
            }
            response.setResponseData(attachments);

            logger.debug("Message prosessing done!");
            // Serialize the response to SOAP
            serializer.serialize(response, request);
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

    private String generateCharacters(Integer size) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            // Generate a random ASCII code of character between 'a' and 'z'
            char randomChar = (char) ('a' + random.nextInt('z' - 'a' + 1));
            sb.append(randomChar);
        }
        return sb.toString();
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

    private static class AttachmentsResponseSerializer extends AbstractServiceResponseSerializer {

        @Override
        protected void serializeResponse(ServiceResponse response, SOAPElement soapResponse, SOAPEnvelope envelope) throws SOAPException {
            Map<String, Integer> attachments = (Map<String, Integer>) response.getResponseData();
            for (Map.Entry<String, Integer> file : attachments.entrySet()) {
                SOAPElement fileElement = soapResponse.addChildElement(envelope.createName("attachment"));

                SOAPElement name = fileElement.addChildElement(envelope.createName("name"));
                name.addTextNode(file.getKey());

                SOAPElement size = fileElement.addChildElement(envelope.createName("size"));
                size.addTextNode(Long.toString(file.getValue()));
            }
        }
    }

    private static class GetAttachmentsRequestDeserializer extends AbstractCustomRequestDeserializer<List<Integer>> {

        @Override
        protected List<Integer> deserializeRequest(Node requestNode, SOAPMessage message) throws SOAPException {
            if (requestNode == null) {
                logger.warn("\"requestNode\" is null. Null is returned.");
                return null;
            }
            List<Integer> sizes = new ArrayList<>();
            for (int i = 0; i < requestNode.getChildNodes().getLength(); i++) {
                org.w3c.dom.Node node = requestNode.getChildNodes().item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE && "size".equals(node.getLocalName())) {
                    sizes.add(Integer.parseInt(node.getTextContent()));
                }
            }
            return sizes;
        }
    }
}
