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
package org.niis.xrd4j.exampleadapter;

import org.niis.xrd4j.common.exception.XRd4JException;
import org.niis.xrd4j.common.message.ErrorMessage;
import org.niis.xrd4j.common.message.ServiceRequest;
import org.niis.xrd4j.common.message.ServiceResponse;
import org.niis.xrd4j.common.util.PropertiesUtil;
import org.niis.xrd4j.exampleadapter.data.Person;
import org.niis.xrd4j.exampleadapter.data.PersonContact;
import org.niis.xrd4j.server.AbstractAdapterServlet;
import org.niis.xrd4j.server.deserializer.AbstractCustomRequestDeserializer;
import org.niis.xrd4j.server.deserializer.CustomRequestDeserializer;
import org.niis.xrd4j.server.serializer.AbstractServiceResponseSerializer;
import org.niis.xrd4j.server.serializer.ServiceResponseSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements four simple X-Road 6 and X-Road 7 compatible services:
 * "getRandom", "helloService", "listPeople" and "personDetails". Service descriptions
 * are defined in "example-adapter.wsdl" file that's located in resources/ folder.
 * The name of the WSDL file and the namespace is configured in resources/xrd-servlet.properties file.
 *
 * @author Petteri Kivimäki
 * @author Raido Kaju
 */
public class ExampleAdapter extends AbstractAdapterServlet {

    private Properties props;
    private static final Logger logger = LoggerFactory.getLogger(ExampleAdapter.class);
    private String namespaceSerialize;
    private String namespaceDeserialize;
    private String prefix;
    private static final List<Person> peopleExampleData = Person.getExamplePeople();

    @Override
    public void init() {
        logger.debug("Starting to initialize Enpoint.");
        this.props = PropertiesUtil.getInstance().load("/xrd-servlet.properties");
        this.namespaceSerialize = this.props.getProperty("namespace.serialize");
        this.namespaceDeserialize = this.props.getProperty("namespace.deserialize");
        this.prefix = this.props.getProperty("namespace.prefix.serialize");
        logger.debug("Namespace for incoming ServiceRequests : \"" + this.namespaceDeserialize + "\".");
        logger.debug("Namespace for outgoing ServiceResponses : \"" + this.namespaceSerialize + "\".");
        logger.debug("Namespace prefix for outgoing ServiceResponses : \"" + this.prefix + "\".");
        logger.debug("Endpoint initialized.");
    }

    /**
     * Must return the path of the WSDL file.
     *
     * @return absolute path of the WSDL file
     */
    @Override
    protected String getWSDLPath() {
        String path = this.props.getProperty("wsdl.path");
        logger.debug("WSDL path : \"" + path + "\".");
        return path;
    }

    @Override
    protected ServiceResponse handleRequest(ServiceRequest request) throws SOAPException, XRd4JException {
        ServiceResponseSerializer serializer;
        // Process services by service code
        if ("getRandom".equals(request.getProducer().getServiceCode())) {
            // Process "getRandom" service
            logger.info("Process \"getRandom\" service.");
            // Create a new response serializer that serializes the response
            // to SOAP. Request contains no data, so in this case we don't
            // need a request deserializer
            serializer = new ServiceResponseSerializerImpl();
            // Create a new ServiceResponse object
            ServiceResponse<String, String> response = new ServiceResponse<>(request.getConsumer(), request.getProducer(), request.getId());
            // Set namespace of the SOAP response
            response.getProducer().setNamespaceUrl(this.namespaceSerialize);
            response.getProducer().setNamespacePrefix(this.prefix);
            // Set response data - a random number between 0 and 100
            response.setResponseData(Integer.toString((int) new Random().nextInt(101)));
            // Serialize the response to SOAP
            serializer.serialize(response, request);
            // Return the response - AbstractAdapterServlet takes care of
            // the rest
            return response;
        } else if ("helloService".equals(request.getProducer().getServiceCode())) {
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
            ServiceResponse<String, String> response = new ServiceResponse<>(request.getConsumer(), request.getProducer(), request.getId());
            // Set namespace of the SOAP response
            response.getProducer().setNamespaceUrl(this.namespaceSerialize);
            response.getProducer().setNamespacePrefix(this.prefix);
            logger.debug("Do message prosessing...");
            if (request.getRequestData() != null) {
                // If request data is not null, add response data to the
                // response object
                response.setResponseData("Hello " + request.getRequestData() + "! Greetings from adapter server!");
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
        } else if ("listPeople".equals(request.getProducer().getServiceCode())) {
            // Process "listPeople" service
            logger.info("Process \"listPeople\" service.");
            // Create a new response serializer that serializes the response
            // to SOAP
            serializer = new ListPeopleResponseSerializer();
            // Create a new ServiceResponse object
            ServiceResponse<String, List<Person>> response = new ServiceResponse<>(request.getConsumer(), request.getProducer(), request.getId());
            // Set namespace of the SOAP response
            response.getProducer().setNamespaceUrl(this.namespaceSerialize);
            response.getProducer().setNamespacePrefix(this.prefix);
            logger.debug("Do message prosessing...");
            // Set our response data to contain our example people so that the serializer can serialize them
            response.setResponseData(peopleExampleData);
            logger.debug("Message prosessing done!");
            // Serialize the response to SOAP
            serializer.serialize(response, request);
            // Return the response - AbstractAdapterServlet takes care of
            // the rest
            return response;
        } else if ("personDetails".equals(request.getProducer().getServiceCode())) {
            // Process "personDetails" service
            logger.info("Process \"personDetails\" service.");
            // Create a new response serializer that serializes the response
            // to SOAP
            serializer = new PersonDetailsResponseSerializer();
            // Create a custom request deserializer that parses the request
            // data from the SOAP request
            CustomRequestDeserializer customDeserializer = new PersonDetailsRequestDeserializer();
            // Parse the request data from the request
            customDeserializer.deserialize(request, this.namespaceDeserialize);
            // Create a new ServiceResponse object
            ServiceResponse<String, Person> response = new ServiceResponse<>(request.getConsumer(), request.getProducer(), request.getId());
            // Set namespace of the SOAP response
            response.getProducer().setNamespaceUrl(this.namespaceSerialize);
            response.getProducer().setNamespacePrefix(this.prefix);
            logger.debug("Do message prosessing...");
            if (request.getRequestData() != null) {
                // If request data is not null, try to find the person and add it to the
                // response object. If the person was not found, nothing will be added
                peopleExampleData.stream().filter(p -> request.getRequestData().equals(p.getSsn()))
                    .findFirst()
                    .ifPresent(response::setResponseData);

            } else {
                // No request data is found - an error message is returned
                logger.warn("No \"ssn\" parameter found. Return a non-techinal error message.");
                ErrorMessage error = new ErrorMessage("422", "422 Unprocessable Entity. Missing \"ssn\" element.");
                response.setErrorMessage(error);
            }
            logger.debug("Message prosessing done!");
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
            ServiceResponse<String, Map<String,Integer>> response = new ServiceResponse<>(request.getConsumer(), request.getProducer(), request.getId());
            // Set namespace of the SOAP response
            response.getProducer().setNamespaceUrl(this.namespaceSerialize);
            response.getProducer().setNamespacePrefix(this.prefix);
            logger.debug("Do message prosessing...");

            Map<String, Integer> attachments =  new HashMap<>();
            List<Integer> requestedSizes = (List<Integer>) request.getRequestData();
            for (int i = 0; i < requestedSizes.size(); i++) {
                attachments.put(String.format("attachment_%d_%d", i, requestedSizes.get(i)), requestedSizes.get(i));
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
            ServiceResponse<String, Map<String, Integer>> response = new ServiceResponse<>(request.getConsumer(), request.getProducer(), request.getId());
            // Set namespace of the SOAP response
            response.getProducer().setNamespaceUrl(this.namespaceSerialize);
            response.getProducer().setNamespacePrefix(this.prefix);
            logger.debug("Do message prosessing...");

            Map<String, Integer> attachments =  new HashMap<>();
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
        // No service matching the service code in the request was found -
        // and error is returned
        serializer = new ServiceResponseSerializerImpl();
        ServiceResponse<String, String> response = new ServiceResponse();
        ErrorMessage error = new ErrorMessage("SOAP-ENV:Client", "Unknown service code.", null, null);
        response.setErrorMessage(error);
        serializer.serialize(response, request);
        return response;
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
     * This class is responsible for serializing response data of getRandom
     * service responses.
     */
    private class ServiceResponseSerializerImpl extends AbstractServiceResponseSerializer {

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
            // Add "data" element
            SOAPElement data = soapResponse.addChildElement(envelope.createName("data"));
            // Put response data inside the "data" element
            data.addTextNode((String) response.getResponseData());
        }
    }

    /**
     * This class is responsible for serializing response data of helloService
     * service responses.
     */
    private class HelloServiceResponseSerializer extends AbstractServiceResponseSerializer {

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
    private class CustomRequestDeserializerImpl extends AbstractCustomRequestDeserializer<String> {

        /**
         * Deserializes the "request" element.
         *
         * @param requestNode request element
         * @return content of the request element
         */
        @Override
        protected String deserializeRequest(Node requestNode, SOAPMessage message) throws SOAPException {
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

    /**
     * This class is responsible for serializing the response data of the listPeople
     * service.
     */
    private class ListPeopleResponseSerializer extends AbstractServiceResponseSerializer {

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
            SOAPElement data = soapResponse.addChildElement(envelope.createName("people"));
            // Get our list of people to serialize into the response and cast it to the "Person" class
            final List<Person> people = (List<Person>) response.getResponseData();
            // Loop over every person object and serialize it into our response list
            for (Person person : people) {
                serializePerson(envelope, data.addChildElement(envelope.createName("person")), person);
            }
        }

        /**
         * Serializes the {@link Person} object into "shortPerson" as defined in our WSDL
         * @param envelope SOAPMessage's SOAPEnvelope object
         * @param parentElement the parent element to add our data under
         * @param person the {@link Person} object to serialize
         */
        private void serializePerson(final SOAPEnvelope envelope, final SOAPElement parentElement, final Person person) throws SOAPException {
            // Create and add the "SSN" element
            SOAPElement ssn = parentElement.addChildElement(envelope.createName("ssn"));
            ssn.addTextNode(person.getSsn());
            // Create and add the "firstName" element
            SOAPElement firstName = parentElement.addChildElement(envelope.createName("firstName"));
            firstName.addTextNode(person.getFirstName());
            // Create and add the "lastName" element
            SOAPElement lastName = parentElement.addChildElement(envelope.createName("lastName"));
            lastName.addTextNode(person.getLastName());
        }
    }

    /**
     * This class is responsible for serializing the response data of the personDetails
     * service.
     */
    private class PersonDetailsResponseSerializer extends AbstractServiceResponseSerializer {

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
            SOAPElement data = soapResponse.addChildElement(envelope.createName("person"));
            // Get our person data to serialize into the response and cast it to the "Person" class
            final Person person = (Person) response.getResponseData();
            // Create and add the "SSN" element
            SOAPElement ssn = data.addChildElement(envelope.createName("ssn"));
            ssn.addTextNode(person.getSsn());
            // Create and add the "firstName" element
            SOAPElement firstName = data.addChildElement(envelope.createName("firstName"));
            firstName.addTextNode(person.getFirstName());
            // Create and add the "lastName" element
            SOAPElement lastName = data.addChildElement(envelope.createName("lastName"));
            lastName.addTextNode(person.getLastName());
            // Create and add the "dateOfBirth" element
            SOAPElement dateOfBirth = data.addChildElement(envelope.createName("dateOfBirth"));
            dateOfBirth.addTextNode(person.getDateOfBirth().toString());
            // Create and add the "contactAddress" element
            SOAPElement contactAddress = data.addChildElement(envelope.createName("contactAddress"));
            contactAddress.addTextNode(person.getContactAddress());
            // Add the "contacts" container element
            SOAPElement contacts = data.addChildElement(envelope.createName("contacts"));
            // Loop over every person object and serialize it into our response list
            for (PersonContact contact : person.getContacts()) {
                serializeContact(envelope, contacts.addChildElement(envelope.createName("contact")), contact);
            }
        }

        /**
         * Serializes the {@link PersonContact} object into "contact" as defined in our WSDL
         * @param envelope SOAPMessage's SOAPEnvelope object
         * @param parentElement the parent element to add our data under
         * @param contact the {@link PersonContact} object to serialize
         */
        private void serializeContact(final SOAPEnvelope envelope, final SOAPElement parentElement, final PersonContact contact) throws SOAPException {
            // Create and add the "type" element
            SOAPElement type = parentElement.addChildElement(envelope.createName("type"));
            type.addTextNode(contact.getType().toString());
            // Create and add the "name" element
            SOAPElement name = parentElement.addChildElement(envelope.createName("name"));
            name.addTextNode(contact.getName());
            // Create and add the "value" element
            SOAPElement value = parentElement.addChildElement(envelope.createName("value"));
            value.addTextNode(contact.getValue());
        }
    }

    /**
     * This class is responsible for deserializing request data of personDetails
     * service request. The type declaration "<String>" defines the type of the
     * request data, which in this case is String.
     */
    private class PersonDetailsRequestDeserializer extends AbstractCustomRequestDeserializer<String> {

        /**
         * Deserializes the "request" element.
         *
         * @param requestNode request element
         * @return content of the request element
         */
        @Override
        protected String deserializeRequest(Node requestNode, SOAPMessage message) throws SOAPException {
            if (requestNode == null) {
                logger.warn("\"requestNode\" is null. Null is returned.");
                return null;
            }
            for (int i = 0; i < requestNode.getChildNodes().getLength(); i++) {
                // Request data is inside of "ssn" element
                if (requestNode.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE
                        && "ssn".equals(requestNode.getChildNodes().item(i).getLocalName())) {
                    logger.debug("Found \"ssn\" element.");
                    // "ssn" element was found - return the text content
                    return requestNode.getChildNodes().item(i).getTextContent();
                        }
            }
            logger.warn("No \"ssn\" element found. Null is returned.");
            return null;
        }
    }

    private class GetAttachmentsRequestDeserializer extends AbstractCustomRequestDeserializer<List<Integer>> {

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

    private class AttachmentsResponseSerializer extends AbstractServiceResponseSerializer {

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

}
