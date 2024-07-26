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
package org.niis.xrd4j.client;

import org.niis.xrd4j.client.deserializer.AbstractResponseDeserializer;
import org.niis.xrd4j.client.deserializer.ServiceResponseDeserializer;
import org.niis.xrd4j.client.serializer.AbstractServiceRequestSerializer;
import org.niis.xrd4j.client.serializer.ServiceRequestSerializer;
import org.niis.xrd4j.common.member.ConsumerMember;
import org.niis.xrd4j.common.member.ProducerMember;
import org.niis.xrd4j.common.message.ServiceRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.xml.soap.Node;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test cases for SOAPClientImpl class. Test cases cover only cases where SOAP
 * server is not needed.
 *
 * @author Petteri Kivimäki
 */
class SOAPClientTest {

    private ServiceRequest<String> request;
    private ServiceRequestSerializer serializer;
    private ServiceResponseDeserializer deserializer;

    /**
     * Initializes instance variables for test cases.
     *
     * @throws Exception
     */
    @BeforeEach
    void setUp() throws Exception {
        ConsumerMember consumer = new ConsumerMember("FI", "GOV", "MEMBER1", "subsystem");
        ProducerMember producer = new ProducerMember("FI", "COM", "MEMBER2", "subsystem", "getRandom", "v1");
        producer.setNamespacePrefix("ns1");
        producer.setNamespaceUrl("http://consumer.x-road.ee");
        this.request = new ServiceRequest<String>(consumer, producer, "1234567890");
        this.request.setUserId("EE1234567890");
        this.request.setRequestData("1234567890");

        this.serializer = new TestRequestSerializer();
        this.deserializer = new TestResponseDeserializer();

        this.serializer.serialize(request);
    }

    /**
     * Invalid URL. No protocol definition.
     *
     * @throws SOAPException
     */
    @Test
    void testException1() throws SOAPException {
        try {
            SOAPClient client = new SOAPClientImpl();
            client.send(request.getSoapMessage(), "test.com");
            fail("Should not reach this");
        } catch (RuntimeException ex) {
            // OK
        }
    }

    /**
     * Invalid URL. Empty value.
     *
     * @throws SOAPException
     */
    @Test
    void testException2() throws SOAPException {
        try {
            SOAPClient client = new SOAPClientImpl();
            client.send(request.getSoapMessage(), "");
            fail("Should not reach this");
        } catch (RuntimeException ex) {
            // OK
        }
    }

    /**
     * Invalid URL. Null value.
     *
     * @throws RuntimeException
     * @throws SOAPException
     */
    @Test
    void testException3() throws RuntimeException, SOAPException {
        try {
            SOAPClient client = new SOAPClientImpl();
            client.send(request.getSoapMessage(), null);
            fail("Should not reach this");
        } catch (RuntimeException ex) {
            // OK
        }
    }

    /**
     * Invalid URL. Invalid protocol definition.
     *
     * @throws SOAPException
     */
    @Test
    void testException4() throws SOAPException {
        try {
            SOAPClient client = new SOAPClientImpl();
            client.send(request.getSoapMessage(), "htp://test.com");
            fail("Should not reach this");
        } catch (RuntimeException ex) {
            // OK
        }
    }

    /**
     * Sending message fails.
     *
     * @throws RuntimeException
     * @throws SOAPException
     */
    /* public void testException5() throws RuntimeException, SOAPException {
     try {
     SOAPClient client = new SOAPClientImpl();
     client.send(request.getSoapMessage(), "http://test.com");
     fail("Should not reach this");
     } catch (SOAPException ex) {
     // OK
     }
     }*/
    /**
     * Sending message fails.
     *
     * @throws RuntimeException
     * @throws SOAPException
     */
    /*public void testException6() throws RuntimeException, SOAPException {
     try {
     SOAPClient client = new SOAPClientImpl();
     client.send(request, "http://test.com", serializer, deserializer, false);
     fail("Should not reach this");
     } catch (SOAPException ex) {
     // OK
     }
     }*/
    /**
     * Try to send null request.
     *
     * @throws RuntimeException
     * @throws SOAPException
     */
    /* public void testException7() throws RuntimeException, SOAPException {
     try {
     SOAPClient client = new SOAPClientImpl();
     client.send(null, "http://test.com", serializer, deserializer, false);
     fail("Should not reach this");
     } catch (NullPointerException ex) {
     // OK
     }
     }*/
    /**
     * Try to send null serializer.
     *
     * @throws RuntimeException
     * @throws SOAPException
     */
    /*public void testException8() throws RuntimeException, SOAPException {
     try {
     SOAPClient client = new SOAPClientImpl();
     client.send(request, "http://test.com", null, deserializer, false);
     fail("Should not reach this");
     } catch (NullPointerException ex) {
     // OK
     }
     }*/
    private class TestRequestSerializer extends AbstractServiceRequestSerializer {

        protected void serializeRequest(ServiceRequest serviceRequest, SOAPElement soapRequest, SOAPEnvelope envelope) throws SOAPException {
            SOAPElement data = soapRequest.addChildElement(envelope.createName("data"));
            data.addTextNode((String) serviceRequest.getRequestData());
        }
    }

    private class TestResponseDeserializer extends AbstractResponseDeserializer<String, String> {

        protected String deserializeRequestData(Node requestNode) throws SOAPException {
            for (int i = 0; i < requestNode.getChildNodes().getLength(); i++) {
                if (requestNode.getChildNodes().item(i).getLocalName().equals("data")) {
                    return requestNode.getChildNodes().item(i).getTextContent();
                }
            }
            return null;
        }

        protected String deserializeResponseData(Node responseNode, SOAPMessage message) throws SOAPException {
            for (int i = 0; i < responseNode.getChildNodes().getLength(); i++) {
                if (responseNode.getChildNodes().item(i).getLocalName().equals("data")) {
                    return responseNode.getChildNodes().item(i).getTextContent();
                }
            }
            return null;
        }
    }
}
