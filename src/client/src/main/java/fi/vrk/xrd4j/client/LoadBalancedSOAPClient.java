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
package fi.vrk.xrd4j.client;

import fi.vrk.xrd4j.client.deserializer.ServiceResponseDeserializer;
import fi.vrk.xrd4j.client.serializer.ServiceRequestSerializer;
import fi.vrk.xrd4j.common.member.ConsumerMember;
import fi.vrk.xrd4j.common.member.ProducerMember;
import fi.vrk.xrd4j.common.message.ServiceRequest;
import fi.vrk.xrd4j.common.message.ServiceResponse;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import java.util.List;

/**
 * This class defines an interface for load balanced SOAP client that can be
 * used for sending SOAPMessage and ServiceRequest objects to multipleSOAP
 * endpoints.
 *
 * @author Petteri Kivimäki
 */
public interface LoadBalancedSOAPClient {

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
    SOAPMessage send(SOAPMessage request) throws SOAPException;

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
    ServiceResponse send(ServiceRequest request, ServiceRequestSerializer serializer,
                         ServiceResponseDeserializer deserializer) throws SOAPException;

    /**
     * Calls listClients meta service and retrieves list of all the potential
     * service providers (i.e., members and subsystems) of an X-Road instance.
     * Returns a list of list of ConsumerMembers that represent X-Road clients.
     *
     * @return list of ConsumerMembers
     */
    List<ConsumerMember> listClients();

    /**
     * Calls listCentralServices meta service and retrieves list of all central
     * services defined in an X-Road instance. Returns a list of ProducerMembers
     * that represent X-Road central services.
     *
     * @return list of ProducerMembers
     */
    List<ProducerMember> listCentralServices();

    /**
     * Calls listMethods meta service that lists all the services offered by a
     * service provider. Returns a list of ProducerMember objects wrapped in
     * ServiceResponse object's responseData variable.
     *
     * @param request the ServiceRequest object to be sent
     * @return ServiceResponse that holds a list of ProducerMember objects
     * @throws SOAPException if there's a SOAP error
     */
    ServiceResponse listMethods(ServiceRequest request) throws SOAPException;

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
    ServiceResponse allowedMethods(ServiceRequest request) throws SOAPException;

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
    ServiceResponse getSecurityServerMetrics(ServiceRequest request, String url) throws SOAPException;

}
