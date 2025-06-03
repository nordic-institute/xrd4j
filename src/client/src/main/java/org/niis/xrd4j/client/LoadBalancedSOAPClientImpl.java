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
package org.niis.xrd4j.client;

import org.niis.xrd4j.client.deserializer.ServiceResponseDeserializer;
import org.niis.xrd4j.client.serializer.ServiceRequestSerializer;
import org.niis.xrd4j.common.member.ConsumerMember;
import org.niis.xrd4j.common.member.ProducerMember;
import org.niis.xrd4j.common.message.ServiceRequest;
import org.niis.xrd4j.common.message.ServiceResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;

import java.util.List;

/**
 * This class represents a round-robin load balanced SOAP client that can be
 * used for sending SOAPMessage and ServiceRequest objects to multiple SOAP
 * endpoints. This class holds a list of server URLs and going down the list of
 * security servers in the group, the round-robin load balancer forwards a
 * client request to each server in turn. However, this class does not provide
 * high availability features - if a request fails, it is not sent again to
 * another endpoint. In addition, repeatedly failing endpoints are not removed
 * from the list and they keep on receiving requests.
 *
 * @author Petteri Kivimäki
 */
public class LoadBalancedSOAPClientImpl implements LoadBalancedSOAPClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadBalancedSOAPClientImpl.class);
    private final SOAPClient soapClient;
    private final List<String> endpointUrls;
    private int nextTarget;

    /**
     * Constructs and initializes a new LoadBalancedSOAPClientImpl object.
     *
     * @param endpointUrls list of security server URLs where the requests are
     * sent
     * @throws SOAPException if there's an error
     */
    public LoadBalancedSOAPClientImpl(List<String> endpointUrls) throws SOAPException {
        this.endpointUrls = endpointUrls;
        this.soapClient = new SOAPClientImpl();
        this.nextTarget = 0;
        LOGGER.debug("Create new LoadBalancedSOAPClientImpl with {} endpoint URLs", endpointUrls.size());
        for (String url : this.endpointUrls) {
            LOGGER.debug("Found URL: \"{}\"", url);
        }
    }

    /**
     * Sends the given message to one of the defined endpoints and blocks until
     * it has returned the response. Null is returned if sending the message
     * fails.
     *
     * @param request the SOAPMessage object to be sent
     * @return the SOAPMessage object that is the response to the request
     * message that was sent.
     * @throws SOAPException if there's a SOAP error
     */
    @Override
    public SOAPMessage send(final SOAPMessage request) throws SOAPException {
        return this.soapClient.send(request, this.getTargetUrl());
    }

    /**
     * Sends the given message to one of the defined endpoints and blocks until
     * it has returned the response. Null is returned if sending the message
     * fails. Serialization and deserialization from/to SOAPMessage is done
     * inside the method.
     *
     * @param request the ServiceRequest object to be sent
     * @param serializer the ServiceRequestSerializer object that serializes the
     * request to SOAPMessage
     * @param deserializer the ServiceResponseDeserializer object that
     * deserializes SOAPMessage response to ServiceResponse
     * @return the ServiceResponse object that is the response to the message
     * that was sent.
     * @throws SOAPException if there's a SOAP error
     */
    @Override
    public ServiceResponse send(final ServiceRequest request, final ServiceRequestSerializer serializer,
                                final ServiceResponseDeserializer deserializer) throws SOAPException {
        return this.soapClient.send(request, this.getTargetUrl(), serializer, deserializer);
    }

    /**
     * Calls listClients meta service and retrieves list of all the potential
     * service providers (i.e., members and subsystems) of an X-Road instance.
     * Returns a list of list of ConsumerMembers that represent X-Road clients.
     *
     * @return list of ConsumerMembers
     */
    @Override
    public List<ConsumerMember> listClients() {
        return this.soapClient.listClients(this.getTargetUrl());
    }

    /**
     * Calls listCentralServices meta service and retrieves list of all central
     * services defined in an X-Road instance. Returns a list of ProducerMembers
     * that represent X-Road central services.
     *
     * @return list of ProducerMembers
     */
    @Override
    public List<ProducerMember> listCentralServices() {
        return this.soapClient.listCentralServices(this.getTargetUrl());
    }

    /**
     * Calls listMethods meta service that lists all the services offered by a
     * service provider. Returns a list of ProducerMember objects wrapped in
     * ServiceResponse object's responseData variable.
     *
     * @param request the ServiceRequest object to be sent
     * @return ServiceResponse that holds a list of ProducerMember objects
     * @throws SOAPException if there's a SOAP error
     */
    @Override
    public ServiceResponse listMethods(final ServiceRequest request) throws SOAPException {
        return this.soapClient.listMethods(request, this.getTargetUrl());
    }

    /**
     * Calls allowedMethods meta service that lists all the services by a
     * service provider that the caller has permission to invoke. Returns a list
     * of ProducerMember objects wrapped in ServiceResponse object's
     * responseData variable.
     *
     * @param request the ServiceRequest object to be sent
     * @return ServiceResponse that holds a list of ProducerMember objects
     * @throws SOAPException if there's a SOAP error
     */
    @Override
    public ServiceResponse allowedMethods(final ServiceRequest request) throws SOAPException {
        return this.soapClient.allowedMethods(request, this.getTargetUrl());
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
        return this.soapClient.getSecurityServerMetrics(request, this.getTargetUrl());
    }

    /**
     * Returns the next target URL in turn and updates the pointer holding the
     * value of the next target.
     *
     * @return target URL
     */
    protected String getTargetUrl() {
        // Get the next target URL
        String target = this.endpointUrls.get(this.nextTarget);
        LOGGER.trace("Current nextTarget: \"{}\", targetUrl: \"{}\"", this.nextTarget, target);
        // Update next pointer
        this.nextTarget = this.nextTarget == this.endpointUrls.size() - 1 ? 0 : this.nextTarget + 1;
        LOGGER.trace("New nextTarget: \"{}\"", this.nextTarget);
        return target;
    }
}
