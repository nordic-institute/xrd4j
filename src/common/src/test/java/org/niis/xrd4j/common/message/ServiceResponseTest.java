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
package org.niis.xrd4j.common.message;

import org.niis.xrd4j.common.exception.XRd4JException;
import org.niis.xrd4j.common.member.ConsumerMember;
import org.niis.xrd4j.common.member.ProducerMember;
import org.niis.xrd4j.common.util.MessageHelper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test cases for ServiceResponse class.
 *
 * @author Petteri Kivimäki
 */
class ServiceResponseTest {

    private ConsumerMember consumer;
    private ProducerMember producer;

    /**
     * Set up instance variables used in test cases.
     *
     * @throws Exception
     */
    @BeforeEach
    void setUp() throws Exception {
        this.consumer = new ConsumerMember("FI", "COM", "12345-5", "system");
        this.producer = new ProducerMember("FI", "GOV", "12345-6", "system", "TestService", "v1");
    }

    /**
     * Test for toString method.
     *
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testToString() throws XRd4JException {
        String id = MessageHelper.generateId();
        ServiceResponse response = new ServiceResponse(consumer, producer, "12345");
        assertEquals("12345", response.toString());
        response = new ServiceResponse(consumer, producer, id);
        assertEquals(id, response.toString());
        assertFalse(response.toString().equals(id + "1"));
        assertEquals("4.0", response.getProtocolVersion());
        response.setProtocolVersion("5.0");
        assertEquals("5.0", response.getProtocolVersion());
    }

    /**
     * Test for equals method.
     *
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testEquals() throws XRd4JException {
        String id = MessageHelper.generateId();
        assertEquals(new ServiceResponse(consumer, producer, "12345"), new ServiceResponse(consumer, producer, "12345"));
        assertEquals(new ServiceResponse(consumer, producer, id), new ServiceResponse(consumer, producer, id));
        assertFalse(new ServiceResponse(consumer, producer, MessageHelper.generateId()).equals(new ServiceResponse(consumer, producer, MessageHelper.generateId())));
    }

    /**
     * Test for ServiceResponse constructor. Consumer is null.
     *
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException1() throws XRd4JException {
        try {
            ServiceResponse response = new ServiceResponse(null, producer, "12345");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ServiceResponse constructor. Producer is null.
     *
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException2() throws XRd4JException {
        try {
            ServiceResponse response = new ServiceResponse(consumer, null, "12345");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ServiceResponse constructor. Id is null.
     *
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException3() throws XRd4JException {
        try {
            ServiceResponse response = new ServiceResponse(consumer, producer, null);
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ServiceResponse constructor. Id is empty.
     *
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException4() throws XRd4JException {
        try {
            ServiceResponse response = new ServiceResponse(consumer, producer, "");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }
}
