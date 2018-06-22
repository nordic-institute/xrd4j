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
package fi.vrk.xrd4j.client.serializer;

import fi.vrk.xrd4j.common.exception.XRd4JException;
import fi.vrk.xrd4j.common.member.ConsumerMember;
import fi.vrk.xrd4j.common.member.ProducerMember;
import fi.vrk.xrd4j.common.member.SecurityServer;
import fi.vrk.xrd4j.common.message.ServiceRequest;
import fi.vrk.xrd4j.common.util.Constants;
import fi.vrk.xrd4j.common.util.MessageHelper;
import fi.vrk.xrd4j.common.util.SOAPHelper;

import junit.framework.TestCase;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

/**
 * Test cases for DefaultServiceRequestSerializer class.
 *
 * @author Petteri Kivimäki
 */
public class DefaultServiceRequestSerializerTest extends TestCase {
    
    /**
     * allowedMethos : member - member level service call.
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    public void test1() throws XRd4JException, SOAPException {
        String id = MessageHelper.generateId();
        String correctRequest = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"MEMBER\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:serviceCode>allowedMethods</id:serviceCode></xrd:service><xrd:userId>user</xrd:userId><xrd:id>" + id + "</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion></SOAP-ENV:Header><SOAP-ENV:Body><xrd:allowedMethods/></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        ConsumerMember consumer = new ConsumerMember("FI", "GOV", "MEMBER1");
        ProducerMember producer = new ProducerMember("FI", "COM", "MEMBER2", "subsystem", Constants.META_SERVICE_ALLOWED_METHODS);
        producer.setSubsystemCode(null);
        producer.setNamespacePrefix(Constants.NS_XRD_PREFIX);
        producer.setNamespaceUrl(Constants.NS_XRD_URL);
        ServiceRequest<String> request = new ServiceRequest<String>(consumer, producer, id);
        request.setUserId("user");

        ServiceRequestSerializer serializer = new DefaultServiceRequestSerializer();
        SOAPMessage msg = serializer.serialize(request);

        assertEquals(correctRequest, SOAPHelper.toString(msg));
    }

    /**
     * allowedMethos : member - subsystem level service call.
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    public void test2() throws XRd4JException, SOAPException {
        String id = MessageHelper.generateId();
        String correctRequest = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"MEMBER\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>service</id:subsystemCode><id:serviceCode>allowedMethods</id:serviceCode></xrd:service><xrd:userId>user</xrd:userId><xrd:id>" + id + "</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion></SOAP-ENV:Header><SOAP-ENV:Body><xrd:allowedMethods/></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        ConsumerMember consumer = new ConsumerMember("FI", "GOV", "MEMBER1");
        ProducerMember producer = new ProducerMember("FI", "COM", "MEMBER2", "service", Constants.META_SERVICE_ALLOWED_METHODS);
        producer.setNamespacePrefix(Constants.NS_XRD_PREFIX);
        producer.setNamespaceUrl(Constants.NS_XRD_URL);
        ServiceRequest<String> request = new ServiceRequest<String>(consumer, producer, id);
        request.setUserId("user");

        ServiceRequestSerializer serializer = new DefaultServiceRequestSerializer();
        SOAPMessage msg = serializer.serialize(request);

        assertEquals(correctRequest, SOAPHelper.toString(msg));
    }

    /**
     * listMethos : subsystem - member level service call.
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    public void test3() throws XRd4JException, SOAPException {
        String id = MessageHelper.generateId();
        String correctRequest = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>client</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:serviceCode>listMethods</id:serviceCode></xrd:service><xrd:userId>user</xrd:userId><xrd:id>" + id + "</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion></SOAP-ENV:Header><SOAP-ENV:Body><xrd:listMethods/></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        ConsumerMember consumer = new ConsumerMember("FI", "GOV", "MEMBER1", "client");
        ProducerMember producer = new ProducerMember("FI", "COM", "MEMBER2", "subsystem", Constants.META_SERVICE_LIST_METHODS, "v1");
        producer.setSubsystemCode(null);
        producer.setServiceVersion(null);
        producer.setNamespacePrefix(Constants.NS_XRD_PREFIX);
        producer.setNamespaceUrl(Constants.NS_XRD_URL);
        ServiceRequest<String> request = new ServiceRequest<String>(consumer, producer, id);
        request.setUserId("user");

        ServiceRequestSerializer serializer = new DefaultServiceRequestSerializer();
        SOAPMessage msg = serializer.serialize(request);

        assertEquals(correctRequest, SOAPHelper.toString(msg));
    }

    /**
     * listMethos : subsystem - subsystem level service call.
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    public void test4() throws XRd4JException, SOAPException {
        String id = MessageHelper.generateId();
        String correctRequest = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>client</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>service</id:subsystemCode><id:serviceCode>listMethods</id:serviceCode></xrd:service><xrd:userId>user</xrd:userId><xrd:id>" + id + "</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion></SOAP-ENV:Header><SOAP-ENV:Body><xrd:listMethods/></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        ConsumerMember consumer = new ConsumerMember("FI", "GOV", "MEMBER1", "client");
        ProducerMember producer = new ProducerMember("FI", "COM", "MEMBER2", "service", Constants.META_SERVICE_LIST_METHODS);
        producer.setNamespacePrefix(Constants.NS_XRD_PREFIX);
        producer.setNamespaceUrl(Constants.NS_XRD_URL);
        ServiceRequest<String> request = new ServiceRequest<String>(consumer, producer, id);
        request.setUserId("user");

        ServiceRequestSerializer serializer = new DefaultServiceRequestSerializer();
        SOAPMessage msg = serializer.serialize(request);

        assertEquals(correctRequest, SOAPHelper.toString(msg));
    }

    /**
     * getSecurityServerMetrics.
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    public void test5() throws XRd4JException, SOAPException {
        String id = MessageHelper.generateId();
        String correctRequest = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>client</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>service</id:subsystemCode><id:serviceCode>getSecurityServerMetrics</id:serviceCode></xrd:service><xrd:securityServer id:objectType=\"SERVER\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:serverCode>server1</id:serverCode></xrd:securityServer><xrd:userId>user</xrd:userId><xrd:id>" + id + "</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion></SOAP-ENV:Header><SOAP-ENV:Body><m:getSecurityServerMetrics xmlns:m=\"http://x-road.eu/xsd/monitoring\"/></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        ConsumerMember consumer = new ConsumerMember("FI", "GOV", "MEMBER1", "client");
        ProducerMember producer = new ProducerMember("FI", "COM", "MEMBER2", "service", Constants.ENV_MONITORING_GET_SECURITY_SERVER_METRICS);
        SecurityServer securityServer = new SecurityServer("FI", "COM", "MEMBER2", "server1");
        producer.setNamespacePrefix(Constants.NS_ENV_MONITORING_PREFIX);
        producer.setNamespaceUrl(Constants.NS_ENV_MONITORING_URL);
        ServiceRequest<String> request = new ServiceRequest<>(consumer, producer, id);
        request.setSecurityServer(securityServer);
        request.setUserId("user");

        ServiceRequestSerializer serializer = new DefaultServiceRequestSerializer();
        SOAPMessage msg = serializer.serialize(request);

        assertEquals(correctRequest, SOAPHelper.toString(msg));
    }
}
