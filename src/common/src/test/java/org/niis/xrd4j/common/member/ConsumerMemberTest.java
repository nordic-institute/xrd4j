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
package org.niis.xrd4j.common.member;

import org.niis.xrd4j.common.exception.XRd4JException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test cases for ConsumerMember class.
 *
 * @author Petteri Kivimäki
 */
class ConsumerMemberTest {

    /**
     * Test for toString method.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testToString() throws XRd4JException {
        ConsumerMember consumer = new ConsumerMember("FI", "COM", "12345-6");
        assertEquals("FI.COM.12345-6", consumer.toString());
        consumer = new ConsumerMember("FI", "COM", "12345-6", "system");
        assertEquals("FI.COM.12345-6.system", consumer.toString());
        consumer = new ConsumerMember("FI_TEST", "GOV", "12345-6", "system");
        assertEquals("FI_TEST.GOV.12345-6.system", consumer.toString());
        consumer = new ConsumerMember("FI_DEV", "GOV", "12345-6", "system");
        assertEquals("FI_DEV.GOV.12345-6.system", consumer.toString());
        assertFalse(consumer.toString().equals("Fi.COM.12345-6.system"));
        assertFalse(consumer.toString().equals("FI.cOm.12345-6.system"));
    }

    /**
     * Test for equals method.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testEquals() throws XRd4JException {
        assertEquals(new ConsumerMember("FI", "COM", "12345-6"), new ConsumerMember("FI", "COM", "12345-6"));
        assertEquals(new ConsumerMember("FI_DEV", "GOV", "12345-6", "system"), new ConsumerMember("FI_DEV", "GOV", "12345-6", "system"));
        assertFalse(new ConsumerMember("FI", "COM", "12345-6").equals(new ConsumerMember("FI", "COM", "12345-67")));
        assertFalse(new ConsumerMember("FI_DEV", "COM", "12345-6").equals(new ConsumerMember("FI", "COM", "12345-6")));
        assertFalse(new ConsumerMember("FI", "COM", "12345-6").equals(new ConsumerMember("FI", "COM", "12345-67", "system")));
    }

    /**
     * Test for ConsumerMember constructor (3 parameters). MemberClass is null.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException1() throws XRd4JException {
        try {
            ConsumerMember member = new ConsumerMember("FI", null, "12345-6");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ConsumerMember constructor (3 parameters). SDSBInstance is null.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException2() throws XRd4JException {
        try {
            ConsumerMember member = new ConsumerMember(null, "COM", "12345-6");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ConsumerMember constructor (3 parameters). Id is null.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException3() throws XRd4JException {
        try {
            ConsumerMember member = new ConsumerMember("FI", "COM", null);
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ConsumerMember constructor (3 parameters). SDSBInstance is empty.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException4() throws XRd4JException {
        try {
            ConsumerMember member = new ConsumerMember("", "COM", "12345-6");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ConsumerMember constructor (3 parameters). Member class is empty.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException5() throws XRd4JException {
        try {
            ConsumerMember member = new ConsumerMember("FI", "", "12345-6");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ConsumerMember constructor (3 parameters). Id is empty.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException6() throws XRd4JException {
        try {
            ConsumerMember member = new ConsumerMember("FI", "COM", "");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ConsumerMember constructor (4 parameters). SDSBInstance is null.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException7() throws XRd4JException {
        try {
            ConsumerMember member = new ConsumerMember(null, "COM", "12345-6", "system");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ConsumerMember constructor (4 parameters). MemberClass is null.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException8() throws XRd4JException {
        try {
            ConsumerMember member = new ConsumerMember("FI", null, "12345-6", "system");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ConsumerMember constructor (4 parameters). Id is null.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException9() throws XRd4JException {
        try {
            ConsumerMember member = new ConsumerMember("FI", "COM", null, "system");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ConsumerMember constructor (4 parameters). Member code is null.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException10() throws XRd4JException {
        try {
            ConsumerMember member = new ConsumerMember("FI", "COM", "12345-6", null);
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ConsumerMember constructor (4 parameters). SDSBInstance is empty.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException11() throws XRd4JException {
        try {
            ConsumerMember member = new ConsumerMember("", "COM", "12345-6", "system");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ConsumerMember constructor (4 parameters). Member class is empty.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException12() throws XRd4JException {
        try {
            ConsumerMember member = new ConsumerMember("FI", "", "", "system");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ConsumerMember constructor (4 parameters). Id is empty.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException13() throws XRd4JException {
        try {
            ConsumerMember member = new ConsumerMember("FI", "COM", "", "system");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ConsumerMember constructor (4 parameters). Member code is.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException14() throws XRd4JException {
        try {
            ConsumerMember member = new ConsumerMember("FI", "COM", "12345-6", "");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }
}
