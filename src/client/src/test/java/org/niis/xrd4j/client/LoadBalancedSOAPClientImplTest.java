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

import junit.framework.TestCase;

import javax.xml.soap.SOAPException;

import java.util.ArrayList;
import java.util.List;

/**
 * Test cases for LoadBalancedSOAPClientImpl class. Test cases cover only cases
 * where SOAP server is not needed.
 *
 * @author Petteri Kivimäki
 */
public class LoadBalancedSOAPClientImplTest extends TestCase {

    /**
     * Test that reading target url throws exception when given empty url list
     * @throws SOAPException
     */
    public void test1() throws SOAPException {
        List<String> urls = new ArrayList<>();
        LoadBalancedSOAPClientImpl client = new LoadBalancedSOAPClientImpl(urls);
        try {
            // The list is empty so this should throw an exception
            assertEquals(client.getTargetUrl(), null);
            // If the exception is not thrown, the test has failed
            fail();
        } catch (IndexOutOfBoundsException e) {

        }
    }

    /**
     * Test that reading target url keeps giving the same url
     * @throws SOAPException
     */
    public void test2() throws SOAPException {
        List<String> urls = new ArrayList<>();
        urls.add("http://server1.myhost.com");
        LoadBalancedSOAPClientImpl client = new LoadBalancedSOAPClientImpl(urls);
        assertEquals("http://server1.myhost.com", client.getTargetUrl());
        assertEquals("http://server1.myhost.com", client.getTargetUrl());
        assertEquals("http://server1.myhost.com", client.getTargetUrl());
    }

    /**
     * Test that reading target url load balances between given urls
     * @throws SOAPException
     */
    public void test3() throws SOAPException {
        List<String> urls = new ArrayList<>();
        urls.add("http://server1.myhost.com");
        urls.add("http://server2.myhost.com");
        LoadBalancedSOAPClientImpl client = new LoadBalancedSOAPClientImpl(urls);
        assertEquals("http://server1.myhost.com", client.getTargetUrl());
        assertEquals("http://server2.myhost.com", client.getTargetUrl());
        assertEquals("http://server1.myhost.com", client.getTargetUrl());
        assertEquals("http://server2.myhost.com", client.getTargetUrl());
        assertEquals("http://server1.myhost.com", client.getTargetUrl());
    }

    /**
     * Test that reading target url load balances between given urls
     * @throws SOAPException
     */
    public void test4() throws SOAPException {
        List<String> urls = new ArrayList<>();
        urls.add("http://server1.myhost.com");
        urls.add("http://server2.myhost.com");
        urls.add("http://server3.myhost.com");
        LoadBalancedSOAPClientImpl client = new LoadBalancedSOAPClientImpl(urls);
        assertEquals("http://server1.myhost.com", client.getTargetUrl());
        assertEquals("http://server2.myhost.com", client.getTargetUrl());
        assertEquals("http://server3.myhost.com", client.getTargetUrl());
        assertEquals("http://server1.myhost.com", client.getTargetUrl());
    }
}
