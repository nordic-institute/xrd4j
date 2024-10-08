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
import org.niis.xrd4j.common.member.ObjectType;
import org.niis.xrd4j.common.message.ServiceRequest;
import org.niis.xrd4j.common.util.SOAPHelper;

import jakarta.xml.soap.Node;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test cases for CustomRequestDeserializer class.
 *
 * @author Petteri Kivimäki
 */
class CustomRequestDeserializerTest {

    /**
     * Request from subsystem to service with service version included.
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test1() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>getRandom</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service><xrd:userId>EE1234567890</xrd:userId><xrd:id>ID11234</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion></SOAP-ENV:Header><SOAP-ENV:Body><ns1:getRandom xmlns:ns1=\"http://producer.x-road.ee\"><data>1234567890</data></ns1:getRandom></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceRequestDeserializer deserializer = new ServiceRequestDeserializerImpl();
        ServiceRequest<String> request = deserializer.deserialize(msg);
        CustomRequestDeserializer customDeserializer = new CustomRequestDeserializerImpl();
        customDeserializer.deserialize(request, "http://producer.x-road.ee");

        assertEquals("1234567890", request.getRequestData());
        assertEquals("4.0", request.getProtocolVersion());
        assertEquals("http://producer.x-road.ee", request.getProducer().getNamespaceUrl());
        assertEquals("ns1", request.getProducer().getNamespacePrefix());
    }

    /**
     * Request from subsystem to service with service version included.
     * Test that request wrappers are correctly processed.
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test1WithWrappers() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>getRandom</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service><xrd:userId>EE1234567890</xrd:userId><xrd:id>ID11234</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion></SOAP-ENV:Header><SOAP-ENV:Body><ns1:getRandom xmlns:ns1=\"http://producer.x-road.ee\"><request><data>1234567890</data></request></ns1:getRandom></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceRequestDeserializer deserializer = new ServiceRequestDeserializerImpl();
        ServiceRequest<String> request = deserializer.deserialize(msg);
        request.setProcessingWrappers(true);
        CustomRequestDeserializer customDeserializer = new CustomRequestDeserializerImpl();
        customDeserializer.deserialize(request, "http://producer.x-road.ee");

        assertEquals("1234567890", request.getRequestData());
        assertEquals("4.0", request.getProtocolVersion());
        assertEquals("http://producer.x-road.ee", request.getProducer().getNamespaceUrl());
        assertEquals("ns1", request.getProducer().getNamespacePrefix());
    }

    /**
     * Request from subsystem to service with service version included.
     * Test that processing works without service wrappers.
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test1WithoutWrappers() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>getRandom</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service><xrd:userId>EE1234567890</xrd:userId><xrd:id>ID11234</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion></SOAP-ENV:Header><SOAP-ENV:Body><ns1:getRandom xmlns:ns1=\"http://producer.x-road.ee\"><data>1234567890</data></ns1:getRandom></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceRequestDeserializer deserializer = new ServiceRequestDeserializerImpl();
        ServiceRequest<String> request = deserializer.deserialize(msg);
        request.setProcessingWrappers(false);
        CustomRequestDeserializer customDeserializer = new CustomRequestDeserializerImpl();
        customDeserializer.deserialize(request, "http://producer.x-road.ee");

        assertEquals("1234567890", request.getRequestData());
        assertEquals("4.0", request.getProtocolVersion());
        assertEquals("http://producer.x-road.ee", request.getProducer().getNamespaceUrl());
        assertEquals("ns1", request.getProducer().getNamespacePrefix());
    }

    /**
     * Request from subsystem to service with service version included.
     * Request with namespace prefix.
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test2() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>getRandom</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service><xrd:userId>EE1234567890</xrd:userId><xrd:id>ID11234</xrd:id><xrd:protocolVersion>4.5</xrd:protocolVersion></SOAP-ENV:Header><SOAP-ENV:Body><ns1:getRandom xmlns:ns1=\"http://producer.x-road.ee\"><ns1:data>1234567890</ns1:data></ns1:getRandom></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceRequestDeserializer deserializer = new ServiceRequestDeserializerImpl();
        ServiceRequest<String> request = deserializer.deserialize(msg);
        CustomRequestDeserializer customDeserializer = new CustomRequestDeserializerImpl();
        customDeserializer.deserialize(request);

        assertEquals("1234567890", request.getRequestData());
        assertEquals("4.5", request.getProtocolVersion());
        assertEquals("http://producer.x-road.ee", request.getProducer().getNamespaceUrl());
        assertEquals("ns1", request.getProducer().getNamespacePrefix());
    }

    /**
     * Request from subsystem to service with service version included.
     * Multiple request child elements.
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test3() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>getRandom</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service><xrd:userId>EE1234567890</xrd:userId><xrd:id>ID11234</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion></SOAP-ENV:Header><SOAP-ENV:Body><ns1:getRandom xmlns:ns1=\"http://producer.x-road.ee\"><request><field1>Field 1</field1><field2>Field 2</field2><field3>Field 3</field3></request></ns1:getRandom></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceRequestDeserializer deserializer = new ServiceRequestDeserializerImpl();
        ServiceRequest<Map> request = deserializer.deserialize(msg);
        CustomRequestDeserializer customDeserializer = new CustomRequestDeserializerImpl1();
        customDeserializer.deserialize(request);

        assertEquals("Field 1", request.getRequestData().get("field1"));
        assertEquals("Field 2", request.getRequestData().get("field2"));
        assertEquals("Field 3", request.getRequestData().get("field3"));
        assertEquals("http://producer.x-road.ee", request.getProducer().getNamespaceUrl());
        assertEquals("ns1", request.getProducer().getNamespacePrefix());
        assertEquals("4.0", request.getProtocolVersion());
    }

    /**
     * Request from subsystem to service with service version included.
     * Multiple request child elements and namespace prefix.
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test4() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>getRandom</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service><xrd:userId>EE1234567890</xrd:userId><xrd:id>ID11234</xrd:id><xrd:protocolVersion>6.0</xrd:protocolVersion></SOAP-ENV:Header><SOAP-ENV:Body><ns1:getRandom xmlns:ns1=\"http://producer.x-road.ee\"><ns1:request><ns1:field1>Field 1</ns1:field1><ns1:field2>Field 2</ns1:field2><ns1:field3>Field 3</ns1:field3></ns1:request></ns1:getRandom></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceRequestDeserializer deserializer = new ServiceRequestDeserializerImpl();
        ServiceRequest<Map> request = deserializer.deserialize(msg);
        CustomRequestDeserializer customDeserializer = new CustomRequestDeserializerImpl1();
        customDeserializer.deserialize(request);

        assertEquals("Field 1", request.getRequestData().get("field1"));
        assertEquals("Field 2", request.getRequestData().get("field2"));
        assertEquals("Field 3", request.getRequestData().get("field3"));
        assertEquals("http://producer.x-road.ee", request.getProducer().getNamespaceUrl());
        assertEquals("ns1", request.getProducer().getNamespacePrefix());
        assertEquals("6.0", request.getProtocolVersion());
    }

    /**
     * Request from subsystem to service with service version included.
     * Line breaks inside the request element.
     * Deserialize with namespace specified.
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test5() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>getRandom</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service><xrd:userId>EE1234567890</xrd:userId><xrd:id>ID11234</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion></SOAP-ENV:Header><SOAP-ENV:Body><ns1:getRandom xmlns:ns1=\"http://producer.x-road.ee\">\n<data>1234567890</data></ns1:getRandom></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceRequestDeserializer deserializer = new ServiceRequestDeserializerImpl();
        ServiceRequest<String> request = deserializer.deserialize(msg);
        CustomRequestDeserializer customDeserializer = new CustomRequestDeserializerImpl();
        customDeserializer.deserialize(request, "http://producer.x-road.ee");

        assertEquals("1234567890", request.getRequestData());
        assertEquals("http://producer.x-road.ee", request.getProducer().getNamespaceUrl());
        assertEquals("ns1", request.getProducer().getNamespacePrefix());
        assertEquals("4.0", request.getProtocolVersion());
    }

    /**
     * Request from subsystem to service with service version included.
     * Deserialize with wrong namespace specified.
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void testException1() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>getRandom</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service><xrd:userId>EE1234567890</xrd:userId><xrd:id>ID11234</xrd:id><xrd:protocolVersion>6.5</xrd:protocolVersion></SOAP-ENV:Header><SOAP-ENV:Body><ns1:getRandom xmlns:ns1=\"http://producer.x-road.ee\"><request><data>1234567890</data></request></ns1:getRandom></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceRequestDeserializer deserializer = new ServiceRequestDeserializerImpl();
        ServiceRequest<String> request = deserializer.deserialize(msg);
        CustomRequestDeserializer customDeserializer = new CustomRequestDeserializerImpl();
        try {
            customDeserializer.deserialize(request, "http://test.com");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
        assertEquals(null, request.getRequestData());
        assertEquals(null, request.getProducer().getNamespaceUrl());
        assertEquals(null, request.getProducer().getNamespacePrefix());

        assertEquals("FI", request.getConsumer().getXRoadInstance());
        assertEquals("GOV", request.getConsumer().getMemberClass());
        assertEquals("MEMBER1", request.getConsumer().getMemberCode());
        assertEquals("subsystem", request.getConsumer().getSubsystemCode());
        assertEquals(ObjectType.SUBSYSTEM, request.getConsumer().getObjectType());

        assertEquals("FI", request.getProducer().getXRoadInstance());
        assertEquals("COM", request.getProducer().getMemberClass());
        assertEquals("MEMBER2", request.getProducer().getMemberCode());
        assertEquals("subsystem", request.getProducer().getSubsystemCode());
        assertEquals("getRandom", request.getProducer().getServiceCode());
        assertEquals("v1", request.getProducer().getServiceVersion());
        assertEquals("ID11234", request.getId());
        assertEquals("EE1234567890", request.getUserId());
        assertEquals("6.5", request.getProtocolVersion());
        assertEquals(ObjectType.SERVICE, request.getProducer().getObjectType());
        assertEquals(true, request.getSoapMessage() != null);
    }

    /**
     * Different service code in header and body.
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void testException2() throws SOAPException, XRd4JException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>getRandom</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service><xrd:userId>EE1234567890</xrd:userId><xrd:id>ID11234</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion></SOAP-ENV:Header><SOAP-ENV:Body><ns1:getRandom2 xmlns:ns1=\"http://test.com\"><request><data>1234567890</data></request></ns1:getRandom2></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceRequestDeserializer deserializer = new ServiceRequestDeserializerImpl();
        ServiceRequest<String> request = deserializer.deserialize(msg);
        CustomRequestDeserializer customDeserializer = new CustomRequestDeserializerImpl();
        try {
            customDeserializer.deserialize(request, "http://test.com");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
        assertEquals(null, request.getRequestData());
        assertEquals(null, request.getProducer().getNamespaceUrl());
        assertEquals(null, request.getProducer().getNamespacePrefix());

        assertEquals("FI", request.getConsumer().getXRoadInstance());
        assertEquals("GOV", request.getConsumer().getMemberClass());
        assertEquals("MEMBER1", request.getConsumer().getMemberCode());
        assertEquals("subsystem", request.getConsumer().getSubsystemCode());
        assertEquals(ObjectType.SUBSYSTEM, request.getConsumer().getObjectType());

        assertEquals("FI", request.getProducer().getXRoadInstance());
        assertEquals("COM", request.getProducer().getMemberClass());
        assertEquals("MEMBER2", request.getProducer().getMemberCode());
        assertEquals("subsystem", request.getProducer().getSubsystemCode());
        assertEquals("getRandom", request.getProducer().getServiceCode());
        assertEquals("v1", request.getProducer().getServiceVersion());
        assertEquals("ID11234", request.getId());
        assertEquals("EE1234567890", request.getUserId());
        assertEquals("4.0", request.getProtocolVersion());
        assertEquals(ObjectType.SERVICE, request.getProducer().getObjectType());
        assertEquals(true, request.getSoapMessage() != null);
    }

    private final class CustomRequestDeserializerImpl extends AbstractCustomRequestDeserializer<String> {

        protected String deserializeRequest(Node requestNode, SOAPMessage message) throws SOAPException {
            for (int i = 0; i < requestNode.getChildNodes().getLength(); i++) {
                if (requestNode.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE
                        && requestNode.getChildNodes().item(i).getLocalName().equals("data")) {
                    return requestNode.getChildNodes().item(i).getTextContent();
                }
            }
            return null;
        }
    }

    private final class CustomRequestDeserializerImpl1 extends AbstractCustomRequestDeserializer<Map> {

        protected Map deserializeRequest(Node requestNode, SOAPMessage message) throws SOAPException {
            if (requestNode == null) {
                return null;
            }
            return SOAPHelper.nodesToMap(requestNode.getChildNodes());
        }
    }
}
