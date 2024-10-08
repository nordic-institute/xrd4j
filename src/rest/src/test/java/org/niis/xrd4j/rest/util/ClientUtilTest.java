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
package org.niis.xrd4j.rest.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test cases for JSONToXMLConverter class.
 *
 * @author Petteri Kivimäki
 */
class ClientUtilTest {

    /**
     * No parameters, no resource id, no slash in base URI, no question mark in
     * base uri.
     */
    @Test
    void testBuildClientURL1() {
        String baseURL = "http://api.test.com";
        String correctURL = baseURL;
        Map<String, String> params = new TreeMap<String, String>();
        String resultURL = ClientUtil.buildTargetURL(baseURL, params);
        assertEquals(correctURL, resultURL);
    }

    /**
     * No parameters (null), no resource id, no slash in base URI, no question
     * mark in base uri.
     */
    @Test
    void testBuildClientURL2() {
        String baseURL = "http://api.test.com";
        String correctURL = baseURL;
        Map<String, String> params = null;
        String resultURL = ClientUtil.buildTargetURL(baseURL, params);
        assertEquals(correctURL, resultURL);
    }

    /**
     * Three parameters, no resource id, no slash in base URI, no question mark
     * in base uri.
     */
    @Test
    void testBuildClientURL3() {
        String baseURL = "http://api.test.com";
        String correctURL = baseURL + "?key1=value1&key2=value2&key3=value3";
        Map<String, String> params = new TreeMap<String, String>();
        params.put("key1", "\nvalue1    ");
        params.put("key2", "value2\r");
        params.put("key3", "\r\nvalue3\r\n");
        String resultURL = ClientUtil.buildTargetURL(baseURL, params);
        assertEquals(correctURL, resultURL);
    }

    /**
     * Three parameters, no resource id, slash in base URI, no question mark in
     * base uri.
     */
    @Test
    void testBuildClientURL4() {
        String baseURL = "http://api.test.com/";
        String correctURL = baseURL + "?key1=value1&key2=value2&key3=value3";
        Map<String, String> params = new TreeMap<String, String>();
        params.put("key1", "value1");
        params.put("key2", "value2");
        params.put("key3", "value3");
        String resultURL = ClientUtil.buildTargetURL(baseURL, params);
        assertEquals(correctURL, resultURL);
    }

    /**
     * Three parameters, no resource id, no slash in base URI, question mark in
     * base uri.
     */
    @Test
    void testBuildClientURL5() {
        String baseURL = "http://api.test.com?";
        String correctURL = baseURL + "key1=value1&key2=value2&key3=value3";
        Map<String, String> params = new TreeMap<String, String>();
        params.put("key1", "    value1");
        params.put("key2", "value2");
        params.put("key3", "    value3  ");
        String resultURL = ClientUtil.buildTargetURL(baseURL, params);
        assertEquals(correctURL, resultURL);
    }

    /**
     * Three parameters, no resource id, slash in base URI, question mark in
     * base uri.
     */
    @Test
    void testBuildClientURL6() {
        String baseURL = "http://api.test.com/?";
        String correctURL = baseURL + "key1=value1&key2=value2&key3=value3";
        Map<String, String> params = new TreeMap<String, String>();
        params.put("key1", "value1");
        params.put("key2", "value2");
        params.put("key3", "value3");
        String resultURL = ClientUtil.buildTargetURL(baseURL, params);
        assertEquals(correctURL, resultURL);
    }

    /**
     * No parameters, resource id, no slash in base URI.
     */
    @Test
    void testBuildClientURL7() {
        String baseURL = "http://api.test.com";
        String correctURL = baseURL + "/10";
        Map<String, String> params = new TreeMap<String, String>();
        params.put("resourceId", " \n10\n");
        String resultURL = ClientUtil.buildTargetURL(baseURL, params);
        assertEquals(correctURL, resultURL);
    }

    /**
     * No parameters, resource id, slash in base URI.
     */
    @Test
    void testBuildClientURL8() {
        String baseURL = "http://api.test.com/";
        String correctURL = baseURL + "10";
        Map<String, String> params = new TreeMap<String, String>();
        params.put("resourceId", "\r10\r    ");
        String resultURL = ClientUtil.buildTargetURL(baseURL, params);
        assertEquals(correctURL, resultURL);
    }

    /**
     * Two parameters, resource id, no slash in base URI.
     */
    @Test
    void testBuildClientURL9() {
        String baseURL = "http://api.test.com";
        String correctURL = baseURL + "/10?key2=value2&key3=value3";
        Map<String, String> params = new TreeMap<String, String>();
        params.put("resourceId", "\r\n10\r\n");
        params.put("key2", " value2 ");
        params.put("key3", "value3");
        String resultURL = ClientUtil.buildTargetURL(baseURL, params);
        assertEquals(correctURL, resultURL);
    }

    /**
     * Two parameters, resource id, slash in base URI.
     */
    @Test
    void testBuildClientURL10() {
        String baseURL = "http://api.test.com/";
        String correctURL = baseURL + "10?key2=value2&key3=value3";
        Map<String, String> params = new TreeMap<String, String>();
        params.put("resourceId", "  10 ");
        params.put("key2", "value2");
        params.put("key3", "value3");
        String resultURL = ClientUtil.buildTargetURL(baseURL, params);
        assertEquals(correctURL, resultURL);
    }

    /**
     * One parameter in base URI, two parameters, no resource id, no slash in
     * base URI.
     */
    @Test
    void testBuildClientURL11() {
        String baseURL = "http://api.test.com?param=1";
        String correctURL = baseURL + "&key2=value2&key3=value3";
        Map<String, String> params = new TreeMap<String, String>();
        params.put("key2", "  value2");
        params.put("key3", "value3  ");
        String resultURL = ClientUtil.buildTargetURL(baseURL, params);
        assertEquals(correctURL, resultURL);
    }

    /**
     * One parameter in base URI, two parameters, no resource id, slash in base
     * URI.
     */
    @Test
    void testBuildClientURL12() {
        String baseURL = "http://api.test.com/?param=1";
        String correctURL = baseURL + "&key2=value2&key3=value3";
        Map<String, String> params = new TreeMap<String, String>();
        params.put("key2", "value2");
        params.put("key3", "value3");
        String resultURL = ClientUtil.buildTargetURL(baseURL, params);
        assertEquals(correctURL, resultURL);
    }

    /**
     * No parameters, no resource id, no slash in base URI, no question mark in
     * base uri.
     */
    @Test
    void testBuildClientURL13() {
        String baseURL = "http://api.test.com";
        String correctURL = baseURL;
        Map<String, List<String>> params = new TreeMap<>();
        String resultURL = ClientUtil.buildTargetURL(baseURL, params);
        assertEquals(correctURL, resultURL);
    }

    /**
     * No parameters (null), no resource id, no slash in base URI, no question
     * mark in base uri.
     */
    @Test
    void testBuildClientURL14() {
        String baseURL = "http://api.test.com";
        String correctURL = baseURL;
        Map<String, List<String>> params = null;
        String resultURL = ClientUtil.buildTargetURL(baseURL, params);
        assertEquals(correctURL, resultURL);
    }

    /**
     * Three parameters, no resource id, no slash in base URI, no question mark
     * in base uri.
     */
    @Test
    void testBuildClientURL15() {
        String baseURL = "http://api.test.com";
        String correctURL = baseURL + "?key1=value1&key2=value2&key3=value3";
        Map<String, List<String>> params = new TreeMap<>();
        List<String> list = new ArrayList<>();
        list.add("\nvalue1    ");
        params.put("key1", list);
        list = new ArrayList<>();
        list.add("value2\r");
        params.put("key2", list);
        list = new ArrayList<>();
        list.add("\r\nvalue3\r\n");
        params.put("key3", list);
        String resultURL = ClientUtil.buildTargetURL(baseURL, params);
        assertEquals(correctURL, resultURL);
    }

    /**
     * Three parameters, no resource id, slash in base URI, no question mark in
     * base uri.
     */
    @Test
    void testBuildClientURL16() {
        String baseURL = "http://api.test.com/";
        String correctURL = baseURL + "?key1=value1&key2=value2&key3=value3";
        Map<String, List<String>> params = new TreeMap<>();
        List<String> list = new ArrayList<>();
        list.add("value1");
        params.put("key1", list);
        list = new ArrayList<>();
        list.add("value2");
        params.put("key2", list);
        list = new ArrayList<>();
        list.add("value3");
        params.put("key3", list);
        String resultURL = ClientUtil.buildTargetURL(baseURL, params);
        assertEquals(correctURL, resultURL);
    }

    /**
     * Three parameters, no resource id, no slash in base URI, question mark in
     * base uri.
     */
    @Test
    void testBuildClientURL17() {
        String baseURL = "http://api.test.com?";
        String correctURL = baseURL + "key1=value1&key2=value2&key3=value3";
        Map<String, List<String>> params = new TreeMap<>();
        List<String> list = new ArrayList<>();
        list.add("    value1");
        params.put("key1", list);
        list = new ArrayList<>();
        list.add("value2");
        params.put("key2", list);
        list = new ArrayList<>();
        list.add("    value3  ");
        params.put("key3", list);
        String resultURL = ClientUtil.buildTargetURL(baseURL, params);
        assertEquals(correctURL, resultURL);
    }

    /**
     * Three parameters, no resource id, slash in base URI, question mark in
     * base uri.
     */
    @Test
    void testBuildClientURL18() {
        String baseURL = "http://api.test.com/?";
        String correctURL = baseURL + "key1=value1&key2=value2&key3=value3";
        Map<String, List<String>> params = new TreeMap<>();
        List<String> list = new ArrayList<>();
        list.add("value1");
        params.put("key1", list);
        list = new ArrayList<>();
        list.add("value2");
        params.put("key2", list);
        list = new ArrayList<>();
        list.add("value3");
        params.put("key3", list);
        String resultURL = ClientUtil.buildTargetURL(baseURL, params);
        assertEquals(correctURL, resultURL);
    }

    /**
     * No parameters, resource id, no slash in base URI.
     */
    @Test
    void testBuildClientURL19() {
        String baseURL = "http://api.test.com";
        String correctURL = baseURL + "/10";
        Map<String, List<String>> params = new TreeMap<>();
        List<String> list = new ArrayList<>();
        list.add(" \n10\n");
        params.put("resourceId", list);
        String resultURL = ClientUtil.buildTargetURL(baseURL, params);
        assertEquals(correctURL, resultURL);
    }

    /**
     * No parameters, resource id, slash in base URI.
     */
    @Test
    void testBuildClientURL20() {
        String baseURL = "http://api.test.com/";
        String correctURL = baseURL + "10";
        Map<String, List<String>> params = new TreeMap<>();
        List<String> list = new ArrayList<>();
        list.add("\r10\r    ");
        params.put("resourceId", list);
        String resultURL = ClientUtil.buildTargetURL(baseURL, params);
        assertEquals(correctURL, resultURL);
    }

    /**
     * Two parameters, resource id, no slash in base URI.
     */
    @Test
    void testBuildClientURL21() {
        String baseURL = "http://api.test.com";
        String correctURL = baseURL + "/10?key2=value2&key3=value3";
        Map<String, List<String>> params = new TreeMap<>();
        List<String> list = new ArrayList<>();
        list.add("\r\n10\r\n");
        params.put("resourceId", list);
        list = new ArrayList<>();
        list.add(" value2 ");
        params.put("key2", list);
        list = new ArrayList<>();
        list.add("value3");
        params.put("key3", list);
        String resultURL = ClientUtil.buildTargetURL(baseURL, params);
        assertEquals(correctURL, resultURL);
    }

    /**
     * Two parameters, resource id, slash in base URI.
     */
    @Test
    void testBuildClientURL22() {
        String baseURL = "http://api.test.com/";
        String correctURL = baseURL + "10?key2=value2&key3=value3";
        Map<String, List<String>> params = new TreeMap<>();
        List<String> list = new ArrayList<>();
        list.add("  10 ");
        params.put("resourceId", list);
        list = new ArrayList<>();
        list.add("value2");
        params.put("key2", list);
        list = new ArrayList<>();
        list.add("value3");
        params.put("key3", list);
        String resultURL = ClientUtil.buildTargetURL(baseURL, params);
        assertEquals(correctURL, resultURL);
    }

    /**
     * One parameter in base URI, two parameters, no resource id, no slash in
     * base URI.
     */
    @Test
    void testBuildClientURL23() {
        String baseURL = "http://api.test.com?param=1";
        String correctURL = baseURL + "&key2=value2&key3=value3";
        Map<String, List<String>> params = new TreeMap<>();
        List<String> list = new ArrayList<>();
        list.add("  value2");
        params.put("key2", list);
        list = new ArrayList<>();
        list.add("value3  ");
        params.put("key3", list);
        String resultURL = ClientUtil.buildTargetURL(baseURL, params);
        assertEquals(correctURL, resultURL);
    }

    /**
     * One parameter in base URI, two parameters, no resource id, slash in base
     * URI.
     */
    @Test
    void testBuildClientURL24() {
        String baseURL = "http://api.test.com/?param=1";
        String correctURL = baseURL + "&key2=value2&key3=value3";
        Map<String, List<String>> params = new TreeMap<>();
        List<String> list = new ArrayList<>();
        list.add("value2");
        params.put("key2", list);
        list = new ArrayList<>();
        list.add("value3");
        params.put("key3", list);
        String resultURL = ClientUtil.buildTargetURL(baseURL, params);
        assertEquals(correctURL, resultURL);
    }

    /**
     * Two parameters with the same name, no resource id, slash in base URI.
     */
    @Test
    void testBuildClientURL25() {
        String baseURL = "http://api.test.com/";
        String correctURL = baseURL + "?key=value1&key=value2&key2=value3";
        Map<String, List<String>> params = new TreeMap<>();
        List<String> list = new ArrayList<>();
        list.add("value1");
        list.add("value2");
        params.put("key", list);
        List<String> list2 = new ArrayList<>();
        list2.add("value3");
        params.put("key2", list2);
        String resultURL = ClientUtil.buildTargetURL(baseURL, params);
        assertEquals(correctURL, resultURL);
    }

    /**
     * Two parameters with the same name, resource id, slash in base URI.
     */
    @Test
    void testBuildClientURL26() {
        String baseURL = "http://api.test.com/";
        String correctURL = baseURL + "10?key=value1&key=value2";
        Map<String, List<String>> params = new TreeMap<>();
        List<String> list = new ArrayList<>();
        list.add("  10 ");
        params.put("resourceId", list);
        list = new ArrayList<>();
        list.add("value1");
        list.add("value2");
        params.put("key", list);
        String resultURL = ClientUtil.buildTargetURL(baseURL, params);
        assertEquals(correctURL, resultURL);
    }

    /**
     * Two parameters with the same name, no resource id, no slash in base URI.
     */
    @Test
    void testBuildClientURL27() {
        String baseURL = "http://api.test.com";
        String correctURL = baseURL + "?key=value1&key=value2";
        Map<String, List<String>> params = new TreeMap<>();
        List<String> list = new ArrayList<>();
        list.add("value1");
        list.add("value2");
        params.put("key", list);
        String resultURL = ClientUtil.buildTargetURL(baseURL, params);
        assertEquals(correctURL, resultURL);
    }

    /**
     * Two parameters with the same name, resource id, no slash in base URI.
     */
    @Test
    void testBuildClientURL28() {
        String baseURL = "http://api.test.com";
        String correctURL = baseURL + "/10?key=value1&key=value2&key=value3&key=value4";
        Map<String, List<String>> params = new TreeMap<>();
        List<String> list = new ArrayList<>();
        list.add("  10 ");
        params.put("resourceId", list);
        list = new ArrayList<>();
        list.add("value1");
        list.add("value2");
        list.add("value3");
        list.add("value4");
        params.put("key", list);
        String resultURL = ClientUtil.buildTargetURL(baseURL, params);
        assertEquals(correctURL, resultURL);
    }
}
