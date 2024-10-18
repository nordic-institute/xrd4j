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
 * Test cases for ProducerMember class.
 *
 * @author Petteri Kivimäki
 */
class ProducerMemberTest {

    /**
     * Test for toString method.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testToString() throws XRd4JException {
        ProducerMember producer = new ProducerMember("FI", "TestService");
        assertEquals("FI.TestService", producer.toString());
        producer = new ProducerMember("FI", "COM", "12345-6", "TestService");
        assertEquals("FI.COM.12345-6.TestService", producer.toString());
        producer = new ProducerMember("FI_TEST", "GOV", "12345-6", "system", "TestService");
        assertEquals("FI_TEST.GOV.12345-6.system.TestService", producer.toString());
        producer = new ProducerMember("FI_TEST", "GOV", "12345-6", "system", "TestService", "v1");
        assertEquals("FI_TEST.GOV.12345-6.system.TestService.v1", producer.toString());
        assertFalse(producer.toString().equals("FI_TEST.GOV.12345-6.system.TestService.v2"));
        assertFalse(producer.toString().equals("FI_TEST.GOV.12345-6.system.TestService"));
        assertFalse(producer.toString().equals("Fi-TEST.GOV.12345-6.system.TestService"));
    }

    /**
     * Test for equals method.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testEquals() throws XRd4JException {
        assertEquals(new ProducerMember("FI", "TestService"), new ProducerMember("FI", "TestService"));
        assertEquals(new ProducerMember("FI", "COM", "12345-6", "TestService"), new ProducerMember("FI", "COM", "12345-6", "TestService"));
        assertEquals(new ProducerMember("FI_TEST", "GOV", "12345-6", "system", "TestService"), new ProducerMember("FI_TEST", "GOV", "12345-6", "system", "TestService"));
        assertEquals(new ProducerMember("FI_TEST", "GOV", "12345-6", "system", "TestService", "v1"), new ProducerMember("FI_TEST", "GOV", "12345-6", "system", "TestService", "v1"));
        assertFalse(new ProducerMember("FI", "FakeService").equals(new ProducerMember("FI", "TestService")));
        assertFalse(new ProducerMember("FI", "COM", "12345-6", "TestService").equals(new ProducerMember("FI", "COM", "12345-7", "TestService")));
        assertFalse(new ProducerMember("FI", "GOV", "12345-6", "TestService").equals(new ProducerMember("FI", "COM", "12345-6", "TestService")));
        assertFalse(new ProducerMember("FI_TEST", "GOV", "12345-6", "system", "TestService", "v1").equals(new ProducerMember("FI_TEST", "GOV", "12345-6", "system", "TestService", "v2")));
        assertFalse(new ProducerMember("FI_DEV", "GOV", "12345-6", "system", "TestService", "v1").equals(new ProducerMember("FI_TEST", "GOV", "12345-6", "system", "TestService", "v1")));
    }

    /**
     * Test for ProducerMember constructor (2 parameters). SDSBInstance is null.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException1() throws XRd4JException {
        try {
            new ProducerMember(null, "TestService");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ProducerMember constructor (2 parameters). Service code is null.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException2() throws XRd4JException {
        try {
            new ProducerMember("FI", null);
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ProducerMember constructor (2 parameters). SDSBInstance is empty.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException3() throws XRd4JException {
        try {
            new ProducerMember("", "TestService");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ProducerMember constructor (2 parameters). Service code is empty.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException4() throws XRd4JException {
        try {
            ProducerMember member = new ProducerMember("FI", "");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ProducerMember constructor (4 parameters). SDSBInstance is null.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException5() throws XRd4JException {
        try {
            ProducerMember member = new ProducerMember(null, "COM", "12345-6", "TestService");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ProducerMember constructor (4 parameters). MemberClass is null.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException6() throws XRd4JException {
        try {
            ProducerMember member = new ProducerMember("FI", null, "12345-6", "TestService");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ProducerMember constructor (4 parameters). Member code is null.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException7() throws XRd4JException {
        try {
            ProducerMember member = new ProducerMember("FI", "COM", null, "TestService");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ProducerMember constructor (4 parameters). Service code is null.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException8() throws XRd4JException {
        try {
            ProducerMember member = new ProducerMember("FI", "COM", "12345-6", null);
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ProducerMember constructor (4 parameters). SDSB instance is empty.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException9() throws XRd4JException {
        try {
            ProducerMember member = new ProducerMember("", "COM", "12345-6", "TestService");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ProducerMember constructor (4 parameters). Member class is empty.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException10() throws XRd4JException {
        try {
            ProducerMember member = new ProducerMember("FI", "", "12345-6", "TestService");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ProducerMember constructor (4 parameters). Member code is empty.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException11() throws XRd4JException {
        try {
            ProducerMember member = new ProducerMember("FI", "COM", "", "TestService");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ProducerMember constructor (4 parameters). Service code is empty.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException12() throws XRd4JException {
        try {
            ProducerMember member = new ProducerMember("FI", "COM", "12345-6", "");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ProducerMember constructor (5 parameters). Subsystem code is
     * null.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException13() throws XRd4JException {
        try {
            ProducerMember member = new ProducerMember("FI_TEST", "GOV", "12345-6", null, "TestService");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ProducerMember constructor (5 parameters). Subsystem code is
     * empty.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException14() throws XRd4JException {
        try {
            ProducerMember member = new ProducerMember("FI_TEST", "GOV", "12345-6", "", "TestService");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ProducerMember constructor (6 parameters). Service version is
     * null.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException15() throws XRd4JException {
        try {
            ProducerMember member = new ProducerMember("FI_TEST", "GOV", "12345-6", "system", "TestService", null);
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ProducerMember constructor (6 parameters). Service version is
     * empty.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException16() throws XRd4JException {
        try {
            ProducerMember member = new ProducerMember("FI_TEST", "GOV", "12345-6", "system", "TestService", "");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ProducerMember constructor (6 parameters). SDSBInstance is
     * null.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException17() throws XRd4JException {
        try {
            ProducerMember member = new ProducerMember(null, "GOV", "12345-6", "system", "TestService", "v1");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ProducerMember constructor (6 parameters). MemberClass is
     * null.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException18() throws XRd4JException {
        try {
            ProducerMember member = new ProducerMember("FI_TEST", null, "12345-6", "system", "TestService", "v1");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ProducerMember constructor (6 parameters). Member code is null.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException19() throws XRd4JException {
        try {
            ProducerMember member = new ProducerMember("FI_TEST", "GOV", null, "system", "TestService", "v1");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ProducerMember constructor (6 parameters). Subsystem code is
     * null.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException20() throws XRd4JException {
        try {
            ProducerMember member = new ProducerMember("FI_TEST", "GOV", "12345-6", null, "TestService", "v1");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ProducerMember constructor (6 parameters). Service code is
     * null.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException21() throws XRd4JException {
        try {
            ProducerMember member = new ProducerMember("FI_TEST", "GOV", "12345-6", "system", null, "v1");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ProducerMember constructor (6 parameters). SDSB instance is empty.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException22() throws XRd4JException {
        try {
            ProducerMember member = new ProducerMember("", "GOV", "12345-6", "system", "TestService", "v1");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ProducerMember constructor (6 parameters). Member class is empty.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException23() throws XRd4JException {
        try {
            ProducerMember member = new ProducerMember("FI_TEST", "", "12345-6", "system", "TestService", "v1");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ProducerMember constructor (6 parameters). Member code is empty.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException24() throws XRd4JException {
        try {
            ProducerMember member = new ProducerMember("FI_TEST", "GOV", "", "system", "TestService", "v1");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ProducerMember constructor (6 parameters). Subsystem code is
     * empty.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException25() throws XRd4JException {
        try {
            ProducerMember member = new ProducerMember("FI_TEST", "GOV", "12345-6", "", "TestService", "v1");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }

    /**
     * Test for ProducerMember constructor (6 parameters). Service code is
     * empty.
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testException26() throws XRd4JException {
        try {
            ProducerMember member = new ProducerMember("FI_TEST", "GOV", "12345-6", "system", "", "v1");
            fail("Should not reach this");
        } catch (XRd4JException ex) {
            // OK
        }
    }
}
