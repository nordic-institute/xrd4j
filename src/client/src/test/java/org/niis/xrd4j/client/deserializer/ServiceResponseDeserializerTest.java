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
package org.niis.xrd4j.client.deserializer;

import org.niis.xrd4j.common.exception.XRd4JException;
import org.niis.xrd4j.common.member.ObjectType;
import org.niis.xrd4j.common.message.ErrorMessageType;
import org.niis.xrd4j.common.message.ServiceResponse;
import org.niis.xrd4j.common.util.SOAPHelper;

import jakarta.xml.soap.Node;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test cases for ServiceRequestDeserializerImpl class.
 *
 * @author Petteri Kivimäki
 */
class ServiceResponseDeserializerTest {

    /**
     * Response to subsystem -> service level service call
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test1() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>getRandom</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service><xrd:userId>EE1234567890</xrd:userId><xrd:id>ID-1234567890</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion><xrd:requestHash algorithmId=\"SHA-512\">ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==</xrd:requestHash></SOAP-ENV:Header><SOAP-ENV:Body><ns1:getRandomResponse xmlns:ns1=\"http://producer.x-road.ee\"><data>9876543210</data></ns1:getRandomResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";

        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceResponseDeserializer deserializer = new TestResponseDeserializer();
        ServiceResponse<String, String> response = deserializer.deserialize(msg);

        assertEquals("FI", response.getConsumer().getXRoadInstance());
        assertEquals("GOV", response.getConsumer().getMemberClass());
        assertEquals("MEMBER1", response.getConsumer().getMemberCode());
        assertEquals("subsystem", response.getConsumer().getSubsystemCode());
        assertEquals(ObjectType.SUBSYSTEM, response.getConsumer().getObjectType());

        assertEquals("FI", response.getProducer().getXRoadInstance());
        assertEquals("COM", response.getProducer().getMemberClass());
        assertEquals("MEMBER2", response.getProducer().getMemberCode());
        assertEquals("subsystem", response.getProducer().getSubsystemCode());
        assertEquals("getRandom", response.getProducer().getServiceCode());
        assertEquals("v1", response.getProducer().getServiceVersion());
        assertEquals("ID-1234567890", response.getId());
        assertEquals("EE1234567890", response.getUserId());
        assertEquals("4.0", response.getProtocolVersion());
        assertEquals(ObjectType.SERVICE, response.getProducer().getObjectType());
        assertEquals("9876543210", response.getResponseData());

        assertEquals("SHA-512", response.getRequestHashAlgorithm());
        assertEquals("ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==", response.getRequestHash());
        assertEquals(true, response.getSoapMessage() != null);
    }

    /**
     * Response to subsystem -> service level service call.
     * Test processing of response with wrappers.
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test1WithWrappers() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>getRandom</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service><xrd:userId>EE1234567890</xrd:userId><xrd:id>ID-1234567890</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion><xrd:requestHash algorithmId=\"SHA-512\">ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==</xrd:requestHash></SOAP-ENV:Header><SOAP-ENV:Body><ns1:getRandomResponse xmlns:ns1=\"http://producer.x-road.ee\"><request><data>1234567890</data></request><response><data>9876543210</data></response></ns1:getRandomResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceResponseDeserializer deserializer = new TestResponseDeserializer();
        ServiceResponse<String, String> response = deserializer.deserialize(msg, "*", true);

        assertEquals("FI", response.getConsumer().getXRoadInstance());
        assertEquals("GOV", response.getConsumer().getMemberClass());
        assertEquals("MEMBER1", response.getConsumer().getMemberCode());
        assertEquals("subsystem", response.getConsumer().getSubsystemCode());
        assertEquals(ObjectType.SUBSYSTEM, response.getConsumer().getObjectType());

        assertEquals("FI", response.getProducer().getXRoadInstance());
        assertEquals("COM", response.getProducer().getMemberClass());
        assertEquals("MEMBER2", response.getProducer().getMemberCode());
        assertEquals("subsystem", response.getProducer().getSubsystemCode());
        assertEquals("getRandom", response.getProducer().getServiceCode());
        assertEquals("v1", response.getProducer().getServiceVersion());
        assertEquals("ID-1234567890", response.getId());
        assertEquals("EE1234567890", response.getUserId());
        assertEquals("4.0", response.getProtocolVersion());
        assertEquals(ObjectType.SERVICE, response.getProducer().getObjectType());
        assertEquals("1234567890", response.getRequestData());
        assertEquals("9876543210", response.getResponseData());

        assertEquals("SHA-512", response.getRequestHashAlgorithm());
        assertEquals("ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==", response.getRequestHash());
        assertEquals(true, response.getSoapMessage() != null);
    }

    /**
     * Response to subsystem -> service level service call.
     * Test processing of response without wrappers.
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test1WithoutWrappers() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>getRandom</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service><xrd:userId>EE1234567890</xrd:userId><xrd:id>ID-1234567890</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion><xrd:requestHash algorithmId=\"SHA-512\">ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==</xrd:requestHash></SOAP-ENV:Header><SOAP-ENV:Body><ns1:getRandomResponse xmlns:ns1=\"http://producer.x-road.ee\"><data>9876543210</data></ns1:getRandomResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceResponseDeserializer deserializer = new TestResponseDeserializer();
        ServiceResponse<String, String> response = deserializer.deserialize(msg, "*", false);

        assertEquals("FI", response.getConsumer().getXRoadInstance());
        assertEquals("GOV", response.getConsumer().getMemberClass());
        assertEquals("MEMBER1", response.getConsumer().getMemberCode());
        assertEquals("subsystem", response.getConsumer().getSubsystemCode());
        assertEquals(ObjectType.SUBSYSTEM, response.getConsumer().getObjectType());

        assertEquals("FI", response.getProducer().getXRoadInstance());
        assertEquals("COM", response.getProducer().getMemberClass());
        assertEquals("MEMBER2", response.getProducer().getMemberCode());
        assertEquals("subsystem", response.getProducer().getSubsystemCode());
        assertEquals("getRandom", response.getProducer().getServiceCode());
        assertEquals("v1", response.getProducer().getServiceVersion());
        assertEquals("ID-1234567890", response.getId());
        assertEquals("EE1234567890", response.getUserId());
        assertEquals("4.0", response.getProtocolVersion());
        assertEquals(ObjectType.SERVICE, response.getProducer().getObjectType());
        assertEquals(null, response.getRequestData());
        assertEquals("9876543210", response.getResponseData());

        assertEquals("SHA-512", response.getRequestHashAlgorithm());
        assertEquals("ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==", response.getRequestHash());
        assertEquals(true, response.getSoapMessage() != null);
    }

    /**
     * Response to subsystem -> service level service call. Request and response
     * with namespace prefix.
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test2() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>getRandom</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service><xrd:userId>EE1234567890</xrd:userId><xrd:id>ID-1234567890</xrd:id><xrd:protocolVersion>4.5</xrd:protocolVersion><xrd:requestHash algorithmId=\"SHA-512\">ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==</xrd:requestHash></SOAP-ENV:Header><SOAP-ENV:Body><ns1:getRandomResponse xmlns:ns1=\"http://producer.x-road.ee\"><ns1:data>9876543210</ns1:data></ns1:getRandomResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceResponseDeserializer deserializer = new TestResponseDeserializer();
        ServiceResponse<String, String> response = deserializer.deserialize(msg);

        assertEquals("FI", response.getConsumer().getXRoadInstance());
        assertEquals("GOV", response.getConsumer().getMemberClass());
        assertEquals("MEMBER1", response.getConsumer().getMemberCode());
        assertEquals("subsystem", response.getConsumer().getSubsystemCode());
        assertEquals(ObjectType.SUBSYSTEM, response.getConsumer().getObjectType());

        assertEquals("FI", response.getProducer().getXRoadInstance());
        assertEquals("COM", response.getProducer().getMemberClass());
        assertEquals("MEMBER2", response.getProducer().getMemberCode());
        assertEquals("subsystem", response.getProducer().getSubsystemCode());
        assertEquals("getRandom", response.getProducer().getServiceCode());
        assertEquals("v1", response.getProducer().getServiceVersion());
        assertEquals("ID-1234567890", response.getId());
        assertEquals("EE1234567890", response.getUserId());
        assertEquals("4.5", response.getProtocolVersion());
        assertEquals(ObjectType.SERVICE, response.getProducer().getObjectType());
        assertEquals("9876543210", response.getResponseData());

        assertEquals("SHA-512", response.getRequestHashAlgorithm());
        assertEquals("ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==", response.getRequestHash());
        assertEquals(true, response.getSoapMessage() != null);
        assertEquals(true, response.getSecurityServer() == null);
    }

    /**
     * Response to subsystem -> central service level service call
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test3() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode></xrd:client><xrd:service id:objectType=\"CENTRALSERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:serviceCode>getRandom</id:serviceCode></xrd:service><xrd:userId>EE1234567890</xrd:userId><xrd:id>ID-1234567890</xrd:id><xrd:protocolVersion>6.0</xrd:protocolVersion><xrd:requestHash algorithmId=\"SHA-512\">ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==</xrd:requestHash></SOAP-ENV:Header><SOAP-ENV:Body><ns1:getRandomResponse xmlns:ns1=\"http://producer.x-road.ee\"><data>9876543210</data></ns1:getRandomResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceResponseDeserializer deserializer = new TestResponseDeserializer();
        ServiceResponse<String, String> response = deserializer.deserialize(msg);

        assertEquals("FI", response.getConsumer().getXRoadInstance());
        assertEquals("GOV", response.getConsumer().getMemberClass());
        assertEquals("MEMBER1", response.getConsumer().getMemberCode());
        assertEquals("subsystem", response.getConsumer().getSubsystemCode());
        assertEquals(ObjectType.SUBSYSTEM, response.getConsumer().getObjectType());

        assertEquals("FI", response.getProducer().getXRoadInstance());
        assertEquals(null, response.getProducer().getMemberClass());
        assertEquals(null, response.getProducer().getMemberCode());
        assertEquals(null, response.getProducer().getSubsystemCode());
        assertEquals("getRandom", response.getProducer().getServiceCode());
        assertEquals(null, response.getProducer().getServiceVersion());
        assertEquals("ID-1234567890", response.getId());
        assertEquals("EE1234567890", response.getUserId());
        assertEquals("6.0", response.getProtocolVersion());
        assertEquals(ObjectType.CENTRALSERVICE, response.getProducer().getObjectType());
        assertEquals("9876543210", response.getResponseData());

        assertEquals("SHA-512", response.getRequestHashAlgorithm());
        assertEquals("ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==", response.getRequestHash());
        assertEquals(true, response.getSoapMessage() != null);
        assertEquals(true, response.getSecurityServer() == null);
    }

    /**
     * Response to member -> central service level service call
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test4() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"MEMBER\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode></xrd:client><xrd:service id:objectType=\"CENTRALSERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:serviceCode>getRandom</id:serviceCode></xrd:service><xrd:userId>EE1234567890</xrd:userId><xrd:id>ID-1234567890</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion><xrd:requestHash algorithmId=\"SHA-512\">ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==</xrd:requestHash></SOAP-ENV:Header><SOAP-ENV:Body><ns1:getRandomResponse xmlns:ns1=\"http://producer.x-road.ee\"><data>9876543210</data></ns1:getRandomResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceResponseDeserializer deserializer = new TestResponseDeserializer();
        ServiceResponse<String, String> response = deserializer.deserialize(msg);

        assertEquals("FI", response.getConsumer().getXRoadInstance());
        assertEquals("GOV", response.getConsumer().getMemberClass());
        assertEquals("MEMBER1", response.getConsumer().getMemberCode());
        assertEquals(null, response.getConsumer().getSubsystemCode());
        assertEquals(ObjectType.MEMBER, response.getConsumer().getObjectType());

        assertEquals("FI", response.getProducer().getXRoadInstance());
        assertEquals(null, response.getProducer().getMemberClass());
        assertEquals(null, response.getProducer().getMemberCode());
        assertEquals(null, response.getProducer().getSubsystemCode());
        assertEquals("getRandom", response.getProducer().getServiceCode());
        assertEquals(null, response.getProducer().getServiceVersion());
        assertEquals("ID-1234567890", response.getId());
        assertEquals("EE1234567890", response.getUserId());
        assertEquals("4.0", response.getProtocolVersion());
        assertEquals(ObjectType.CENTRALSERVICE, response.getProducer().getObjectType());
        assertEquals("9876543210", response.getResponseData());

        assertEquals("SHA-512", response.getRequestHashAlgorithm());
        assertEquals("ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==", response.getRequestHash());
        assertEquals(true, response.getSoapMessage() != null);
        assertEquals(true, response.getSecurityServer() == null);
    }

    /**
     * Response to member -> service level service call. No service version.
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test5() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"MEMBER\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>getRandom</id:serviceCode></xrd:service><xrd:securityServer><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:serverCode>server1</id:serverCode></xrd:securityServer><xrd:userId>EE1234567890</xrd:userId><xrd:id>ID-1234567890</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion><xrd:requestHash algorithmId=\"SHA-512\">ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==</xrd:requestHash></SOAP-ENV:Header><SOAP-ENV:Body><ns1:getRandomResponse xmlns:ns1=\"http://producer.x-road.ee\"><data>9876543210</data></ns1:getRandomResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceResponseDeserializer deserializer = new TestResponseDeserializer();
        ServiceResponse<String, String> response = deserializer.deserialize(msg);

        assertEquals("FI", response.getConsumer().getXRoadInstance());
        assertEquals("GOV", response.getConsumer().getMemberClass());
        assertEquals("MEMBER1", response.getConsumer().getMemberCode());
        assertEquals(null, response.getConsumer().getSubsystemCode());
        assertEquals(ObjectType.MEMBER, response.getConsumer().getObjectType());

        assertEquals("FI", response.getProducer().getXRoadInstance());
        assertEquals("COM", response.getProducer().getMemberClass());
        assertEquals("MEMBER2", response.getProducer().getMemberCode());
        assertEquals("subsystem", response.getProducer().getSubsystemCode());
        assertEquals("FI", response.getSecurityServer().getXRoadInstance());
        assertEquals("GOV", response.getSecurityServer().getMemberClass());
        assertEquals("MEMBER1", response.getSecurityServer().getMemberCode());
        assertEquals("server1", response.getSecurityServer().getServerCode());
        assertEquals("getRandom", response.getProducer().getServiceCode());
        assertEquals(null, response.getProducer().getServiceVersion());
        assertEquals("ID-1234567890", response.getId());
        assertEquals("EE1234567890", response.getUserId());
        assertEquals("4.0", response.getProtocolVersion());
        assertEquals(ObjectType.SERVICE, response.getProducer().getObjectType());
        assertEquals("9876543210", response.getResponseData());

        assertEquals("SHA-512", response.getRequestHashAlgorithm());
        assertEquals("ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==", response.getRequestHash());
        assertEquals(true, response.getSoapMessage() != null);
    }

    /**
     * Response to subsystem -> service level service call. No service subsystem
     * and version.
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test6() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:serviceCode>getRandom</id:serviceCode></xrd:service><xrd:userId>EE1234567890</xrd:userId><xrd:id>ID-1234567890</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion><xrd:issue>Test issue</xrd:issue><xrd:requestHash algorithmId=\"SHA-512\">ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==</xrd:requestHash></SOAP-ENV:Header><SOAP-ENV:Body><ns1:getRandomResponse xmlns:ns1=\"http://producer.x-road.ee\"><data>9876543210</data></ns1:getRandomResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceResponseDeserializer deserializer = new TestResponseDeserializer();
        ServiceResponse<String, String> response = deserializer.deserialize(msg);

        assertEquals("FI", response.getConsumer().getXRoadInstance());
        assertEquals("GOV", response.getConsumer().getMemberClass());
        assertEquals("MEMBER1", response.getConsumer().getMemberCode());
        assertEquals("subsystem", response.getConsumer().getSubsystemCode());
        assertEquals(ObjectType.SUBSYSTEM, response.getConsumer().getObjectType());

        assertEquals("FI", response.getProducer().getXRoadInstance());
        assertEquals("COM", response.getProducer().getMemberClass());
        assertEquals("MEMBER2", response.getProducer().getMemberCode());
        assertEquals(null, response.getProducer().getSubsystemCode());
        assertEquals("getRandom", response.getProducer().getServiceCode());
        assertEquals(null, response.getProducer().getServiceVersion());
        assertEquals("ID-1234567890", response.getId());
        assertEquals("EE1234567890", response.getUserId());
        assertEquals("4.0", response.getProtocolVersion());
        assertEquals(ObjectType.SERVICE, response.getProducer().getObjectType());
        assertEquals("9876543210", response.getResponseData());
        assertEquals("Test issue", response.getIssue());

        assertEquals("SHA-512", response.getRequestHashAlgorithm());
        assertEquals("ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==", response.getRequestHash());
        assertEquals(true, response.getSoapMessage() != null);
        assertEquals(true, response.getSecurityServer() == null);
    }

    /**
     * Response to subsystem -> service level service call. No service
     * subsystem.
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test7() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:serviceCode>getRandom</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service><xrd:userId>EE1234567890</xrd:userId><xrd:id>ID-1234567890</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion><xrd:requestHash algorithmId=\"SHA-512\">ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==</xrd:requestHash></SOAP-ENV:Header><SOAP-ENV:Body><ns1:getRandomResponse xmlns:ns1=\"http://producer.x-road.ee\"><data>9876543210</data></ns1:getRandomResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceResponseDeserializer deserializer = new TestResponseDeserializer();
        ServiceResponse<String, String> response = deserializer.deserialize(msg);

        assertEquals("FI", response.getConsumer().getXRoadInstance());
        assertEquals("GOV", response.getConsumer().getMemberClass());
        assertEquals("MEMBER1", response.getConsumer().getMemberCode());
        assertEquals("subsystem", response.getConsumer().getSubsystemCode());
        assertEquals(ObjectType.SUBSYSTEM, response.getConsumer().getObjectType());

        assertEquals("FI", response.getProducer().getXRoadInstance());
        assertEquals("COM", response.getProducer().getMemberClass());
        assertEquals("MEMBER2", response.getProducer().getMemberCode());
        assertEquals(null, response.getProducer().getSubsystemCode());
        assertEquals("getRandom", response.getProducer().getServiceCode());
        assertEquals("v1", response.getProducer().getServiceVersion());
        assertEquals("ID-1234567890", response.getId());
        assertEquals("EE1234567890", response.getUserId());
        assertEquals("4.0", response.getProtocolVersion());
        assertEquals(ObjectType.SERVICE, response.getProducer().getObjectType());
        assertEquals("9876543210", response.getResponseData());

        assertEquals("SHA-512", response.getRequestHashAlgorithm());
        assertEquals("ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==", response.getRequestHash());
        assertEquals(true, response.getSoapMessage() != null);
        assertEquals(true, response.getSecurityServer() == null);
    }

    /**
     * Response to subsystem -> service level service call. No user id and
     * request hash.
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test8() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>getRandom</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service><xrd:securityServer><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:serverCode>server1</id:serverCode></xrd:securityServer><xrd:id>ID-1234567890</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion></SOAP-ENV:Header><SOAP-ENV:Body><ns1:getRandomResponse xmlns:ns1=\"http://producer.x-road.ee\"><data>9876543210</data></ns1:getRandomResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceResponseDeserializer deserializer = new TestResponseDeserializer();
        ServiceResponse<String, String> response = deserializer.deserialize(msg);

        assertEquals("FI", response.getConsumer().getXRoadInstance());
        assertEquals("GOV", response.getConsumer().getMemberClass());
        assertEquals("MEMBER1", response.getConsumer().getMemberCode());
        assertEquals("subsystem", response.getConsumer().getSubsystemCode());
        assertEquals(ObjectType.SUBSYSTEM, response.getConsumer().getObjectType());

        assertEquals("FI", response.getProducer().getXRoadInstance());
        assertEquals("COM", response.getProducer().getMemberClass());
        assertEquals("MEMBER2", response.getProducer().getMemberCode());
        assertEquals("subsystem", response.getProducer().getSubsystemCode());
        assertEquals("getRandom", response.getProducer().getServiceCode());
        assertEquals("v1", response.getProducer().getServiceVersion());
        assertEquals("FI", response.getSecurityServer().getXRoadInstance());
        assertEquals("GOV", response.getSecurityServer().getMemberClass());
        assertEquals("MEMBER1", response.getSecurityServer().getMemberCode());
        assertEquals("server1", response.getSecurityServer().getServerCode());
        assertEquals("ID-1234567890", response.getId());
        assertEquals(null, response.getUserId());
        assertEquals("4.0", response.getProtocolVersion());
        assertEquals(ObjectType.SERVICE, response.getProducer().getObjectType());
        assertEquals("9876543210", response.getResponseData());

        assertEquals(null, response.getRequestHashAlgorithm());
        assertEquals(null, response.getRequestHash());

        assertEquals(false, response.hasError());
        assertEquals(true, response.getSoapMessage() != null);
    }

    /**
     * Response to subsystem -> service level service call. Producer namespace
     * URI parameter used.
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test9() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>getRandom</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service><xrd:userId>EE1234567890</xrd:userId><xrd:id>ID-1234567890</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion><xrd:requestHash algorithmId=\"SHA-512\">ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==</xrd:requestHash></SOAP-ENV:Header><SOAP-ENV:Body><ns1:getRandomResponse xmlns:ns1=\"http://producer.x-road.ee\"><data>9876543210</data></ns1:getRandomResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        SOAPMessage msg = SOAPHelper.toSOAP(soapString);
        assertEquals(true, msg != null);
        ServiceResponseDeserializer deserializer = new TestResponseDeserializer();
        ServiceResponse<String, String> response = deserializer.deserialize(msg, "http://producer.x-road.ee");

        assertEquals("FI", response.getConsumer().getXRoadInstance());
        assertEquals("GOV", response.getConsumer().getMemberClass());
        assertEquals("MEMBER1", response.getConsumer().getMemberCode());
        assertEquals("subsystem", response.getConsumer().getSubsystemCode());
        assertEquals(ObjectType.SUBSYSTEM, response.getConsumer().getObjectType());

        assertEquals("FI", response.getProducer().getXRoadInstance());
        assertEquals("COM", response.getProducer().getMemberClass());
        assertEquals("MEMBER2", response.getProducer().getMemberCode());
        assertEquals("subsystem", response.getProducer().getSubsystemCode());
        assertEquals("getRandom", response.getProducer().getServiceCode());
        assertEquals("v1", response.getProducer().getServiceVersion());
        assertEquals("ID-1234567890", response.getId());
        assertEquals("EE1234567890", response.getUserId());
        assertEquals("4.0", response.getProtocolVersion());
        assertEquals(ObjectType.SERVICE, response.getProducer().getObjectType());
        assertEquals("9876543210", response.getResponseData());

        assertEquals("SHA-512", response.getRequestHashAlgorithm());
        assertEquals("ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==", response.getRequestHash());
        assertEquals(true, response.getSoapMessage() != null);
        assertEquals(true, response.getSecurityServer() == null);
    }

    /**
     * Response to subsystem -> service level service call. Erroneous Producer
     * namespace URI parameter used.
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test10() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>getRandom</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service><xrd:userId>EE1234567890</xrd:userId><xrd:id>ID-1234567890</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion><xrd:requestHash algorithmId=\"SHA-512\">ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==</xrd:requestHash></SOAP-ENV:Header><SOAP-ENV:Body><ns1:getRandomResponse xmlns:ns1=\"http://producer.x-road.ee\"><request><data>1234567890</data></request>  <response>    <data>9876543210</data>    </response>  </ns1:getRandomResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceResponseDeserializer deserializer = new TestResponseDeserializer();
        ServiceResponse<String, String> response = deserializer.deserialize(msg, "http://test.com");

        assertEquals("FI", response.getConsumer().getXRoadInstance());
        assertEquals("GOV", response.getConsumer().getMemberClass());
        assertEquals("MEMBER1", response.getConsumer().getMemberCode());
        assertEquals("subsystem", response.getConsumer().getSubsystemCode());
        assertEquals(ObjectType.SUBSYSTEM, response.getConsumer().getObjectType());

        assertEquals("FI", response.getProducer().getXRoadInstance());
        assertEquals("COM", response.getProducer().getMemberClass());
        assertEquals("MEMBER2", response.getProducer().getMemberCode());
        assertEquals("subsystem", response.getProducer().getSubsystemCode());
        assertEquals("getRandom", response.getProducer().getServiceCode());
        assertEquals("v1", response.getProducer().getServiceVersion());
        assertEquals("ID-1234567890", response.getId());
        assertEquals("EE1234567890", response.getUserId());
        assertEquals("4.0", response.getProtocolVersion());
        assertEquals(ObjectType.SERVICE, response.getProducer().getObjectType());
        assertEquals(null, response.getRequestData());
        assertEquals(null, response.getResponseData());

        assertEquals("SHA-512", response.getRequestHashAlgorithm());
        assertEquals("ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==", response.getRequestHash());
        assertEquals(false, response.hasError());
        assertEquals(true, response.getSoapMessage() != null);
        assertEquals(true, response.getSecurityServer() == null);
    }

    /**
     * Response to subsystem -> service level service call. Producer namespace
     * URI parameter used. Response string inside response element - no child
     * elements.
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test11() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>getRandom</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service><xrd:userId>EE1234567890</xrd:userId><xrd:id>ID-1234567890</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion><xrd:requestHash algorithmId=\"SHA-512\">ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==</xrd:requestHash></SOAP-ENV:Header><SOAP-ENV:Body><ns1:getRandomResponse xmlns:ns1=\"http://producer.x-road.ee\">9876543210</ns1:getRandomResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        SOAPMessage msg = SOAPHelper.toSOAP(soapString);
        assertEquals(true, msg != null);
        ServiceResponseDeserializer deserializer = new TestResponseDeserializer3();
        ServiceResponse<String, String> response = deserializer.deserialize(msg, "http://producer.x-road.ee");

        assertEquals("FI", response.getConsumer().getXRoadInstance());
        assertEquals("GOV", response.getConsumer().getMemberClass());
        assertEquals("MEMBER1", response.getConsumer().getMemberCode());
        assertEquals("subsystem", response.getConsumer().getSubsystemCode());
        assertEquals(ObjectType.SUBSYSTEM, response.getConsumer().getObjectType());

        assertEquals("FI", response.getProducer().getXRoadInstance());
        assertEquals("COM", response.getProducer().getMemberClass());
        assertEquals("MEMBER2", response.getProducer().getMemberCode());
        assertEquals("subsystem", response.getProducer().getSubsystemCode());
        assertEquals("getRandom", response.getProducer().getServiceCode());
        assertEquals("v1", response.getProducer().getServiceVersion());
        assertEquals("ID-1234567890", response.getId());
        assertEquals("EE1234567890", response.getUserId());
        assertEquals("4.0", response.getProtocolVersion());
        assertEquals(ObjectType.SERVICE, response.getProducer().getObjectType());
        assertEquals("9876543210", response.getResponseData());

        assertEquals("SHA-512", response.getRequestHashAlgorithm());
        assertEquals("ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==", response.getRequestHash());
        assertEquals(true, response.getSoapMessage() != null);
        assertEquals(true, response.getSecurityServer() == null);
    }

    /**
     * Response to subsystem -> service level service call. No NS.
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test12() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>getRandom</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service><xrd:userId>EE1234567890</xrd:userId><xrd:id>ID-1234567890</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion><xrd:requestHash algorithmId=\"SHA-512\">ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==</xrd:requestHash></SOAP-ENV:Header><SOAP-ENV:Body><getRandomResponse><data>9876543210</data></getRandomResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceResponseDeserializer deserializer = new TestResponseDeserializer();
        ServiceResponse<String, String> response = deserializer.deserialize(msg);

        assertEquals("FI", response.getConsumer().getXRoadInstance());
        assertEquals("GOV", response.getConsumer().getMemberClass());
        assertEquals("MEMBER1", response.getConsumer().getMemberCode());
        assertEquals("subsystem", response.getConsumer().getSubsystemCode());
        assertEquals(ObjectType.SUBSYSTEM, response.getConsumer().getObjectType());

        assertEquals("FI", response.getProducer().getXRoadInstance());
        assertEquals("COM", response.getProducer().getMemberClass());
        assertEquals("MEMBER2", response.getProducer().getMemberCode());
        assertEquals("subsystem", response.getProducer().getSubsystemCode());
        assertEquals("getRandom", response.getProducer().getServiceCode());
        assertEquals("v1", response.getProducer().getServiceVersion());
        assertEquals("ID-1234567890", response.getId());
        assertEquals("EE1234567890", response.getUserId());
        assertEquals("4.0", response.getProtocolVersion());
        assertEquals(ObjectType.SERVICE, response.getProducer().getObjectType());
        assertEquals("9876543210", response.getResponseData());

        assertEquals("SHA-512", response.getRequestHashAlgorithm());
        assertEquals("ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==", response.getRequestHash());
        assertEquals(true, response.getSoapMessage() != null);
        assertEquals(true, response.getSecurityServer() == null);
    }

    /**
     * Response to subsystem -> service level service call. Security token and
     * token type with namespace prefix.
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test13() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\" xmlns:ext=\"http://x-road.eu/xsd/security-token.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>getRandom</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service><xrd:userId>EE1234567890</xrd:userId><xrd:id>ID-1234567890</xrd:id><ext:securityToken ext:tokenType=\"urn:ietf:params:oauth:token-type:jwt\">eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiVGVzdCJ9.negHPJEwkKcNcgVC6dNtzPZk_48Kig6IzxnabL9jKsw</ext:securityToken><xrd:protocolVersion>4.5</xrd:protocolVersion><xrd:requestHash algorithmId=\"SHA-512\">ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==</xrd:requestHash></SOAP-ENV:Header><SOAP-ENV:Body><ns1:getRandomResponse xmlns:ns1=\"http://producer.x-road.ee\"><ns1:data>9876543210</ns1:data></ns1:getRandomResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceResponseDeserializer deserializer = new TestResponseDeserializer();
        ServiceResponse<String, String> response = deserializer.deserialize(msg);

        assertEquals("FI", response.getConsumer().getXRoadInstance());
        assertEquals("GOV", response.getConsumer().getMemberClass());
        assertEquals("MEMBER1", response.getConsumer().getMemberCode());
        assertEquals("subsystem", response.getConsumer().getSubsystemCode());
        assertEquals(ObjectType.SUBSYSTEM, response.getConsumer().getObjectType());

        assertEquals("FI", response.getProducer().getXRoadInstance());
        assertEquals("COM", response.getProducer().getMemberClass());
        assertEquals("MEMBER2", response.getProducer().getMemberCode());
        assertEquals("subsystem", response.getProducer().getSubsystemCode());
        assertEquals("getRandom", response.getProducer().getServiceCode());
        assertEquals("v1", response.getProducer().getServiceVersion());
        assertEquals("ID-1234567890", response.getId());
        assertEquals("EE1234567890", response.getUserId());
        assertEquals("eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiVGVzdCJ9.negHPJEwkKcNcgVC6dNtzPZk_48Kig6IzxnabL9jKsw", response.getSecurityToken());
        assertEquals("urn:ietf:params:oauth:token-type:jwt", response.getSecurityTokenType());
        assertEquals("4.5", response.getProtocolVersion());
        assertEquals(ObjectType.SERVICE, response.getProducer().getObjectType());
        assertEquals("9876543210", response.getResponseData());

        assertEquals("SHA-512", response.getRequestHashAlgorithm());
        assertEquals("ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==", response.getRequestHash());
        assertEquals(true, response.getSoapMessage() != null);
        assertEquals(true, response.getSecurityServer() == null);
    }

    /**
     * Response to subsystem -> service level service call. Security token and
     * token type. Token type without namespace prefix.
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test14() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\" xmlns:ext=\"http://x-road.eu/xsd/security-token.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>getRandom</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service><xrd:userId>EE1234567890</xrd:userId><xrd:id>ID-1234567890</xrd:id><ext:securityToken tokenType=\"urn:ietf:params:oauth:token-type:jwt\">eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiVGVzdCJ9.negHPJEwkKcNcgVC6dNtzPZk_48Kig6IzxnabL9jKsw</ext:securityToken><xrd:protocolVersion>4.5</xrd:protocolVersion><xrd:requestHash algorithmId=\"SHA-512\">ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==</xrd:requestHash></SOAP-ENV:Header><SOAP-ENV:Body><ns1:getRandomResponse xmlns:ns1=\"http://producer.x-road.ee\"><ns1:data>9876543210</ns1:data></ns1:getRandomResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceResponseDeserializer deserializer = new TestResponseDeserializer();
        ServiceResponse<String, String> response = deserializer.deserialize(msg);

        assertEquals("FI", response.getConsumer().getXRoadInstance());
        assertEquals("GOV", response.getConsumer().getMemberClass());
        assertEquals("MEMBER1", response.getConsumer().getMemberCode());
        assertEquals("subsystem", response.getConsumer().getSubsystemCode());
        assertEquals(ObjectType.SUBSYSTEM, response.getConsumer().getObjectType());

        assertEquals("FI", response.getProducer().getXRoadInstance());
        assertEquals("COM", response.getProducer().getMemberClass());
        assertEquals("MEMBER2", response.getProducer().getMemberCode());
        assertEquals("subsystem", response.getProducer().getSubsystemCode());
        assertEquals("getRandom", response.getProducer().getServiceCode());
        assertEquals("v1", response.getProducer().getServiceVersion());
        assertEquals("ID-1234567890", response.getId());
        assertEquals("EE1234567890", response.getUserId());
        assertEquals("eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiVGVzdCJ9.negHPJEwkKcNcgVC6dNtzPZk_48Kig6IzxnabL9jKsw", response.getSecurityToken());
        assertEquals("urn:ietf:params:oauth:token-type:jwt", response.getSecurityTokenType());
        assertEquals("4.5", response.getProtocolVersion());
        assertEquals(ObjectType.SERVICE, response.getProducer().getObjectType());
        assertEquals("9876543210", response.getResponseData());

        assertEquals("SHA-512", response.getRequestHashAlgorithm());
        assertEquals("ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==", response.getRequestHash());
        assertEquals(true, response.getSoapMessage() != null);
        assertEquals(true, response.getSecurityServer() == null);
    }

    /**
     * Response to subsystem -> service level service call. Security token
     * without token type.
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test15() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\" xmlns:ext=\"http://x-road.eu/xsd/security-token.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>getRandom</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service><xrd:userId>EE1234567890</xrd:userId><xrd:id>ID-1234567890</xrd:id><ext:securityToken>eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiVGVzdCJ9.negHPJEwkKcNcgVC6dNtzPZk_48Kig6IzxnabL9jKsw</ext:securityToken><xrd:protocolVersion>4.5</xrd:protocolVersion><xrd:requestHash algorithmId=\"SHA-512\">ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==</xrd:requestHash></SOAP-ENV:Header><SOAP-ENV:Body><ns1:getRandomResponse xmlns:ns1=\"http://producer.x-road.ee\"><ns1:data>9876543210</ns1:data></ns1:getRandomResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceResponseDeserializer deserializer = new TestResponseDeserializer();
        ServiceResponse<String, String> response = deserializer.deserialize(msg);

        assertEquals("FI", response.getConsumer().getXRoadInstance());
        assertEquals("GOV", response.getConsumer().getMemberClass());
        assertEquals("MEMBER1", response.getConsumer().getMemberCode());
        assertEquals("subsystem", response.getConsumer().getSubsystemCode());
        assertEquals(ObjectType.SUBSYSTEM, response.getConsumer().getObjectType());

        assertEquals("FI", response.getProducer().getXRoadInstance());
        assertEquals("COM", response.getProducer().getMemberClass());
        assertEquals("MEMBER2", response.getProducer().getMemberCode());
        assertEquals("subsystem", response.getProducer().getSubsystemCode());
        assertEquals("getRandom", response.getProducer().getServiceCode());
        assertEquals("v1", response.getProducer().getServiceVersion());
        assertEquals("ID-1234567890", response.getId());
        assertEquals("EE1234567890", response.getUserId());
        assertEquals("eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiVGVzdCJ9.negHPJEwkKcNcgVC6dNtzPZk_48Kig6IzxnabL9jKsw", response.getSecurityToken());
        assertEquals(null, response.getSecurityTokenType());
        assertEquals("4.5", response.getProtocolVersion());
        assertEquals(ObjectType.SERVICE, response.getProducer().getObjectType());
        assertEquals("9876543210", response.getResponseData());

        assertEquals("SHA-512", response.getRequestHashAlgorithm());
        assertEquals("ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==", response.getRequestHash());
        assertEquals(true, response.getSoapMessage() != null);
        assertEquals(true, response.getSecurityServer() == null);
    }

    /**
     * Response to subsystem -> service level service call. Technical error with
     * header.
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void testTechError1() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>getRandom</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service><xrd:id>ID-1234567890</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion></SOAP-ENV:Header><SOAP-ENV:Body><SOAP-ENV:Fault><faultcode>fault code</faultcode><faultstring>fault string</faultstring><faultactor>fault actor</faultactor><detail>fault details</detail></SOAP-ENV:Fault></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceResponseDeserializer deserializer = new TestResponseDeserializer();
        ServiceResponse<String, String> response = deserializer.deserialize(msg);

        assertEquals("FI", response.getConsumer().getXRoadInstance());
        assertEquals("GOV", response.getConsumer().getMemberClass());
        assertEquals("MEMBER1", response.getConsumer().getMemberCode());
        assertEquals("subsystem", response.getConsumer().getSubsystemCode());
        assertEquals(ObjectType.SUBSYSTEM, response.getConsumer().getObjectType());

        assertEquals("FI", response.getProducer().getXRoadInstance());
        assertEquals("COM", response.getProducer().getMemberClass());
        assertEquals("MEMBER2", response.getProducer().getMemberCode());
        assertEquals("subsystem", response.getProducer().getSubsystemCode());
        assertEquals("getRandom", response.getProducer().getServiceCode());
        assertEquals("v1", response.getProducer().getServiceVersion());
        assertEquals("ID-1234567890", response.getId());
        assertEquals(null, response.getUserId());
        assertEquals("4.0", response.getProtocolVersion());
        assertEquals(ObjectType.SERVICE, response.getProducer().getObjectType());
        assertEquals(null, response.getRequestData());
        assertEquals(null, response.getResponseData());

        assertEquals(null, response.getRequestHashAlgorithm());
        assertEquals(null, response.getRequestHash());

        assertEquals(true, response.hasError());
        assertEquals("fault code", response.getErrorMessage().getFaultCode());
        assertEquals("fault string", response.getErrorMessage().getFaultString());
        assertEquals("fault actor", response.getErrorMessage().getFaultActor());
        assertEquals("fault details", response.getErrorMessage().getDetail());
        assertEquals(ErrorMessageType.STANDARD_SOAP_ERROR_MESSAGE, response.getErrorMessage().getErrorMessageType());
        assertEquals(true, response.getSoapMessage() != null);
        assertEquals(true, response.getSecurityServer() == null);
    }

    /**
     * Response to subsystem -> service level service call. Technical error
     * without header.
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void testTechError2() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header /><SOAP-ENV:Body><SOAP-ENV:Fault><faultcode>fault code</faultcode><faultstring>fault string</faultstring><faultactor>fault actor</faultactor><detail>fault details</detail></SOAP-ENV:Fault></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceResponseDeserializer deserializer = new TestResponseDeserializer();
        ServiceResponse<String, String> response = deserializer.deserialize(msg);

        assertEquals(null, response.getConsumer());
        assertEquals(null, response.getProducer());
        assertEquals(null, response.getId());
        assertEquals(null, response.getUserId());
        assertEquals(null, response.getRequestData());
        assertEquals(null, response.getResponseData());
        assertEquals(null, response.getRequestHashAlgorithm());
        assertEquals(null, response.getRequestHash());
        assertEquals("4.0", response.getProtocolVersion());

        assertEquals(true, response.hasError());
        assertEquals("fault code", response.getErrorMessage().getFaultCode());
        assertEquals("fault string", response.getErrorMessage().getFaultString());
        assertEquals("fault actor", response.getErrorMessage().getFaultActor());
        assertEquals("fault details", response.getErrorMessage().getDetail());
        assertEquals(ErrorMessageType.STANDARD_SOAP_ERROR_MESSAGE, response.getErrorMessage().getErrorMessageType());
        assertEquals(true, response.getSoapMessage() != null);
        assertEquals(true, response.getSecurityServer() == null);
    }

    /**
     * Response to subsystem -> service level service call. Technical error with
     * header.
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void testTechError3() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>getRandom</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service><xrd:id>ID-1234567890</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion></SOAP-ENV:Header><SOAP-ENV:Body><SOAP-ENV:Fault><faultcode>fault code</faultcode><faultstring>fault string</faultstring><faultactor>fault actor</faultactor><detail>fault details</detail></SOAP-ENV:Fault></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceResponseDeserializer deserializer = new TestResponseDeserializer();
        ServiceResponse<String, String> response = deserializer.deserialize(msg);

        assertEquals("FI", response.getConsumer().getXRoadInstance());
        assertEquals("GOV", response.getConsumer().getMemberClass());
        assertEquals("MEMBER1", response.getConsumer().getMemberCode());
        assertEquals("subsystem", response.getConsumer().getSubsystemCode());
        assertEquals(ObjectType.SUBSYSTEM, response.getConsumer().getObjectType());

        assertEquals("FI", response.getProducer().getXRoadInstance());
        assertEquals("COM", response.getProducer().getMemberClass());
        assertEquals("MEMBER2", response.getProducer().getMemberCode());
        assertEquals("subsystem", response.getProducer().getSubsystemCode());
        assertEquals("getRandom", response.getProducer().getServiceCode());
        assertEquals("v1", response.getProducer().getServiceVersion());
        assertEquals("ID-1234567890", response.getId());
        assertEquals(null, response.getUserId());
        assertEquals("4.0", response.getProtocolVersion());
        assertEquals(ObjectType.SERVICE, response.getProducer().getObjectType());
        assertEquals(null, response.getRequestData());
        assertEquals(null, response.getResponseData());

        assertEquals(null, response.getRequestHashAlgorithm());
        assertEquals(null, response.getRequestHash());

        assertEquals(true, response.hasError());
        assertEquals("fault code", response.getErrorMessage().getFaultCode());
        assertEquals("fault string", response.getErrorMessage().getFaultString());
        assertEquals("fault actor", response.getErrorMessage().getFaultActor());
        assertEquals("fault details", response.getErrorMessage().getDetail());
        assertEquals(ErrorMessageType.STANDARD_SOAP_ERROR_MESSAGE, response.getErrorMessage().getErrorMessageType());
        assertEquals(true, response.getSoapMessage() != null);
        assertEquals(true, response.getSecurityServer() == null);
    }

    /**
     * Response to subsystem -> service level service call. Technical error
     * without header. Camel case error elements.
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void testTechError4() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header /><SOAP-ENV:Body><SOAP-ENV:Fault><faultCode>fault code</faultCode><faultString>fault string</faultString><faultActor>fault actor</faultActor><detail>fault details</detail></SOAP-ENV:Fault></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceResponseDeserializer deserializer = new TestResponseDeserializer();
        ServiceResponse<String, String> response = deserializer.deserialize(msg);

        assertEquals(null, response.getConsumer());
        assertEquals(null, response.getProducer());
        assertEquals(null, response.getId());
        assertEquals(null, response.getUserId());
        assertEquals(null, response.getRequestData());
        assertEquals(null, response.getResponseData());
        assertEquals(null, response.getRequestHashAlgorithm());
        assertEquals(null, response.getRequestHash());
        assertEquals("4.0", response.getProtocolVersion());

        assertEquals(true, response.hasError());
        assertEquals("fault code", response.getErrorMessage().getFaultCode());
        assertEquals("fault string", response.getErrorMessage().getFaultString());
        assertEquals("fault actor", response.getErrorMessage().getFaultActor());
        assertEquals("fault details", response.getErrorMessage().getDetail());
        assertEquals(ErrorMessageType.STANDARD_SOAP_ERROR_MESSAGE, response.getErrorMessage().getErrorMessageType());
        assertEquals(true, response.getSoapMessage() != null);
        assertEquals(true, response.getSecurityServer() == null);
    }

    /**
     * Response to subsystem -> service level service call. Technical error
     * without header. Camel case error elements. Detail with child elements.
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void testTechError5() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header /><SOAP-ENV:Body><SOAP-ENV:Fault><faultCode>fault code</faultCode><faultString>fault string</faultString><faultActor>fault actor</faultActor><detail><order>order element</order><confirmation>confirmation element</confirmation></detail></SOAP-ENV:Fault></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceResponseDeserializer deserializer = new TestResponseDeserializer2();
        ServiceResponse<String, String> response = deserializer.deserialize(msg);

        assertEquals(null, response.getConsumer());
        assertEquals(null, response.getProducer());
        assertEquals(null, response.getId());
        assertEquals(null, response.getUserId());
        assertEquals(null, response.getRequestData());
        assertEquals(null, response.getResponseData());
        assertEquals(null, response.getRequestHashAlgorithm());
        assertEquals(null, response.getRequestHash());
        assertEquals("4.0", response.getProtocolVersion());

        assertEquals(true, response.hasError());
        assertEquals("fault code", response.getErrorMessage().getFaultCode());
        assertEquals("fault string", response.getErrorMessage().getFaultString());
        assertEquals("fault actor", response.getErrorMessage().getFaultActor());
        assertEquals("order element", ((Map) response.getErrorMessage().getDetail()).get("order"));
        assertEquals("confirmation element", ((Map) response.getErrorMessage().getDetail()).get("confirmation"));
        assertEquals(ErrorMessageType.STANDARD_SOAP_ERROR_MESSAGE, response.getErrorMessage().getErrorMessageType());
        assertEquals(true, response.getSoapMessage() != null);
        assertEquals(true, response.getSecurityServer() == null);
    }

    /**
     * Response to subsystem -> service level service call. Technical error
     * without header. Camel case error elements. Detail missing.
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void testTechError6() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header /><SOAP-ENV:Body><SOAP-ENV:Fault><faultCode>fault code</faultCode><faultString>fault string</faultString><faultActor>fault actor</faultActor></SOAP-ENV:Fault></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceResponseDeserializer deserializer = new TestResponseDeserializer2();
        ServiceResponse<String, String> response = deserializer.deserialize(msg);

        assertEquals(null, response.getConsumer());
        assertEquals(null, response.getProducer());
        assertEquals(null, response.getId());
        assertEquals(null, response.getUserId());
        assertEquals(null, response.getRequestData());
        assertEquals(null, response.getResponseData());
        assertEquals(null, response.getRequestHashAlgorithm());
        assertEquals(null, response.getRequestHash());
        assertEquals("4.0", response.getProtocolVersion());

        assertEquals(true, response.hasError());
        assertEquals("fault code", response.getErrorMessage().getFaultCode());
        assertEquals("fault string", response.getErrorMessage().getFaultString());
        assertEquals("fault actor", response.getErrorMessage().getFaultActor());
        assertEquals(null, response.getErrorMessage().getDetail());
        assertEquals(ErrorMessageType.STANDARD_SOAP_ERROR_MESSAGE, response.getErrorMessage().getErrorMessageType());
        assertEquals(true, response.getSoapMessage() != null);
        assertEquals(true, response.getSecurityServer() == null);
    }

    /**
     * Response to subsystem -> service level service call. Non technical error.
     * Camel case element names.
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void testNonTechError1() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>getRandom</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service><xrd:id>ID-1234567890</xrd:id><xrd:protocolVersion>5.0</xrd:protocolVersion></SOAP-ENV:Header><SOAP-ENV:Body><ns1:getRandomResponse xmlns:ns1=\"http://producer.x-road.ee\"><faultCode>fault code</faultCode><faultString>fault string</faultString></ns1:getRandomResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceResponseDeserializer deserializer = new TestResponseDeserializer();
        ServiceResponse<String, String> response = deserializer.deserialize(msg);

        assertEquals("FI", response.getConsumer().getXRoadInstance());
        assertEquals("GOV", response.getConsumer().getMemberClass());
        assertEquals("MEMBER1", response.getConsumer().getMemberCode());
        assertEquals("subsystem", response.getConsumer().getSubsystemCode());
        assertEquals(ObjectType.SUBSYSTEM, response.getConsumer().getObjectType());

        assertEquals("FI", response.getProducer().getXRoadInstance());
        assertEquals("COM", response.getProducer().getMemberClass());
        assertEquals("MEMBER2", response.getProducer().getMemberCode());
        assertEquals("subsystem", response.getProducer().getSubsystemCode());
        assertEquals("getRandom", response.getProducer().getServiceCode());
        assertEquals("v1", response.getProducer().getServiceVersion());
        assertEquals("ID-1234567890", response.getId());
        assertEquals(null, response.getUserId());
        assertEquals("5.0", response.getProtocolVersion());
        assertEquals(ObjectType.SERVICE, response.getProducer().getObjectType());
        assertEquals(null, response.getResponseData());

        assertEquals(null, response.getRequestHashAlgorithm());
        assertEquals(null, response.getRequestHash());

        assertEquals(true, response.hasError());
        assertEquals("fault code", response.getErrorMessage().getFaultCode());
        assertEquals("fault string", response.getErrorMessage().getFaultString());
        assertEquals(ErrorMessageType.NON_TECHNICAL_SOAP_ERROR_MESSAGE, response.getErrorMessage().getErrorMessageType());
        assertEquals(true, response.getSoapMessage() != null);
        assertEquals(true, response.getSecurityServer() == null);
    }

    /**
     * Response to subsystem -> service level service call. Non technical error.
     * Lower case element names.
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void testNonTechError2() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>getRandom</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service><xrd:id>ID-1234567890</xrd:id><xrd:protocolVersion>6.0</xrd:protocolVersion></SOAP-ENV:Header><SOAP-ENV:Body><ns1:getRandomResponse xmlns:ns1=\"http://producer.x-road.ee\"><faultcode>fault code</faultcode><faultstring>fault string</faultstring></ns1:getRandomResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceResponseDeserializer deserializer = new TestResponseDeserializer();
        ServiceResponse<String, String> response = deserializer.deserialize(msg);

        assertEquals("FI", response.getConsumer().getXRoadInstance());
        assertEquals("GOV", response.getConsumer().getMemberClass());
        assertEquals("MEMBER1", response.getConsumer().getMemberCode());
        assertEquals("subsystem", response.getConsumer().getSubsystemCode());
        assertEquals(ObjectType.SUBSYSTEM, response.getConsumer().getObjectType());

        assertEquals("FI", response.getProducer().getXRoadInstance());
        assertEquals("COM", response.getProducer().getMemberClass());
        assertEquals("MEMBER2", response.getProducer().getMemberCode());
        assertEquals("subsystem", response.getProducer().getSubsystemCode());
        assertEquals("getRandom", response.getProducer().getServiceCode());
        assertEquals("v1", response.getProducer().getServiceVersion());
        assertEquals("ID-1234567890", response.getId());
        assertEquals(null, response.getUserId());
        assertEquals("6.0", response.getProtocolVersion());
        assertEquals(ObjectType.SERVICE, response.getProducer().getObjectType());
        assertEquals(null, response.getResponseData());

        assertEquals(null, response.getRequestHashAlgorithm());
        assertEquals(null, response.getRequestHash());

        assertEquals(true, response.hasError());
        assertEquals("fault code", response.getErrorMessage().getFaultCode());
        assertEquals("fault string", response.getErrorMessage().getFaultString());
        assertEquals(ErrorMessageType.NON_TECHNICAL_SOAP_ERROR_MESSAGE, response.getErrorMessage().getErrorMessageType());
        assertEquals(true, response.getSoapMessage() != null);
        assertEquals(true, response.getSecurityServer() == null);
    }

    /**
     * Response to subsystem -> service level service call. Non technical error.
     * Camel case element names. Fault code only.
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void testNonTechError3() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>getRandom</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service><xrd:id>ID-1234567890</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion></SOAP-ENV:Header><SOAP-ENV:Body><ns1:getRandomResponse xmlns:ns1=\"http://producer.x-road.ee\"><faultCode>fault code</faultCode></ns1:getRandomResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceResponseDeserializer deserializer = new TestResponseDeserializer();
        ServiceResponse<String, String> response = deserializer.deserialize(msg);

        assertEquals("FI", response.getConsumer().getXRoadInstance());
        assertEquals("GOV", response.getConsumer().getMemberClass());
        assertEquals("MEMBER1", response.getConsumer().getMemberCode());
        assertEquals("subsystem", response.getConsumer().getSubsystemCode());
        assertEquals(ObjectType.SUBSYSTEM, response.getConsumer().getObjectType());

        assertEquals("FI", response.getProducer().getXRoadInstance());
        assertEquals("COM", response.getProducer().getMemberClass());
        assertEquals("MEMBER2", response.getProducer().getMemberCode());
        assertEquals("subsystem", response.getProducer().getSubsystemCode());
        assertEquals("getRandom", response.getProducer().getServiceCode());
        assertEquals("v1", response.getProducer().getServiceVersion());
        assertEquals("ID-1234567890", response.getId());
        assertEquals(null, response.getUserId());
        assertEquals("4.0", response.getProtocolVersion());
        assertEquals(ObjectType.SERVICE, response.getProducer().getObjectType());
        assertEquals(null, response.getResponseData());

        assertEquals(null, response.getRequestHashAlgorithm());
        assertEquals(null, response.getRequestHash());

        assertEquals(true, response.hasError());
        assertEquals("fault code", response.getErrorMessage().getFaultCode());
        assertEquals(null, response.getErrorMessage().getFaultString());
        assertEquals(ErrorMessageType.NON_TECHNICAL_SOAP_ERROR_MESSAGE, response.getErrorMessage().getErrorMessageType());
        assertEquals(true, response.getSoapMessage() != null);
        assertEquals(true, response.getSecurityServer() == null);
    }

    /**
     * Response to subsystem -> service level service call. Non technical error.
     * Camel case element names. Fault string only.
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void testNonTechError4() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>getRandom</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service><xrd:id>ID-1234567890</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion></SOAP-ENV:Header><SOAP-ENV:Body><ns1:getRandomResponse xmlns:ns1=\"http://producer.x-road.ee\"><faultString>fault string</faultString></ns1:getRandomResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceResponseDeserializer deserializer = new TestResponseDeserializer();
        ServiceResponse<String, String> response = deserializer.deserialize(msg);

        assertEquals("FI", response.getConsumer().getXRoadInstance());
        assertEquals("GOV", response.getConsumer().getMemberClass());
        assertEquals("MEMBER1", response.getConsumer().getMemberCode());
        assertEquals("subsystem", response.getConsumer().getSubsystemCode());
        assertEquals(ObjectType.SUBSYSTEM, response.getConsumer().getObjectType());

        assertEquals("FI", response.getProducer().getXRoadInstance());
        assertEquals("COM", response.getProducer().getMemberClass());
        assertEquals("MEMBER2", response.getProducer().getMemberCode());
        assertEquals("subsystem", response.getProducer().getSubsystemCode());
        assertEquals("getRandom", response.getProducer().getServiceCode());
        assertEquals("v1", response.getProducer().getServiceVersion());
        assertEquals("ID-1234567890", response.getId());
        assertEquals(null, response.getUserId());
        assertEquals("4.0", response.getProtocolVersion());
        assertEquals(ObjectType.SERVICE, response.getProducer().getObjectType());
        assertEquals(null, response.getResponseData());

        assertEquals(null, response.getRequestHashAlgorithm());
        assertEquals(null, response.getRequestHash());

        assertEquals(true, response.hasError());
        assertEquals(null, response.getErrorMessage().getFaultCode());
        assertEquals("fault string", response.getErrorMessage().getFaultString());
        assertEquals(ErrorMessageType.NON_TECHNICAL_SOAP_ERROR_MESSAGE, response.getErrorMessage().getErrorMessageType());
        assertEquals(true, response.getSoapMessage() != null);
        assertEquals(true, response.getSecurityServer() == null);
    }

    /**
     * Response to subsystem -> service level service call. Non technical error.
     * Lower case element names. Fault code and fault string empty.
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void testNonTechError5() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>getRandom</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service><xrd:id>ID-1234567890</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion></SOAP-ENV:Header><SOAP-ENV:Body><ns1:getRandomResponse xmlns:ns1=\"http://producer.x-road.ee\"><faultcode></faultcode><faultstring></faultstring></ns1:getRandomResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceResponseDeserializer deserializer = new TestResponseDeserializer();
        ServiceResponse<String, String> response = deserializer.deserialize(msg);

        assertEquals("FI", response.getConsumer().getXRoadInstance());
        assertEquals("GOV", response.getConsumer().getMemberClass());
        assertEquals("MEMBER1", response.getConsumer().getMemberCode());
        assertEquals("subsystem", response.getConsumer().getSubsystemCode());
        assertEquals(ObjectType.SUBSYSTEM, response.getConsumer().getObjectType());

        assertEquals("FI", response.getProducer().getXRoadInstance());
        assertEquals("COM", response.getProducer().getMemberClass());
        assertEquals("MEMBER2", response.getProducer().getMemberCode());
        assertEquals("subsystem", response.getProducer().getSubsystemCode());
        assertEquals("getRandom", response.getProducer().getServiceCode());
        assertEquals("v1", response.getProducer().getServiceVersion());
        assertEquals("ID-1234567890", response.getId());
        assertEquals(null, response.getUserId());
        assertEquals("4.0", response.getProtocolVersion());
        assertEquals(ObjectType.SERVICE, response.getProducer().getObjectType());
        assertEquals(null, response.getResponseData());

        assertEquals(null, response.getRequestHashAlgorithm());
        assertEquals(null, response.getRequestHash());

        assertEquals(true, response.hasError());
        assertEquals("", response.getErrorMessage().getFaultCode());
        assertEquals("", response.getErrorMessage().getFaultString());
        assertEquals(ErrorMessageType.NON_TECHNICAL_SOAP_ERROR_MESSAGE, response.getErrorMessage().getErrorMessageType());
        assertEquals(true, response.getSoapMessage() != null);
        assertEquals(true, response.getSecurityServer() == null);
    }

    private class TestResponseDeserializer extends AbstractResponseDeserializer<String, String> {

        protected String deserializeRequestData(Node requestNode) throws SOAPException {
            for (int i = 0; i < requestNode.getChildNodes().getLength(); i++) {
                if (requestNode.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE
                        && requestNode.getChildNodes().item(i).getLocalName().equals("data")) {
                    return requestNode.getChildNodes().item(i).getTextContent();
                }
            }
            return null;
        }

        protected String deserializeResponseData(Node responseNode, SOAPMessage message) throws SOAPException {
            for (int i = 0; i < responseNode.getChildNodes().getLength(); i++) {
                if (responseNode.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE
                        && responseNode.getChildNodes().item(i).getLocalName().equals("data")) {
                    return responseNode.getChildNodes().item(i).getTextContent();
                }
            }
            return null;
        }
    }

    private final class TestResponseDeserializer2 extends TestResponseDeserializer {

        @Override
        protected Object deserializeFaultDetail(Node detailNode) {
            if (detailNode == null) {
                return null;
            }
            return SOAPHelper.nodesToMap(detailNode.getChildNodes());
        }
    }

    private final class TestResponseDeserializer3 extends TestResponseDeserializer {

        @Override
        protected String deserializeResponseData(Node responseNode, SOAPMessage message) throws SOAPException {
            return responseNode.getTextContent();
        }
    }
}
