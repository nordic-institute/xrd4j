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
import org.niis.xrd4j.common.member.ConsumerMember;
import org.niis.xrd4j.common.member.ObjectType;

import jakarta.xml.soap.SOAPException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test cases for ListClientsResponseDeserializer class.
 *
 * @author Petteri Kivimäki
 */
class ListClientsResponseDeserializerTest {

    /**
     * Client list with 2 members (MEMBER, SUBSYSTEM)
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test1() throws XRd4JException, SOAPException {
        String soapString = "<?xml version=\"1.0\"?><xrd:clientList xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\" xmlns:id=\"http://x-road.eu/xsd/identifiers\"><xrd:member><xrd:id id:objectType=\"MEMBER\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>12345</id:memberCode></xrd:id><xrd:name>Test org</xrd:name></xrd:member><xrd:member><xrd:id id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI-DEV</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>54321</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode></xrd:id><xrd:name>Another org</xrd:name></xrd:member></xrd:clientList>";
        List<ConsumerMember> list = new ListClientsResponseDeserializer().deserializeConsumerList(soapString);

        assertEquals("FI", list.get(0).getXRoadInstance());
        assertEquals("GOV", list.get(0).getMemberClass());
        assertEquals("12345", list.get(0).getMemberCode());
        assertEquals(null, list.get(0).getSubsystemCode());
        assertEquals(ObjectType.MEMBER, list.get(0).getObjectType());

        assertEquals("FI-DEV", list.get(1).getXRoadInstance());
        assertEquals("COM", list.get(1).getMemberClass());
        assertEquals("54321", list.get(1).getMemberCode());
        assertEquals("subsystem", list.get(1).getSubsystemCode());
        assertEquals(ObjectType.SUBSYSTEM, list.get(1).getObjectType());
    }

    /**
     * Client list with 3 members (MEMBER, 2 * SUBSYSTEM)
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test2() throws XRd4JException, SOAPException {
        String soapString = "<?xml version=\"1.0\"?><xrd:clientList xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\" xmlns:id=\"http://x-road.eu/xsd/identifiers\"><xrd:member><xrd:id id:objectType=\"MEMBER\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>12345</id:memberCode></xrd:id><xrd:name>Org 1</xrd:name></xrd:member><xrd:member><xrd:id id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>54321</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode></xrd:id><xrd:name>Org 2</xrd:name></xrd:member><xrd:member><xrd:id id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>PRI</id:memberClass><id:memberCode>00000-1</id:memberCode><id:subsystemCode>testSystem</id:subsystemCode></xrd:id><xrd:name>Org 3</xrd:name></xrd:member></xrd:clientList>";
        List<ConsumerMember> list = new ListClientsResponseDeserializer().deserializeConsumerList(soapString);

        assertEquals("FI", list.get(0).getXRoadInstance());
        assertEquals("GOV", list.get(0).getMemberClass());
        assertEquals("12345", list.get(0).getMemberCode());
        assertEquals(null, list.get(0).getSubsystemCode());
        assertEquals(ObjectType.MEMBER, list.get(0).getObjectType());

        assertEquals("FI", list.get(1).getXRoadInstance());
        assertEquals("COM", list.get(1).getMemberClass());
        assertEquals("54321", list.get(1).getMemberCode());
        assertEquals("subsystem", list.get(1).getSubsystemCode());
        assertEquals(ObjectType.SUBSYSTEM, list.get(1).getObjectType());

        assertEquals("FI", list.get(2).getXRoadInstance());
        assertEquals("PRI", list.get(2).getMemberClass());
        assertEquals("00000-1", list.get(2).getMemberCode());
        assertEquals("testSystem", list.get(2).getSubsystemCode());
        assertEquals(ObjectType.SUBSYSTEM, list.get(2).getObjectType());
    }

    /**
     * Client list with 4 members (2 * MEMBER, 2 * SUBSYSTEM)
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test3() throws XRd4JException, SOAPException {
        String soapString = "<?xml version=\"1.0\"?><ns2:clientList xmlns:ns1=\"http://x-road.eu/xsd/identifiers\" xmlns:ns2=\"http://x-road.eu/xsd/xroad.xsd\"><ns2:member><ns2:id ns1:objectType=\"MEMBER\"><ns1:xRoadInstance>FI-DEV63</ns1:xRoadInstance><ns1:memberClass>GOV</ns1:memberClass><ns1:memberCode>7654321-0</ns1:memberCode></ns2:id><ns2:name>Org 1</ns2:name></ns2:member><ns2:member><ns2:id ns1:objectType=\"MEMBER\"><ns1:xRoadInstance>FI-DEV63</ns1:xRoadInstance><ns1:memberClass>GOV</ns1:memberClass><ns1:memberCode>1234567-8</ns1:memberCode></ns2:id><ns2:name>Org 2</ns2:name></ns2:member><ns2:member><ns2:id ns1:objectType=\"SUBSYSTEM\"><ns1:xRoadInstance>FI-DEV63</ns1:xRoadInstance><ns1:memberClass>GOV</ns1:memberClass><ns1:memberCode>1234567-8</ns1:memberCode><ns1:subsystemCode>TestClient</ns1:subsystemCode></ns2:id><ns2:name>Org 2</ns2:name></ns2:member><ns2:member><ns2:id ns1:objectType=\"SUBSYSTEM\"><ns1:xRoadInstance>FI-DEV63</ns1:xRoadInstance><ns1:memberClass>GOV</ns1:memberClass><ns1:memberCode>1234567-8</ns1:memberCode><ns1:subsystemCode>TestService</ns1:subsystemCode></ns2:id><ns2:name>Org 2</ns2:name></ns2:member></ns2:clientList>";
        List<ConsumerMember> list = new ListClientsResponseDeserializer().deserializeConsumerList(soapString);

        assertEquals("FI-DEV63", list.get(0).getXRoadInstance());
        assertEquals("GOV", list.get(0).getMemberClass());
        assertEquals("7654321-0", list.get(0).getMemberCode());
        assertEquals(null, list.get(0).getSubsystemCode());
        assertEquals(ObjectType.MEMBER, list.get(0).getObjectType());

        assertEquals("FI-DEV63", list.get(1).getXRoadInstance());
        assertEquals("GOV", list.get(1).getMemberClass());
        assertEquals("1234567-8", list.get(1).getMemberCode());
        assertEquals(null, list.get(1).getSubsystemCode());
        assertEquals(ObjectType.MEMBER, list.get(1).getObjectType());

        assertEquals("FI-DEV63", list.get(2).getXRoadInstance());
        assertEquals("GOV", list.get(2).getMemberClass());
        assertEquals("1234567-8", list.get(2).getMemberCode());
        assertEquals("TestClient", list.get(2).getSubsystemCode());
        assertEquals(ObjectType.SUBSYSTEM, list.get(2).getObjectType());

        assertEquals("FI-DEV63", list.get(3).getXRoadInstance());
        assertEquals("GOV", list.get(3).getMemberClass());
        assertEquals("1234567-8", list.get(3).getMemberCode());
        assertEquals("TestService", list.get(3).getSubsystemCode());
        assertEquals(ObjectType.SUBSYSTEM, list.get(3).getObjectType());
    }

    /**
     * Client list with no members
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test4() throws XRd4JException, SOAPException {
        String soapString = "<?xml version=\"1.0\"?><xrd:clientList xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\" xmlns:id=\"http://x-road.eu/xsd/identifiers\"></xrd:clientList>";
        List<ConsumerMember> list = new ListClientsResponseDeserializer().deserializeConsumerList(soapString);

        assertEquals(0, list.size());
    }

    /**
     * Client list with no members
     *
     * @throws XRd4JException
     * @throws SOAPException
     */
    @Test
    void test5() throws XRd4JException, SOAPException {
        String soapString = "<?xml version=\"1.0\"?><xrd:clientList xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\" xmlns:id=\"http://x-road.eu/xsd/identifiers\"/>";
        List<ConsumerMember> list = new ListClientsResponseDeserializer().deserializeConsumerList(soapString);

        assertEquals(0, list.size());
    }
}
