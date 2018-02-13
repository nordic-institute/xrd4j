/**
 * The MIT License
 * Copyright © 2017 Population Register Centre (VRK)
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
package fi.vrk.xrd4j.client;

import fi.vrk.xrd4j.client.deserializer.GetSecurityServerMetricsResponseDeserializer;
import fi.vrk.xrd4j.client.deserializer.ListCentralServicesResponseDeserializer;
import fi.vrk.xrd4j.client.deserializer.ListClientsResponseDeserializer;
import fi.vrk.xrd4j.client.deserializer.ListServicesResponseDeserializer;
import fi.vrk.xrd4j.client.deserializer.ServiceResponseDeserializer;
import fi.vrk.xrd4j.client.serializer.DefaultServiceRequestSerializer;
import fi.vrk.xrd4j.client.serializer.ServiceRequestSerializer;
import fi.vrk.xrd4j.common.exception.XRd4JRuntimeException;
import fi.vrk.xrd4j.common.member.ConsumerMember;
import fi.vrk.xrd4j.common.member.ProducerMember;
import fi.vrk.xrd4j.common.message.ServiceRequest;
import fi.vrk.xrd4j.common.message.ServiceResponse;
import fi.vrk.xrd4j.common.util.Constants;
import fi.vrk.xrd4j.common.util.SOAPHelper;
import fi.vrk.xrd4j.rest.ClientResponse;
import fi.vrk.xrd4j.rest.client.RESTClient;
import fi.vrk.xrd4j.rest.client.RESTClientFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * This class represents a SOAP client that can be used for sending SOAPMessage
 * and ServiceRequest objects to SOAP endpoints.
 *
 * @author Petteri Kivimäki
 */
public class SOAPClientImpl implements SOAPClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SOAPClientImpl.class);
    private static final String SEND_SOAP_TO = "Send SOAP message to \"{}\".";
    private static final String CALL_METASERVICE = "Call \"{}\" meta service.";
    private final SOAPConnectionFactory connectionFactory;

    /**
     * Constructs and initializes a new SOAPClientImpl.
     *
     * @throws SOAPException if there's a SOAP error
     */
    public SOAPClientImpl() throws SOAPException {
        this.connectionFactory = SOAPConnectionFactory.newInstance();
    }

    /**
     * Sends the given message to the specified endpoint and blocks until it has
     * returned the response. Null is returned if the given url is malformed or
     * if sending the message fails.
     *
     * @param request the SOAPMessage object to be sent
     * @param url URL that identifies where the message should be sent
     * @return the SOAPMessage object that is the response to the request
     * message that was sent.
     * @throws SOAPException if there's a SOAP error
     */
    @Override
    public SOAPMessage send(final SOAPMessage request, final String url) throws SOAPException {
        URL client;
        try {
            client = new URL(url);
        } catch (MalformedURLException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new XRd4JRuntimeException(ex.getMessage());
        }
        SOAPConnection connection = connectionFactory.createConnection();
        LOGGER.debug(SEND_SOAP_TO, url);
        LOGGER.trace("Outgoing SOAP request : \"{}\".", SOAPHelper.toString(request));
        SOAPMessage response = connection.call(request, client);
        LOGGER.debug("SOAP response received.");
        LOGGER.trace("Incoming SOAP response : \"{}\".", SOAPHelper.toString(response));
        connection.close();
        return response;
    }

    /**
     * Sends the given message to the specified endpoint and blocks until it has
     * returned the response. Null is returned if the given url is malformed or
     * if sending the message fails. Serialization and deserialization from/to
     * SOAPMessage is done inside the method.
     *
     * @param request the ServiceRequest object to be sent
     * @param url URL that identifies where the message should be sent
     * @param serializer the ServiceRequestSerializer object that serializes the
     * request to SOAPMessage
     * @param deserializer the ServiceResponseDeserializer object that
     * deserializes SOAPMessage response to ServiceResponse
     * @return the ServiceResponse object that is the response to the message
     * that was sent.
     * @throws SOAPException if there's a SOAP error
     */
    @Override
    public ServiceResponse send(final ServiceRequest request, final String url,
                                final ServiceRequestSerializer serializer,
                                final ServiceResponseDeserializer deserializer) throws SOAPException {
        SOAPMessage soapRequest = serializer.serialize(request);
        LOGGER.info("Send ServiceRequest to \"{}\". Request id : \"{}\"", url, request.getId());
        LOGGER.debug("Consumer : {}", request.getConsumer().toString());
        LOGGER.debug("Producer : {}", request.getProducer().toString());
        SOAPMessage soapResponse = this.send(soapRequest, url);
        String producerNamespaceURI = request.getProducer().getNamespaceUrl() == null
                || request.getProducer().getNamespaceUrl().isEmpty() ? "*" : request.getProducer().getNamespaceUrl();
        ServiceResponse response = deserializer.deserialize(soapResponse, producerNamespaceURI, request.isProcessingWrappers());
        LOGGER.info("ServiceResponse received. Request id : \"{}\"", request.getId());
        return response;
    }

    /**
     * Calls listClients meta service and retrieves list of all the potential
     * service providers (i.e., members and subsystems) of an X-Road instance.
     * Returns a list of list of ConsumerMembers that represent X-Road clients.
     *
     * @param url URL of X-Road security server
     * @return list of ConsumerMembers
     */
    @Override
    public List<ConsumerMember> listClients(String url) {
        LOGGER.info(CALL_METASERVICE, Constants.META_SERVICE_LIST_CLIENTS);
        if (!url.endsWith("/")) {
            url += "/";
        }
        LOGGER.debug(SEND_SOAP_TO, url);
        RESTClient client = RESTClientFactory.createRESTClient("get");
        ClientResponse response = client.send(url + Constants.META_SERVICE_LIST_CLIENTS, null, null, null);
        List<ConsumerMember> list = new ListClientsResponseDeserializer().deserializeConsumerList(response.getData());
        LOGGER.debug("Received \"{}\" clients from the security server.", list.size());
        return list;
    }

    /**
     * Calls listCentralServices meta service and retrieves list of all central
     * services defined in an X-Road instance. Returns a list of ProducerMembers
     * that represent X-Road central services.
     *
     * @param url URL of X-Road security server
     * @return list of ProducerMembers
     */
    @Override
    public List<ProducerMember> listCentralServices(String url) {
        LOGGER.info(CALL_METASERVICE, Constants.META_SERVICE_LIST_CENTRAL_SERVICES);
        if (!url.endsWith("/")) {
            url += "/";
        }
        LOGGER.debug(SEND_SOAP_TO, url);
        RESTClient client = RESTClientFactory.createRESTClient("get");
        ClientResponse response = client.send(url + Constants.META_SERVICE_LIST_CENTRAL_SERVICES, null, null, null);
        List<ProducerMember> list = new ListCentralServicesResponseDeserializer().deserializeProducerList(response.getData());
        LOGGER.debug("Received \"{}\" clients from the security server.", list.size());
        return list;
    }

    /**
     * Calls listMethods meta service that lists all the services offered by a
     * service provider. Returns a list of ProducerMember objects wrapped in
     * ServiceResponse object's responseData variable.
     *
     * @param request the ServiceRequest object to be sent
     * @param url URL that identifies where the message should be sent
     * @return ServiceResponse that holds a list of ProducerMember objects
     * @throws SOAPException if there's a SOAP error
     */
    @Override
    public ServiceResponse listMethods(final ServiceRequest request, final String url) throws SOAPException {
        LOGGER.info(CALL_METASERVICE, Constants.META_SERVICE_LIST_METHODS);
        return this.listServices(request, url, Constants.META_SERVICE_LIST_METHODS);
    }

    /**
     * Calls allowedMethods meta service that lists all the services by a
     * service provider that the caller has permission to invoke. Returns a list
     * of ProducerMember objects wrapped in ServiceResponse object's
     * responseData variable.
     *
     * @param request the ServiceRequest object to be sent
     * @param url URL that identifies where the message should be sent
     * @return ServiceResponse that holds a list of ProducerMember objects
     * @throws SOAPException if there's a SOAP error
     */
    @Override
    public ServiceResponse allowedMethods(final ServiceRequest request, final String url) throws SOAPException {
        LOGGER.info(CALL_METASERVICE, Constants.META_SERVICE_ALLOWED_METHODS);
        return this.listServices(request, url, Constants.META_SERVICE_ALLOWED_METHODS);
    }

    /**
     * This is a helper method for meta service calls that don't have a request
     * body. The method sets the service code, name space and name space prefix,
     * and removes the service code.
     *
     * @param request the ServiceRequest object to be sent
     * @param url URL that identifies where the message should be sent
     * @param serviceCode service code of the meta service to be called
     * @return ServiceResponse that holds the response of the meta service
     * @throws SOAPException if there's a SOAP error
     * @throws MalformedURLException MalformedURLException if no protocol is
     * specified, or an unknown protocol is found, or url is null
     */
    private ServiceResponse listServices(final ServiceRequest request, final String url, final String serviceCode) throws SOAPException {
        // Set correct values for meta service call
        request.getProducer().setServiceCode(serviceCode);
        request.getProducer().setServiceVersion(null);
        request.getProducer().setNamespacePrefix(Constants.NS_XRD_PREFIX);
        request.getProducer().setNamespaceUrl(Constants.NS_XRD_URL);
        // Request serializer
        ServiceRequestSerializer serializer = new DefaultServiceRequestSerializer();
        // Response deserializer
        ServiceResponseDeserializer deserializer = new ListServicesResponseDeserializer();
        // Return response
        return this.send(request, url, serializer, deserializer);
    }

    /**
     * Calls getSecurityServerMetrics monitoring service that returns a data set
     * collected by environmental monitoring sensors.
     *
     * @param request the ServiceRequest object to be sent
     * @param url URL that identifies where the message should be sent
     * @return ServiceResponse that holds a NodeList containing the response
     * data
     * @throws SOAPException if there's a SOAP error
     */
    @Override
    public ServiceResponse getSecurityServerMetrics(final ServiceRequest request, final String url) throws SOAPException {
        // Set correct values for meta service call
        request.getProducer().setSubsystemCode(null);
        request.getProducer().setServiceCode(Constants.ENV_MONITORING_GET_SECURITY_SERVER_METRICS);
        request.getProducer().setServiceVersion(null);
        request.getProducer().setNamespacePrefix(Constants.NS_ENV_MONITORING_PREFIX);
        request.getProducer().setNamespaceUrl(Constants.NS_ENV_MONITORING_URL);
        // Request serializer
        ServiceRequestSerializer serializer = new DefaultServiceRequestSerializer();
        // Response deserializer
        ServiceResponseDeserializer deserializer = new GetSecurityServerMetricsResponseDeserializer();
        // Return response
        return this.send(request, url, serializer, deserializer);
    }
}
