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
import org.niis.xrd4j.common.member.ProducerMember;
import org.niis.xrd4j.common.message.ErrorMessageType;
import org.niis.xrd4j.common.message.ServiceResponse;
import org.niis.xrd4j.common.util.SOAPHelper;

import org.junit.jupiter.api.Test;

import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test cases for ListServicesResponseDeserializer class.
 *
 * @author Petteri Kivimäki
 */
class ListServicesResponseDeserializerTest {


    /**
     * ListMethods: Subsystem - Service (under subsystem). Response: 1 service
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test1() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>client</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>listMethods</id:serviceCode></xrd:service><xrd:userId>EE1234567890</xrd:userId><xrd:id>ID-1234567890</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion><xrd:requestHash algorithmId=\"SHA-512\">ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==</xrd:requestHash></SOAP-ENV:Header><SOAP-ENV:Body><xrd:listMethodsResponse><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>PRI</id:memberClass><id:memberCode>12345-6</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>testService</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service></xrd:listMethodsResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";

        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceResponseDeserializer deserializer = new ListServicesResponseDeserializer();
        ServiceResponse<String, List<ProducerMember>> response = deserializer.deserialize(msg);

        assertEquals("FI", response.getConsumer().getXRoadInstance());
        assertEquals("GOV", response.getConsumer().getMemberClass());
        assertEquals("MEMBER1", response.getConsumer().getMemberCode());
        assertEquals("client", response.getConsumer().getSubsystemCode());
        assertEquals(ObjectType.SUBSYSTEM, response.getConsumer().getObjectType());

        assertEquals("FI", response.getProducer().getXRoadInstance());
        assertEquals("COM", response.getProducer().getMemberClass());
        assertEquals("MEMBER2", response.getProducer().getMemberCode());
        assertEquals("subsystem", response.getProducer().getSubsystemCode());
        assertEquals("listMethods", response.getProducer().getServiceCode());
        assertEquals(null, response.getProducer().getServiceVersion());
        assertEquals("ID-1234567890", response.getId());
        assertEquals("EE1234567890", response.getUserId());
        assertEquals("4.0", response.getProtocolVersion());
        assertEquals(ObjectType.SERVICE, response.getProducer().getObjectType());
        assertEquals(null, response.getRequestData());

        assertEquals("SHA-512", response.getRequestHashAlgorithm());
        assertEquals("ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==", response.getRequestHash());
        assertEquals(true, response.getSoapMessage() != null);

        assertEquals("FI", response.getResponseData().get(0).getXRoadInstance());
        assertEquals("PRI", response.getResponseData().get(0).getMemberClass());
        assertEquals("12345-6", response.getResponseData().get(0).getMemberCode());
        assertEquals("subsystem", response.getResponseData().get(0).getSubsystemCode());
        assertEquals("testService", response.getResponseData().get(0).getServiceCode());
        assertEquals("v1", response.getResponseData().get(0).getServiceVersion());
        assertEquals(ObjectType.SERVICE, response.getResponseData().get(0).getObjectType());
    }

    /**
     * ListMethods: Member - Service (under subsystem). Response: 1 service
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test2() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"MEMBER\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>listMethods</id:serviceCode></xrd:service><xrd:userId>EE1234567890</xrd:userId><xrd:id>ID-1234567890</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion><xrd:requestHash algorithmId=\"SHA-512\">ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==</xrd:requestHash></SOAP-ENV:Header><SOAP-ENV:Body><xrd:listMethodsResponse><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>PRI</id:memberClass><id:memberCode>12345-6</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>testService</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>PRI</id:memberClass><id:memberCode>12345-7</id:memberCode><id:subsystemCode>subsystem1</id:subsystemCode><id:serviceCode>testService1</id:serviceCode></xrd:service><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>PRI</id:memberClass><id:memberCode>12345-8</id:memberCode><id:serviceCode>testService2</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>PRI</id:memberClass><id:memberCode>12345-9</id:memberCode><id:serviceCode>testService3</id:serviceCode></xrd:service></xrd:listMethodsResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";

        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceResponseDeserializer deserializer = new ListServicesResponseDeserializer();
        ServiceResponse<String, List<ProducerMember>> response = deserializer.deserialize(msg);

        assertEquals("FI", response.getConsumer().getXRoadInstance());
        assertEquals("GOV", response.getConsumer().getMemberClass());
        assertEquals("MEMBER1", response.getConsumer().getMemberCode());
        assertEquals(null, response.getConsumer().getSubsystemCode());
        assertEquals(ObjectType.MEMBER, response.getConsumer().getObjectType());

        assertEquals("FI", response.getProducer().getXRoadInstance());
        assertEquals("COM", response.getProducer().getMemberClass());
        assertEquals("MEMBER2", response.getProducer().getMemberCode());
        assertEquals("subsystem", response.getProducer().getSubsystemCode());
        assertEquals("listMethods", response.getProducer().getServiceCode());
        assertEquals(null, response.getProducer().getServiceVersion());
        assertEquals("ID-1234567890", response.getId());
        assertEquals("EE1234567890", response.getUserId());
        assertEquals("4.0", response.getProtocolVersion());
        assertEquals(ObjectType.SERVICE, response.getProducer().getObjectType());
        assertEquals(null, response.getRequestData());

        assertEquals("SHA-512", response.getRequestHashAlgorithm());
        assertEquals("ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==", response.getRequestHash());
        assertEquals(true, response.getSoapMessage() != null);

        assertEquals("FI", response.getResponseData().get(0).getXRoadInstance());
        assertEquals("PRI", response.getResponseData().get(0).getMemberClass());
        assertEquals("12345-6", response.getResponseData().get(0).getMemberCode());
        assertEquals("subsystem", response.getResponseData().get(0).getSubsystemCode());
        assertEquals("testService", response.getResponseData().get(0).getServiceCode());
        assertEquals("v1", response.getResponseData().get(0).getServiceVersion());
        assertEquals(ObjectType.SERVICE, response.getResponseData().get(0).getObjectType());

        assertEquals("FI", response.getResponseData().get(1).getXRoadInstance());
        assertEquals("PRI", response.getResponseData().get(1).getMemberClass());
        assertEquals("12345-7", response.getResponseData().get(1).getMemberCode());
        assertEquals("subsystem1", response.getResponseData().get(1).getSubsystemCode());
        assertEquals("testService1", response.getResponseData().get(1).getServiceCode());
        assertEquals(null, response.getResponseData().get(1).getServiceVersion());
        assertEquals(ObjectType.SERVICE, response.getResponseData().get(1).getObjectType());

        assertEquals("FI", response.getResponseData().get(2).getXRoadInstance());
        assertEquals("PRI", response.getResponseData().get(2).getMemberClass());
        assertEquals("12345-8", response.getResponseData().get(2).getMemberCode());
        assertEquals(null, response.getResponseData().get(2).getSubsystemCode());
        assertEquals("testService2", response.getResponseData().get(2).getServiceCode());
        assertEquals("v1", response.getResponseData().get(2).getServiceVersion());
        assertEquals(ObjectType.SERVICE, response.getResponseData().get(2).getObjectType());

        assertEquals("FI", response.getResponseData().get(3).getXRoadInstance());
        assertEquals("PRI", response.getResponseData().get(3).getMemberClass());
        assertEquals("12345-9", response.getResponseData().get(3).getMemberCode());
        assertEquals(null, response.getResponseData().get(3).getSubsystemCode());
        assertEquals("testService3", response.getResponseData().get(3).getServiceCode());
        assertEquals(null, response.getResponseData().get(3).getServiceVersion());
        assertEquals(ObjectType.SERVICE, response.getResponseData().get(3).getObjectType());
    }

    /**
     * ListMethods: Member - Service (under member). Response: 4 services
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test3() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"MEMBER\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:serviceCode>listMethods</id:serviceCode></xrd:service><xrd:userId>EE1234567890</xrd:userId><xrd:id>ID-1234567890</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion><xrd:requestHash algorithmId=\"SHA-512\">ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==</xrd:requestHash></SOAP-ENV:Header><SOAP-ENV:Body><xrd:listMethodsResponse><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>PRI</id:memberClass><id:memberCode>12345-6</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>testService</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>PRI</id:memberClass><id:memberCode>12345-7</id:memberCode><id:subsystemCode>subsystem1</id:subsystemCode><id:serviceCode>testService1</id:serviceCode></xrd:service><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>PRI</id:memberClass><id:memberCode>12345-8</id:memberCode><id:serviceCode>testService2</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>PRI</id:memberClass><id:memberCode>12345-9</id:memberCode><id:serviceCode>testService3</id:serviceCode></xrd:service></xrd:listMethodsResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";

        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceResponseDeserializer deserializer = new ListServicesResponseDeserializer();
        ServiceResponse<String, List<ProducerMember>> response = deserializer.deserialize(msg);

        assertEquals("FI", response.getConsumer().getXRoadInstance());
        assertEquals("GOV", response.getConsumer().getMemberClass());
        assertEquals("MEMBER1", response.getConsumer().getMemberCode());
        assertEquals(null, response.getConsumer().getSubsystemCode());
        assertEquals(ObjectType.MEMBER, response.getConsumer().getObjectType());

        assertEquals("FI", response.getProducer().getXRoadInstance());
        assertEquals("COM", response.getProducer().getMemberClass());
        assertEquals("MEMBER2", response.getProducer().getMemberCode());
        assertEquals(null, response.getProducer().getSubsystemCode());
        assertEquals("listMethods", response.getProducer().getServiceCode());
        assertEquals(null, response.getProducer().getServiceVersion());
        assertEquals("ID-1234567890", response.getId());
        assertEquals("EE1234567890", response.getUserId());
        assertEquals("4.0", response.getProtocolVersion());
        assertEquals(ObjectType.SERVICE, response.getProducer().getObjectType());
        assertEquals(null, response.getRequestData());

        assertEquals("SHA-512", response.getRequestHashAlgorithm());
        assertEquals("ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==", response.getRequestHash());
        assertEquals(true, response.getSoapMessage() != null);

        assertEquals("FI", response.getResponseData().get(0).getXRoadInstance());
        assertEquals("PRI", response.getResponseData().get(0).getMemberClass());
        assertEquals("12345-6", response.getResponseData().get(0).getMemberCode());
        assertEquals("subsystem", response.getResponseData().get(0).getSubsystemCode());
        assertEquals("testService", response.getResponseData().get(0).getServiceCode());
        assertEquals("v1", response.getResponseData().get(0).getServiceVersion());
        assertEquals(ObjectType.SERVICE, response.getResponseData().get(0).getObjectType());

        assertEquals("FI", response.getResponseData().get(1).getXRoadInstance());
        assertEquals("PRI", response.getResponseData().get(1).getMemberClass());
        assertEquals("12345-7", response.getResponseData().get(1).getMemberCode());
        assertEquals("subsystem1", response.getResponseData().get(1).getSubsystemCode());
        assertEquals("testService1", response.getResponseData().get(1).getServiceCode());
        assertEquals(null, response.getResponseData().get(1).getServiceVersion());
        assertEquals(ObjectType.SERVICE, response.getResponseData().get(1).getObjectType());

        assertEquals("FI", response.getResponseData().get(2).getXRoadInstance());
        assertEquals("PRI", response.getResponseData().get(2).getMemberClass());
        assertEquals("12345-8", response.getResponseData().get(2).getMemberCode());
        assertEquals(null, response.getResponseData().get(2).getSubsystemCode());
        assertEquals("testService2", response.getResponseData().get(2).getServiceCode());
        assertEquals("v1", response.getResponseData().get(2).getServiceVersion());
        assertEquals(ObjectType.SERVICE, response.getResponseData().get(2).getObjectType());

        assertEquals("FI", response.getResponseData().get(3).getXRoadInstance());
        assertEquals("PRI", response.getResponseData().get(3).getMemberClass());
        assertEquals("12345-9", response.getResponseData().get(3).getMemberCode());
        assertEquals(null, response.getResponseData().get(3).getSubsystemCode());
        assertEquals("testService3", response.getResponseData().get(3).getServiceCode());
        assertEquals(null, response.getResponseData().get(3).getServiceVersion());
        assertEquals(ObjectType.SERVICE, response.getResponseData().get(3).getObjectType());
    }

    /**
     * AllowedMethods: Subsystem - Service (under subsystem). Response: 1
     * service
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test4() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>client</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>allowedMethods</id:serviceCode></xrd:service><xrd:userId>EE1234567890</xrd:userId><xrd:id>ID-1234567890</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion><xrd:requestHash algorithmId=\"SHA-512\">ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==</xrd:requestHash></SOAP-ENV:Header><SOAP-ENV:Body><xrd:allowedMethodsResponse><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>PRI</id:memberClass><id:memberCode>12345-6</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>testService</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service></xrd:allowedMethodsResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";

        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceResponseDeserializer deserializer = new ListServicesResponseDeserializer();
        ServiceResponse<String, List<ProducerMember>> response = deserializer.deserialize(msg);

        assertEquals("FI", response.getConsumer().getXRoadInstance());
        assertEquals("GOV", response.getConsumer().getMemberClass());
        assertEquals("MEMBER1", response.getConsumer().getMemberCode());
        assertEquals("client", response.getConsumer().getSubsystemCode());
        assertEquals(ObjectType.SUBSYSTEM, response.getConsumer().getObjectType());

        assertEquals("FI", response.getProducer().getXRoadInstance());
        assertEquals("COM", response.getProducer().getMemberClass());
        assertEquals("MEMBER2", response.getProducer().getMemberCode());
        assertEquals("subsystem", response.getProducer().getSubsystemCode());
        assertEquals("allowedMethods", response.getProducer().getServiceCode());
        assertEquals(null, response.getProducer().getServiceVersion());
        assertEquals("ID-1234567890", response.getId());
        assertEquals("EE1234567890", response.getUserId());
        assertEquals("4.0", response.getProtocolVersion());
        assertEquals(ObjectType.SERVICE, response.getProducer().getObjectType());
        assertEquals(null, response.getRequestData());

        assertEquals("SHA-512", response.getRequestHashAlgorithm());
        assertEquals("ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==", response.getRequestHash());
        assertEquals(true, response.getSoapMessage() != null);

        assertEquals("FI", response.getResponseData().get(0).getXRoadInstance());
        assertEquals("PRI", response.getResponseData().get(0).getMemberClass());
        assertEquals("12345-6", response.getResponseData().get(0).getMemberCode());
        assertEquals("subsystem", response.getResponseData().get(0).getSubsystemCode());
        assertEquals("testService", response.getResponseData().get(0).getServiceCode());
        assertEquals("v1", response.getResponseData().get(0).getServiceVersion());
        assertEquals(ObjectType.SERVICE, response.getResponseData().get(0).getObjectType());
    }

    /**
     * AllowedMethods: Member - Service (under subsystem). Response: 4 services
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test5() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"MEMBER\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>allowedMethods</id:serviceCode></xrd:service><xrd:userId>EE1234567890</xrd:userId><xrd:id>ID-1234567890</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion><xrd:requestHash algorithmId=\"SHA-512\">ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==</xrd:requestHash></SOAP-ENV:Header><SOAP-ENV:Body><xrd:allowedMethodsResponse><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>PRI</id:memberClass><id:memberCode>12345-6</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>testService</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>PRI</id:memberClass><id:memberCode>12345-7</id:memberCode><id:subsystemCode>subsystem1</id:subsystemCode><id:serviceCode>testService1</id:serviceCode></xrd:service><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>PRI</id:memberClass><id:memberCode>12345-8</id:memberCode><id:serviceCode>testService2</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>PRI</id:memberClass><id:memberCode>12345-9</id:memberCode><id:serviceCode>testService3</id:serviceCode></xrd:service></xrd:allowedMethodsResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";

        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceResponseDeserializer deserializer = new ListServicesResponseDeserializer();
        ServiceResponse<String, List<ProducerMember>> response = deserializer.deserialize(msg);

        assertEquals("FI", response.getConsumer().getXRoadInstance());
        assertEquals("GOV", response.getConsumer().getMemberClass());
        assertEquals("MEMBER1", response.getConsumer().getMemberCode());
        assertEquals(null, response.getConsumer().getSubsystemCode());
        assertEquals(ObjectType.MEMBER, response.getConsumer().getObjectType());

        assertEquals("FI", response.getProducer().getXRoadInstance());
        assertEquals("COM", response.getProducer().getMemberClass());
        assertEquals("MEMBER2", response.getProducer().getMemberCode());
        assertEquals("subsystem", response.getProducer().getSubsystemCode());
        assertEquals("allowedMethods", response.getProducer().getServiceCode());
        assertEquals(null, response.getProducer().getServiceVersion());
        assertEquals("ID-1234567890", response.getId());
        assertEquals("EE1234567890", response.getUserId());
        assertEquals("4.0", response.getProtocolVersion());
        assertEquals(ObjectType.SERVICE, response.getProducer().getObjectType());
        assertEquals(null, response.getRequestData());

        assertEquals("SHA-512", response.getRequestHashAlgorithm());
        assertEquals("ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==", response.getRequestHash());
        assertEquals(true, response.getSoapMessage() != null);

        assertEquals("FI", response.getResponseData().get(0).getXRoadInstance());
        assertEquals("PRI", response.getResponseData().get(0).getMemberClass());
        assertEquals("12345-6", response.getResponseData().get(0).getMemberCode());
        assertEquals("subsystem", response.getResponseData().get(0).getSubsystemCode());
        assertEquals("testService", response.getResponseData().get(0).getServiceCode());
        assertEquals("v1", response.getResponseData().get(0).getServiceVersion());
        assertEquals(ObjectType.SERVICE, response.getResponseData().get(0).getObjectType());

        assertEquals("FI", response.getResponseData().get(1).getXRoadInstance());
        assertEquals("PRI", response.getResponseData().get(1).getMemberClass());
        assertEquals("12345-7", response.getResponseData().get(1).getMemberCode());
        assertEquals("subsystem1", response.getResponseData().get(1).getSubsystemCode());
        assertEquals("testService1", response.getResponseData().get(1).getServiceCode());
        assertEquals(null, response.getResponseData().get(1).getServiceVersion());
        assertEquals(ObjectType.SERVICE, response.getResponseData().get(1).getObjectType());

        assertEquals("FI", response.getResponseData().get(2).getXRoadInstance());
        assertEquals("PRI", response.getResponseData().get(2).getMemberClass());
        assertEquals("12345-8", response.getResponseData().get(2).getMemberCode());
        assertEquals(null, response.getResponseData().get(2).getSubsystemCode());
        assertEquals("testService2", response.getResponseData().get(2).getServiceCode());
        assertEquals("v1", response.getResponseData().get(2).getServiceVersion());
        assertEquals(ObjectType.SERVICE, response.getResponseData().get(2).getObjectType());

        assertEquals("FI", response.getResponseData().get(3).getXRoadInstance());
        assertEquals("PRI", response.getResponseData().get(3).getMemberClass());
        assertEquals("12345-9", response.getResponseData().get(3).getMemberCode());
        assertEquals(null, response.getResponseData().get(3).getSubsystemCode());
        assertEquals("testService3", response.getResponseData().get(3).getServiceCode());
        assertEquals(null, response.getResponseData().get(3).getServiceVersion());
        assertEquals(ObjectType.SERVICE, response.getResponseData().get(3).getObjectType());
    }

    /**
     * AllowedMethods: Member - Service (under member). Response: 4 services
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test6() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"MEMBER\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:serviceCode>allowedMethods</id:serviceCode></xrd:service><xrd:userId>EE1234567890</xrd:userId><xrd:id>ID-1234567890</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion><xrd:requestHash algorithmId=\"SHA-512\">ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==</xrd:requestHash></SOAP-ENV:Header><SOAP-ENV:Body><xrd:allowedMethodsResponse><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>PRI</id:memberClass><id:memberCode>12345-6</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>testService</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>PRI</id:memberClass><id:memberCode>12345-7</id:memberCode><id:subsystemCode>subsystem1</id:subsystemCode><id:serviceCode>testService1</id:serviceCode></xrd:service><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>PRI</id:memberClass><id:memberCode>12345-8</id:memberCode><id:serviceCode>testService2</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>PRI</id:memberClass><id:memberCode>12345-9</id:memberCode><id:serviceCode>testService3</id:serviceCode></xrd:service></xrd:allowedMethodsResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";

        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceResponseDeserializer deserializer = new ListServicesResponseDeserializer();
        ServiceResponse<String, List<ProducerMember>> response = deserializer.deserialize(msg);

        assertEquals("FI", response.getConsumer().getXRoadInstance());
        assertEquals("GOV", response.getConsumer().getMemberClass());
        assertEquals("MEMBER1", response.getConsumer().getMemberCode());
        assertEquals(null, response.getConsumer().getSubsystemCode());
        assertEquals(ObjectType.MEMBER, response.getConsumer().getObjectType());

        assertEquals("FI", response.getProducer().getXRoadInstance());
        assertEquals("COM", response.getProducer().getMemberClass());
        assertEquals("MEMBER2", response.getProducer().getMemberCode());
        assertEquals(null, response.getProducer().getSubsystemCode());
        assertEquals("allowedMethods", response.getProducer().getServiceCode());
        assertEquals(null, response.getProducer().getServiceVersion());
        assertEquals("ID-1234567890", response.getId());
        assertEquals("EE1234567890", response.getUserId());
        assertEquals("4.0", response.getProtocolVersion());
        assertEquals(ObjectType.SERVICE, response.getProducer().getObjectType());
        assertEquals(null, response.getRequestData());

        assertEquals("SHA-512", response.getRequestHashAlgorithm());
        assertEquals("ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==", response.getRequestHash());
        assertEquals(true, response.getSoapMessage() != null);

        assertEquals("FI", response.getResponseData().get(0).getXRoadInstance());
        assertEquals("PRI", response.getResponseData().get(0).getMemberClass());
        assertEquals("12345-6", response.getResponseData().get(0).getMemberCode());
        assertEquals("subsystem", response.getResponseData().get(0).getSubsystemCode());
        assertEquals("testService", response.getResponseData().get(0).getServiceCode());
        assertEquals("v1", response.getResponseData().get(0).getServiceVersion());
        assertEquals(ObjectType.SERVICE, response.getResponseData().get(0).getObjectType());

        assertEquals("FI", response.getResponseData().get(1).getXRoadInstance());
        assertEquals("PRI", response.getResponseData().get(1).getMemberClass());
        assertEquals("12345-7", response.getResponseData().get(1).getMemberCode());
        assertEquals("subsystem1", response.getResponseData().get(1).getSubsystemCode());
        assertEquals("testService1", response.getResponseData().get(1).getServiceCode());
        assertEquals(null, response.getResponseData().get(1).getServiceVersion());
        assertEquals(ObjectType.SERVICE, response.getResponseData().get(1).getObjectType());

        assertEquals("FI", response.getResponseData().get(2).getXRoadInstance());
        assertEquals("PRI", response.getResponseData().get(2).getMemberClass());
        assertEquals("12345-8", response.getResponseData().get(2).getMemberCode());
        assertEquals(null, response.getResponseData().get(2).getSubsystemCode());
        assertEquals("testService2", response.getResponseData().get(2).getServiceCode());
        assertEquals("v1", response.getResponseData().get(2).getServiceVersion());
        assertEquals(ObjectType.SERVICE, response.getResponseData().get(2).getObjectType());

        assertEquals("FI", response.getResponseData().get(3).getXRoadInstance());
        assertEquals("PRI", response.getResponseData().get(3).getMemberClass());
        assertEquals("12345-9", response.getResponseData().get(3).getMemberCode());
        assertEquals(null, response.getResponseData().get(3).getSubsystemCode());
        assertEquals("testService3", response.getResponseData().get(3).getServiceCode());
        assertEquals(null, response.getResponseData().get(3).getServiceVersion());
        assertEquals(ObjectType.SERVICE, response.getResponseData().get(3).getObjectType());
    }

    /**
     * ListMethods: Subsystem - Service (under subsystem). Response: 0 service
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test7() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>client</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>listMethods</id:serviceCode></xrd:service><xrd:userId>EE1234567890</xrd:userId><xrd:id>ID-1234567890</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion><xrd:requestHash algorithmId=\"SHA-512\">ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==</xrd:requestHash></SOAP-ENV:Header><SOAP-ENV:Body><xrd:listMethodsResponse></xrd:listMethodsResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";

        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceResponseDeserializer deserializer = new ListServicesResponseDeserializer();
        ServiceResponse<String, List<ProducerMember>> response = deserializer.deserialize(msg);

        assertEquals("FI", response.getConsumer().getXRoadInstance());
        assertEquals("GOV", response.getConsumer().getMemberClass());
        assertEquals("MEMBER1", response.getConsumer().getMemberCode());
        assertEquals("client", response.getConsumer().getSubsystemCode());
        assertEquals(ObjectType.SUBSYSTEM, response.getConsumer().getObjectType());

        assertEquals("FI", response.getProducer().getXRoadInstance());
        assertEquals("COM", response.getProducer().getMemberClass());
        assertEquals("MEMBER2", response.getProducer().getMemberCode());
        assertEquals("subsystem", response.getProducer().getSubsystemCode());
        assertEquals("listMethods", response.getProducer().getServiceCode());
        assertEquals(null, response.getProducer().getServiceVersion());
        assertEquals("ID-1234567890", response.getId());
        assertEquals("EE1234567890", response.getUserId());
        assertEquals("4.0", response.getProtocolVersion());
        assertEquals(ObjectType.SERVICE, response.getProducer().getObjectType());
        assertEquals(null, response.getRequestData());

        assertEquals("SHA-512", response.getRequestHashAlgorithm());
        assertEquals("ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==", response.getRequestHash());
        assertEquals(true, response.getSoapMessage() != null);

        assertEquals(0, response.getResponseData().size());
    }

    /**
     * ListMethods: Subsystem - Service (under subsystem). Response: 0 service
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test8() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>client</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>listMethods</id:serviceCode></xrd:service><xrd:userId>EE1234567890</xrd:userId><xrd:id>ID-1234567890</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion><xrd:requestHash algorithmId=\"SHA-512\">ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==</xrd:requestHash></SOAP-ENV:Header><SOAP-ENV:Body><xrd:listMethodsResponse/></SOAP-ENV:Body></SOAP-ENV:Envelope>";

        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceResponseDeserializer deserializer = new ListServicesResponseDeserializer();
        ServiceResponse<String, List<ProducerMember>> response = deserializer.deserialize(msg);

        assertEquals("FI", response.getConsumer().getXRoadInstance());
        assertEquals("GOV", response.getConsumer().getMemberClass());
        assertEquals("MEMBER1", response.getConsumer().getMemberCode());
        assertEquals("client", response.getConsumer().getSubsystemCode());
        assertEquals(ObjectType.SUBSYSTEM, response.getConsumer().getObjectType());

        assertEquals("FI", response.getProducer().getXRoadInstance());
        assertEquals("COM", response.getProducer().getMemberClass());
        assertEquals("MEMBER2", response.getProducer().getMemberCode());
        assertEquals("subsystem", response.getProducer().getSubsystemCode());
        assertEquals("listMethods", response.getProducer().getServiceCode());
        assertEquals(null, response.getProducer().getServiceVersion());
        assertEquals("ID-1234567890", response.getId());
        assertEquals("EE1234567890", response.getUserId());
        assertEquals("4.0", response.getProtocolVersion());
        assertEquals(ObjectType.SERVICE, response.getProducer().getObjectType());
        assertEquals(null, response.getRequestData());

        assertEquals("SHA-512", response.getRequestHashAlgorithm());
        assertEquals("ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==", response.getRequestHash());
        assertEquals(true, response.getSoapMessage() != null);

        assertEquals(0, response.getResponseData().size());
    }

    /**
     * AllowedMethods: Subsystem - Service (under subsystem). Response: 0
     * service
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test9() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>client</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>allowedMethods</id:serviceCode></xrd:service><xrd:userId>EE1234567890</xrd:userId><xrd:id>ID-1234567890</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion><xrd:requestHash algorithmId=\"SHA-512\">ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==</xrd:requestHash></SOAP-ENV:Header><SOAP-ENV:Body><xrd:allowedMethodsResponse></xrd:allowedMethodsResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";

        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceResponseDeserializer deserializer = new ListServicesResponseDeserializer();
        ServiceResponse<String, List<ProducerMember>> response = deserializer.deserialize(msg);

        assertEquals("FI", response.getConsumer().getXRoadInstance());
        assertEquals("GOV", response.getConsumer().getMemberClass());
        assertEquals("MEMBER1", response.getConsumer().getMemberCode());
        assertEquals("client", response.getConsumer().getSubsystemCode());
        assertEquals(ObjectType.SUBSYSTEM, response.getConsumer().getObjectType());

        assertEquals("FI", response.getProducer().getXRoadInstance());
        assertEquals("COM", response.getProducer().getMemberClass());
        assertEquals("MEMBER2", response.getProducer().getMemberCode());
        assertEquals("subsystem", response.getProducer().getSubsystemCode());
        assertEquals("allowedMethods", response.getProducer().getServiceCode());
        assertEquals(null, response.getProducer().getServiceVersion());
        assertEquals("ID-1234567890", response.getId());
        assertEquals("EE1234567890", response.getUserId());
        assertEquals("4.0", response.getProtocolVersion());
        assertEquals(ObjectType.SERVICE, response.getProducer().getObjectType());
        assertEquals(null, response.getRequestData());

        assertEquals("SHA-512", response.getRequestHashAlgorithm());
        assertEquals("ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==", response.getRequestHash());
        assertEquals(true, response.getSoapMessage() != null);

        assertEquals(0, response.getResponseData().size());
    }

    /**
     * AllowedMethods: Subsystem - Service (under subsystem). Response: 0
     * service
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test10() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>client</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>allowedMethods</id:serviceCode></xrd:service><xrd:userId>EE1234567890</xrd:userId><xrd:id>ID-1234567890</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion><xrd:requestHash algorithmId=\"SHA-512\">ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==</xrd:requestHash></SOAP-ENV:Header><SOAP-ENV:Body><xrd:allowedMethodsResponse/></SOAP-ENV:Body></SOAP-ENV:Envelope>";

        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceResponseDeserializer deserializer = new ListServicesResponseDeserializer();
        ServiceResponse<String, List<ProducerMember>> response = deserializer.deserialize(msg);

        assertEquals("FI", response.getConsumer().getXRoadInstance());
        assertEquals("GOV", response.getConsumer().getMemberClass());
        assertEquals("MEMBER1", response.getConsumer().getMemberCode());
        assertEquals("client", response.getConsumer().getSubsystemCode());
        assertEquals(ObjectType.SUBSYSTEM, response.getConsumer().getObjectType());

        assertEquals("FI", response.getProducer().getXRoadInstance());
        assertEquals("COM", response.getProducer().getMemberClass());
        assertEquals("MEMBER2", response.getProducer().getMemberCode());
        assertEquals("subsystem", response.getProducer().getSubsystemCode());
        assertEquals("allowedMethods", response.getProducer().getServiceCode());
        assertEquals(null, response.getProducer().getServiceVersion());
        assertEquals("ID-1234567890", response.getId());
        assertEquals("EE1234567890", response.getUserId());
        assertEquals("4.0", response.getProtocolVersion());
        assertEquals(ObjectType.SERVICE, response.getProducer().getObjectType());
        assertEquals(null, response.getRequestData());

        assertEquals("SHA-512", response.getRequestHashAlgorithm());
        assertEquals("ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==", response.getRequestHash());
        assertEquals(true, response.getSoapMessage() != null);

        assertEquals(0, response.getResponseData().size());
    }

    /**
     * ListMethods: Subsystem - Service (under subsystem). Response: 1 service
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test11() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>client</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>listMethods</id:serviceCode></xrd:service><xrd:securityServer><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:serverCode>server1</id:serverCode></xrd:securityServer><xrd:userId>EE1234567890</xrd:userId><xrd:id>ID-1234567890</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion><xrd:requestHash algorithmId=\"SHA-512\">ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==</xrd:requestHash></SOAP-ENV:Header><SOAP-ENV:Body><xrd:listMethodsResponse><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>PRI</id:memberClass><id:memberCode>12345-6</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>testService</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service></xrd:listMethodsResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";

        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceResponseDeserializer deserializer = new ListServicesResponseDeserializer();
        ServiceResponse<String, List<ProducerMember>> response = deserializer.deserialize(msg);

        assertEquals("FI", response.getConsumer().getXRoadInstance());
        assertEquals("GOV", response.getConsumer().getMemberClass());
        assertEquals("MEMBER1", response.getConsumer().getMemberCode());
        assertEquals("client", response.getConsumer().getSubsystemCode());
        assertEquals(ObjectType.SUBSYSTEM, response.getConsumer().getObjectType());

        assertEquals("FI", response.getProducer().getXRoadInstance());
        assertEquals("COM", response.getProducer().getMemberClass());
        assertEquals("MEMBER2", response.getProducer().getMemberCode());
        assertEquals("subsystem", response.getProducer().getSubsystemCode());
        assertEquals("listMethods", response.getProducer().getServiceCode());
        assertEquals(null, response.getProducer().getServiceVersion());
        assertEquals("FI", response.getSecurityServer().getXRoadInstance());
        assertEquals("GOV", response.getSecurityServer().getMemberClass());
        assertEquals("MEMBER1", response.getSecurityServer().getMemberCode());
        assertEquals("server1", response.getSecurityServer().getServerCode());
        assertEquals("ID-1234567890", response.getId());
        assertEquals("EE1234567890", response.getUserId());
        assertEquals("4.0", response.getProtocolVersion());
        assertEquals(ObjectType.SERVICE, response.getProducer().getObjectType());
        assertEquals(null, response.getRequestData());

        assertEquals("SHA-512", response.getRequestHashAlgorithm());
        assertEquals("ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==", response.getRequestHash());
        assertEquals(true, response.getSoapMessage() != null);

        assertEquals("FI", response.getResponseData().get(0).getXRoadInstance());
        assertEquals("PRI", response.getResponseData().get(0).getMemberClass());
        assertEquals("12345-6", response.getResponseData().get(0).getMemberCode());
        assertEquals("subsystem", response.getResponseData().get(0).getSubsystemCode());
        assertEquals("testService", response.getResponseData().get(0).getServiceCode());
        assertEquals("v1", response.getResponseData().get(0).getServiceVersion());
        assertEquals(ObjectType.SERVICE, response.getResponseData().get(0).getObjectType());
    }

    /**
     * Technical error without header.
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void testTechError1() throws XRd4JException, SOAPException {
        String soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Body><SOAP-ENV:Fault><faultcode>Server.ClientProxy.LoggingFailed.TimestamperFailed</faultcode><faultstring>Cannot time-stamp messages</faultstring><faultactor></faultactor><detail><xrd:faultDetail>TimestamperFailed</xrd:faultDetail></detail></SOAP-ENV:Fault></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        SOAPMessage msg = SOAPHelper.toSOAP(soapString);

        ServiceResponseDeserializer deserializer = new ListServicesResponseDeserializer();
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
        assertEquals("Server.ClientProxy.LoggingFailed.TimestamperFailed", response.getErrorMessage().getFaultCode());
        assertEquals("Cannot time-stamp messages", response.getErrorMessage().getFaultString());
        assertEquals("", response.getErrorMessage().getFaultActor());
        assertEquals(true, response.getErrorMessage().getDetail() != null);
        assertEquals("TimestamperFailed", response.getErrorMessage().getDetail());
        assertEquals(ErrorMessageType.STANDARD_SOAP_ERROR_MESSAGE, response.getErrorMessage().getErrorMessageType());
        assertEquals(true, response.getSoapMessage() != null);
    }
}
