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
package org.niis.xrd4j.common.util;

import jakarta.xml.soap.SOAPElement;
import org.junit.jupiter.api.Test;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test cases for SOAPHelper class.
 *
 * @author Petteri Kivimäki
 */
class SOAPHelperTest {

    /**
     * Multiple parameters with the same name.
     */
    @Test
    void testNodesToMap1() {
        String xml = "<request><param1>value1</param1><param1>value2</param1><param1>value3</param1><param2>value1</param2><param2>value2</param2><param3></param3><param4>\n</param4></request>";
        SOAPElement soap = SOAPHelper.xmlStrToSOAPElement(xml);
        Map<String, String> nodes = SOAPHelper.nodesToMap(soap.getChildNodes());
        if (nodes.keySet().size() != 3) {
            fail("Too many keys found: " + nodes.keySet().size() + ". The right key count is 3.");
        }
        if (!nodes.get("param1").equals("value3")) {
            fail("\"param1\" invalid value: " + nodes.get("param1"));
        }
        if (!nodes.get("param2").equals("value2")) {
            fail("\"param2\" invalid value: " + nodes.get("param2"));
        }
        if (!nodes.get("param3").equals("")) {
            fail("\"param3\" invalid value: " + nodes.get("param3"));
        }
    }

    /**
     * Unique parameter names with tabs and line breaks.
     */
    @Test
    void testNodesToMap2() {
        String xml = "<test:request xmlns:test=\"http://test.com\">\n<test:param1>\tvalue1\n</test:param1><test:param2>value2</test:param2>\r\n<test:param3>value3</test:param3></test:request>";
        SOAPElement soap = SOAPHelper.xmlStrToSOAPElement(xml);
        Map<String, String> nodes = SOAPHelper.nodesToMap(soap.getChildNodes());
        if (nodes.keySet().size() != 3) {
            fail("Too many keys found: " + nodes.keySet().size() + ". The right key count is 3.");
        }
        if (!nodes.get("param1").equals("value1")) {
            fail("\"param1\" invalid value: " + nodes.get("param1"));
        }
        if (!nodes.get("param2").equals("value2")) {
            fail("\"param2\" invalid value: " + nodes.get("param2"));
        }
        if (!nodes.get("param3").equals("value3")) {
            fail("\"param3\" invalid value: " + nodes.get("param3"));
        }
    }

    /**
     * Multiple parameters with the same name inside en extra wrapper element.
     */
    @Test
    void testNodesToMap3() {
        String xml = "<request><wrapper><param1>value1</param1><param2>value2</param2><param3>value3</param3></wrapper></request>";
        SOAPElement soap = SOAPHelper.xmlStrToSOAPElement(xml);
        Map<String, String> nodes = SOAPHelper.nodesToMap(soap.getChildNodes());
        if (nodes.keySet().size() != 3) {
            fail("Too many keys found: " + nodes.keySet().size() + ". The right key count is 3.");
        }
        if (!nodes.get("param1").equals("value1")) {
            fail("\"param1\" invalid value: " + nodes.get("param1"));
        }
        if (!nodes.get("param2").equals("value2")) {
            fail("\"param2\" invalid value: " + nodes.get("param2"));
        }
        if (!nodes.get("param3").equals("value3")) {
            fail("\"param3\" invalid value: " + nodes.get("param3"));
        }
    }

    /**
     * Multiple parameters with the same name inside two extra wrapper elements
     * with tabs and line breaks.
     */
    @Test
    void testNodesToMap4() {
        String xml = "<request><wrapperext>\n<wrapper>\n\t<param1>value1</param1>\r\n<param2>value2</param2><param3>value3</param3></wrapper></wrapperext></request>";
        SOAPElement soap = SOAPHelper.xmlStrToSOAPElement(xml);
        Map<String, String> nodes = SOAPHelper.nodesToMap(soap.getChildNodes());
        if (nodes.keySet().size() != 3) {
            fail("Too many keys found: " + nodes.keySet().size() + ". The right key count is 3.");
        }
        if (!nodes.get("param1").equals("value1")) {
            fail("\"param1\" invalid value: " + nodes.get("param1"));
        }
        if (!nodes.get("param2").equals("value2")) {
            fail("\"param2\" invalid value: " + nodes.get("param2"));
        }
        if (!nodes.get("param3").equals("value3")) {
            fail("\"param3\" invalid value: " + nodes.get("param3"));
        }
    }

    /**
     * Multiple parameters with the same name.
     */
    @Test
    void testNodesToMultiMap1() {
        String xml = "<request><param1>value1</param1><param1>value2</param1><param1>value3</param1><param2>value1</param2><param2>value2</param2><param3></param3></request>";
        SOAPElement soap = SOAPHelper.xmlStrToSOAPElement(xml);
        Map<String, List<String>> nodes = SOAPHelper.nodesToMultiMap(soap.getChildNodes());

        if (nodes.keySet().size() != 3) {
            fail("Too many keys found: " + nodes.keySet().size() + ". The right key count is 2.");
        }
        if (nodes.get("param1").size() != 3) {
            fail("Too many \"param1\" values found: " + nodes.get("param1").size() + ". The right value count is 3.");
        }
        if (nodes.get("param2").size() != 2) {
            fail("Too many \"param2\" values found: " + nodes.get("param2").size() + ". The right value count is 2.");
        }
        if (nodes.get("param3").size() != 1) {
            fail("Too many \"param3\" values found: " + nodes.get("param3").size() + ". The right value count is 1.");
        }
        List<String> params = new ArrayList<>();
        params.add("value1");
        params.add("value2");
        params.add("value3");
        for (String value : params) {
            boolean hit = false;
            for (String nodeValue : nodes.get("param1")) {
                if (nodeValue.equals(value)) {
                    hit = true;
                    break;
                }
            }
            if (!hit) {
                fail("Missing \"param1\" value: " + value);
            }
        }
        params = new ArrayList<>();
        params.add("value1");
        params.add("value2");
        for (String value : params) {
            boolean hit = false;
            for (String nodeValue : nodes.get("param2")) {
                if (nodeValue.equals(value)) {
                    hit = true;
                    break;
                }
            }
            if (!hit) {
                fail("Missing \"param2\" value: " + value);
            }
        }
        if (!nodes.get("param3").get(0).isEmpty()) {
            fail("\"param3\" value should be empty, but \"" + nodes.get("param3").get(0) + "\" was found.");
        }
    }

    /**
     * Multiple parameters with the same name inside an extra wrapper element.
     */
    @Test
    void testNodesToMultiMap2() {
        String xml = "<test:request xmlns:test=\"http://test.com\"><test:wrapper><test:param1>value1</test:param1><test:param1>value2</test:param1><test:param1>value3</test:param1><test:param2>value1</test:param2><test:param2>value2</test:param2></test:wrapper></test:request>";
        SOAPElement soap = SOAPHelper.xmlStrToSOAPElement(xml);
        Map<String, List<String>> nodes = SOAPHelper.nodesToMultiMap(soap.getChildNodes());

        if (nodes.keySet().size() != 2) {
            fail("Too many keys found: " + nodes.keySet().size() + ". The right key count is 2.");
        }
        if (nodes.get("param1").size() != 3) {
            fail("Too many \"param1\" values found: " + nodes.get("param1").size() + ". The right value count is 3.");
        }
        if (nodes.get("param2").size() != 2) {
            fail("Too many \"param2\" values found: " + nodes.get("param1").size() + ". The right value count is 2.");
        }
        List<String> params = new ArrayList<>();
        params.add("value1");
        params.add("value2");
        params.add("value3");
        for (String value : params) {
            boolean hit = false;
            for (String nodeValue : nodes.get("param1")) {
                if (nodeValue.equals(value)) {
                    hit = true;
                    break;
                }
            }
            if (!hit) {
                fail("Missing \"param1\" value: " + value);
            }
        }
        params = new ArrayList<>();
        params.add("value1");
        params.add("value2");
        for (String value : params) {
            boolean hit = false;
            for (String nodeValue : nodes.get("param2")) {
                if (nodeValue.equals(value)) {
                    hit = true;
                    break;
                }
            }
            if (!hit) {
                fail("Missing \"param2\" value: " + value);
            }
        }

    }

    /**
     * Multiple parameters with the same name inside two extra wrapper elements
     * with tabs and line breaks.
     */
    @Test
    void testNodesToMultiMap3() {
        String xml = "<request><extwrapper><wrapper>\n<param1>\nvalue1\n</param1>\n<param1>\t\tvalue2</param1>\r\n<param1>value3</param1>\t\n<param2>value1</param2><param2>value2</param2>\t</wrapper>\n</extwrapper></request>";
        SOAPElement soap = SOAPHelper.xmlStrToSOAPElement(xml);
        Map<String, List<String>> nodes = SOAPHelper.nodesToMultiMap(soap.getChildNodes());

        if (nodes.keySet().size() != 2) {
            fail("Too many keys found: " + nodes.keySet().size() + ". The right key count is 2.");
        }
        if (nodes.get("param1").size() != 3) {
            fail("Too many \"param1\" values found: " + nodes.get("param1").size() + ". The right value count is 3.");
        }
        if (nodes.get("param2").size() != 2) {
            fail("Too many \"param2\" values found: " + nodes.get("param1").size() + ". The right value count is 2.");
        }
        List<String> params = new ArrayList<>();
        params.add("value1");
        params.add("value2");
        params.add("value3");
        for (String value : params) {
            boolean hit = false;
            for (String nodeValue : nodes.get("param1")) {
                if (nodeValue.equals(value)) {
                    hit = true;
                    break;
                }
            }
            if (!hit) {
                fail("Missing \"param1\" value: " + value);
            }
        }
        params = new ArrayList<>();
        params.add("value1");
        params.add("value2");
        for (String value : params) {
            boolean hit = false;
            for (String nodeValue : nodes.get("param2")) {
                if (nodeValue.equals(value)) {
                    hit = true;
                    break;
                }
            }
            if (!hit) {
                fail("Missing \"param2\" value: " + value);
            }
        }

    }

    /**
     * Test reading version information
     */
    @Test
    void testGetXRdVersionInfo1() {
        String xml = "<m:getSecurityServerMetricsResponse xmlns:m=\"http://x-road.eu/xsd/monitoring\"><m:metricSet><m:name>SERVER:FI/GOV/MEMBER1/server1</m:name><m:stringMetric><m:name>proxyVersion</m:name><m:value>6.9.0-1.20170104084337gitb8fe14a</m:value></m:stringMetric><m:metricSet><m:name>systemMetrics</m:name><m:histogramMetric><m:name>CommittedVirtualMemory</m:name><m:updated>2017-02-18T05:13:54.864Z</m:updated><m:min>2331078656</m:min><m:max>2331078656</m:max><m:mean>2331078656</m:mean><m:median>2331078656</m:median><m:stddev>0.0</m:stddev></m:histogramMetric><m:numericMetric><m:name>DiskSpaceFree_/</m:name><m:value>4594552832</m:value></m:numericMetric><m:stringMetric><m:name>OperatingSystem</m:name><m:value>Linux version 3.13.0-106-generic (buildd@lcy01-30) (gcc version 4.8.4 (Ubuntu 4.8.4-2ubuntu1~14.04.3) ) #153-Ubuntu SMP Tue Dec 6 15:44:32 UTC 2016</m:value></m:stringMetric><m:metricSet><m:name>Processes</m:name><m:metricSet><m:name>1</m:name><m:stringMetric><m:name>processId</m:name><m:value>1</m:value></m:stringMetric><m:stringMetric><m:name>command</m:name><m:value>init</m:value></m:stringMetric><m:stringMetric><m:name>cpuLoad</m:name><m:value>5.1</m:value></m:stringMetric><m:stringMetric><m:name>memUsed</m:name><m:value>0.0</m:value></m:stringMetric><m:stringMetric><m:name>startTime</m:name><m:value>11:21</m:value></m:stringMetric><m:stringMetric><m:name>userId</m:name><m:value>root</m:value></m:stringMetric></m:metricSet>   </m:metricSet><m:metricSet><m:name>Xroad Processes</m:name><m:metricSet><m:name>1161</m:name><m:stringMetric><m:name>processId</m:name><m:value>1161</m:value></m:stringMetric><m:stringMetric><m:name>command</m:name><m:value>/usr/lib/jvm/java-1.8.0-openjdk-amd64/bin/java -Xmx192m -XX:MaxMetaspaceSize=100m -Djruby.compile.mode=OFF -Djna.tmpdir=/var/lib/xroad -Djetty.admin.port=8083 -Djetty.public.port=8084 -Daddon.extraClasspath= -Dlogback.configurationFile=/etc/xroad/conf.d/jetty-logback.xml -XX:+UseG1GC -Dfile.encoding=UTF-8 -Xshare:auto -Djdk.tls.ephemeralDHKeySize=2048 -cp /usr/share/xroad/jetty9/start.jar org.eclipse.jetty.start.Main jetty.home=/usr/share/xroad/jetty9</m:value></m:stringMetric><m:stringMetric><m:name>cpuLoad</m:name><m:value>9.3</m:value></m:stringMetric><m:stringMetric><m:name>memUsed</m:name><m:value>2.6</m:value></m:stringMetric><m:stringMetric><m:name>startTime</m:name><m:value>11:22</m:value></m:stringMetric><m:stringMetric><m:name>userId</m:name><m:value>xroad</m:value></m:stringMetric></m:metricSet><m:metricSet><m:name>1162</m:name><m:stringMetric><m:name>processId</m:name><m:value>1162</m:value></m:stringMetric><m:stringMetric><m:name>command</m:name><m:value>/usr/lib/jvm/java-1.8.0-openjdk-amd64/bin/java -Xmx50m -XX:MaxMetaspaceSize=30m -Dlogback.configurationFile=/etc/xroad/conf.d/confclient-logback-service.xml -XX:+UseG1GC -Dfile.encoding=UTF-8 -Xshare:auto -Djdk.tls.ephemeralDHKeySize=2048 -cp /usr/share/xroad/jlib/configuration-client.jar ee.ria.xroad.common.conf.globalconf.ConfigurationClientMain</m:value></m:stringMetric><m:stringMetric><m:name>cpuLoad</m:name><m:value>11.0</m:value></m:stringMetric><m:stringMetric><m:name>memUsed</m:name><m:value>2.7</m:value></m:stringMetric><m:stringMetric><m:name>startTime</m:name><m:value>11:22</m:value></m:stringMetric><m:stringMetric><m:name>userId</m:name><m:value>xroad</m:value></m:stringMetric></m:metricSet></m:metricSet><m:metricSet><m:name>Packages</m:name><m:stringMetric><m:name>accountsservice</m:name><m:value>0.6.35-0ubuntu7.3</m:value></m:stringMetric><m:stringMetric><m:name>acpid</m:name><m:value>1:2.0.21-1ubuntu2</m:value></m:stringMetric><m:stringMetric><m:name>xroad-addon-messagelog</m:name><m:value>6.9.0-1.20170104084337gitb8fe14a</m:value></m:stringMetric><m:stringMetric><m:name>xroad-addon-metaservices</m:name><m:value>6.9.0-1.20170104084337gitb8fe14a</m:value></m:stringMetric><m:stringMetric><m:name>xroad-addon-proxymonitor</m:name><m:value>6.9.0-1.20170104084337gitb8fe14a</m:value></m:stringMetric><m:stringMetric><m:name>xroad-addon-wsdlvalidator</m:name><m:value>6.9.0-1.20170104084337gitb8fe14a</m:value></m:stringMetric><m:stringMetric><m:name>xroad-common</m:name><m:value>6.9.0-1.20170104084337gitb8fe14a</m:value></m:stringMetric><m:stringMetric><m:name>xroad-jetty9</m:name><m:value>6.9.0-1.20170104084337gitb8fe14a</m:value></m:stringMetric><m:stringMetric><m:name>xroad-monitor</m:name><m:value>6.9.0-1.20170104084337gitb8fe14a</m:value></m:stringMetric><m:stringMetric><m:name>xroad-proxy</m:name><m:value>6.9.0-1.20170104084337gitb8fe14a</m:value></m:stringMetric><m:stringMetric><m:name>xroad-securityserver</m:name><m:value>6.9.0-1.20170104084337gitb8fe14a</m:value></m:stringMetric><m:stringMetric><m:name>xroad-securityserver-fi</m:name><m:value>6.9.0-1.20170104084337gitb8fe14a</m:value></m:stringMetric><m:stringMetric><m:name>xz-utils</m:name><m:value>5.1.1alpha+20120614-2ubuntu2</m:value></m:stringMetric><m:stringMetric><m:name>zerofree</m:name><m:value>1.0.2-1ubuntu1</m:value></m:stringMetric><m:stringMetric><m:name>zlib1g</m:name><m:value>1:1.2.8.dfsg-1ubuntu1</m:value></m:stringMetric></m:metricSet></m:metricSet></m:metricSet></m:getSecurityServerMetricsResponse>";
        SOAPElement message = SOAPHelper.xmlStrToSOAPElement(xml);
        NodeList list = message.getElementsByTagNameNS(Constants.NS_ENV_MONITORING_URL, Constants.NS_ENV_MONITORING_ELEM_METRIC_SET);

        Map<String, String> results = SOAPHelper.getXRdVersionInfo(list);
        assertEquals("6.9.0-1.20170104084337gitb8fe14a", results.get("xroad-addon-messagelog"));
        assertEquals("6.9.0-1.20170104084337gitb8fe14a", results.get("xroad-addon-metaservices"));
        assertEquals("6.9.0-1.20170104084337gitb8fe14a", results.get("xroad-addon-proxymonitor"));
        assertEquals("6.9.0-1.20170104084337gitb8fe14a", results.get("xroad-addon-wsdlvalidator"));
        assertEquals("6.9.0-1.20170104084337gitb8fe14a", results.get("xroad-common"));
        assertEquals("6.9.0-1.20170104084337gitb8fe14a", results.get("xroad-jetty9"));
        assertEquals("6.9.0-1.20170104084337gitb8fe14a", results.get("xroad-monitor"));
        assertEquals("6.9.0-1.20170104084337gitb8fe14a", results.get("xroad-proxy"));
        assertEquals("6.9.0-1.20170104084337gitb8fe14a", results.get("xroad-securityserver"));
        assertEquals("6.9.0-1.20170104084337gitb8fe14a", results.get("xroad-securityserver-fi"));
    }

    /**
     * Test reading version information
     */
    @Test
    void testGetXRdVersionInfo2() {
        String xml = "<m:getSecurityServerMetricsResponse xmlns:m=\"http://x-road.eu/xsd/monitoring\"><m:metricSet><m:name>SERVER:FI/GOV/MEMBER1/server1</m:name><m:stringMetric><m:name>proxyVersion</m:name><m:value>6.9.0-1.20170104084337gitb8fe14a</m:value></m:stringMetric><m:metricSet><m:name>systemMetrics</m:name><m:histogramMetric><m:name>CommittedVirtualMemory</m:name><m:updated>2017-02-18T05:13:54.864Z</m:updated><m:min>2331078656</m:min><m:max>2331078656</m:max><m:mean>2331078656</m:mean><m:median>2331078656</m:median><m:stddev>0.0</m:stddev></m:histogramMetric><m:numericMetric><m:name>DiskSpaceFree_/</m:name><m:value>4594552832</m:value></m:numericMetric><m:stringMetric><m:name>OperatingSystem</m:name><m:value>Linux version 3.13.0-106-generic (buildd@lcy01-30) (gcc version 4.8.4 (Ubuntu 4.8.4-2ubuntu1~14.04.3) ) #153-Ubuntu SMP Tue Dec 6 15:44:32 UTC 2016</m:value></m:stringMetric><m:metricSet><m:name>Processes</m:name><m:metricSet><m:name>1</m:name><m:stringMetric><m:name>processId</m:name><m:value>1</m:value></m:stringMetric><m:stringMetric><m:name>command</m:name><m:value>init</m:value></m:stringMetric><m:stringMetric><m:name>cpuLoad</m:name><m:value>5.1</m:value></m:stringMetric><m:stringMetric><m:name>memUsed</m:name><m:value>0.0</m:value></m:stringMetric><m:stringMetric><m:name>startTime</m:name><m:value>11:21</m:value></m:stringMetric><m:stringMetric><m:name>userId</m:name><m:value>root</m:value></m:stringMetric></m:metricSet>   </m:metricSet><m:metricSet><m:name>Xroad Processes</m:name><m:metricSet><m:name>1161</m:name><m:stringMetric><m:name>processId</m:name><m:value>1161</m:value></m:stringMetric><m:stringMetric><m:name>command</m:name><m:value>/usr/lib/jvm/java-1.8.0-openjdk-amd64/bin/java -Xmx192m -XX:MaxMetaspaceSize=100m -Djruby.compile.mode=OFF -Djna.tmpdir=/var/lib/xroad -Djetty.admin.port=8083 -Djetty.public.port=8084 -Daddon.extraClasspath= -Dlogback.configurationFile=/etc/xroad/conf.d/jetty-logback.xml -XX:+UseG1GC -Dfile.encoding=UTF-8 -Xshare:auto -Djdk.tls.ephemeralDHKeySize=2048 -cp /usr/share/xroad/jetty9/start.jar org.eclipse.jetty.start.Main jetty.home=/usr/share/xroad/jetty9</m:value></m:stringMetric><m:stringMetric><m:name>cpuLoad</m:name><m:value>9.3</m:value></m:stringMetric><m:stringMetric><m:name>memUsed</m:name><m:value>2.6</m:value></m:stringMetric><m:stringMetric><m:name>startTime</m:name><m:value>11:22</m:value></m:stringMetric><m:stringMetric><m:name>userId</m:name><m:value>xroad</m:value></m:stringMetric></m:metricSet><m:metricSet><m:name>1162</m:name><m:stringMetric><m:name>processId</m:name><m:value>1162</m:value></m:stringMetric><m:stringMetric><m:name>command</m:name><m:value>/usr/lib/jvm/java-1.8.0-openjdk-amd64/bin/java -Xmx50m -XX:MaxMetaspaceSize=30m -Dlogback.configurationFile=/etc/xroad/conf.d/confclient-logback-service.xml -XX:+UseG1GC -Dfile.encoding=UTF-8 -Xshare:auto -Djdk.tls.ephemeralDHKeySize=2048 -cp /usr/share/xroad/jlib/configuration-client.jar ee.ria.xroad.common.conf.globalconf.ConfigurationClientMain</m:value></m:stringMetric><m:stringMetric><m:name>cpuLoad</m:name><m:value>11.0</m:value></m:stringMetric><m:stringMetric><m:name>memUsed</m:name><m:value>2.7</m:value></m:stringMetric><m:stringMetric><m:name>startTime</m:name><m:value>11:22</m:value></m:stringMetric><m:stringMetric><m:name>userId</m:name><m:value>xroad</m:value></m:stringMetric></m:metricSet></m:metricSet><m:metricSet><m:name>Packages</m:name><m:stringMetric><m:name>accountsservice</m:name><m:value>0.6.35-0ubuntu7.3</m:value></m:stringMetric><m:stringMetric><m:name>acpid</m:name><m:value>1:2.0.21-1ubuntu2</m:value></m:stringMetric><m:stringMetric><m:name>xz-utils</m:name><m:value>5.1.1alpha+20120614-2ubuntu2</m:value></m:stringMetric><m:stringMetric><m:name>zerofree</m:name><m:value>1.0.2-1ubuntu1</m:value></m:stringMetric><m:stringMetric><m:name>zlib1g</m:name><m:value>1:1.2.8.dfsg-1ubuntu1</m:value></m:stringMetric></m:metricSet></m:metricSet></m:metricSet></m:getSecurityServerMetricsResponse>";
        SOAPElement message = SOAPHelper.xmlStrToSOAPElement(xml);
        NodeList list = message.getElementsByTagNameNS(Constants.NS_ENV_MONITORING_URL, Constants.NS_ENV_MONITORING_ELEM_METRIC_SET);

        Map<String, String> results = SOAPHelper.getXRdVersionInfo(list);
        assertEquals(0, results.size());
    }

    /**
     * Test reading version information
     */
    @Test
    void testGetXRdVersionInfo3() {
        String xml = "<m:getSecurityServerMetricsResponse xmlns:m=\"http://x-road.eu/xsd/monitoring\"><m:metricSet><m:name>SERVER:FI/GOV/MEMBER1/server1</m:name><m:stringMetric><m:name>proxyVersion</m:name><m:value>6.9.0-1.20170104084337gitb8fe14a</m:value></m:stringMetric><m:metricSet><m:name>systemMetrics</m:name><m:histogramMetric><m:name>CommittedVirtualMemory</m:name><m:updated>2017-02-18T05:13:54.864Z</m:updated><m:min>2331078656</m:min><m:max>2331078656</m:max><m:mean>2331078656</m:mean><m:median>2331078656</m:median><m:stddev>0.0</m:stddev></m:histogramMetric><m:numericMetric><m:name>DiskSpaceFree_/</m:name><m:value>4594552832</m:value></m:numericMetric><m:stringMetric><m:name>OperatingSystem</m:name><m:value>Linux version 3.13.0-106-generic (buildd@lcy01-30) (gcc version 4.8.4 (Ubuntu 4.8.4-2ubuntu1~14.04.3) ) #153-Ubuntu SMP Tue Dec 6 15:44:32 UTC 2016</m:value></m:stringMetric><m:metricSet><m:name>Processes</m:name><m:metricSet><m:name>1</m:name><m:stringMetric><m:name>processId</m:name><m:value>1</m:value></m:stringMetric><m:stringMetric><m:name>command</m:name><m:value>init</m:value></m:stringMetric><m:stringMetric><m:name>cpuLoad</m:name><m:value>5.1</m:value></m:stringMetric><m:stringMetric><m:name>memUsed</m:name><m:value>0.0</m:value></m:stringMetric><m:stringMetric><m:name>startTime</m:name><m:value>11:21</m:value></m:stringMetric><m:stringMetric><m:name>userId</m:name><m:value>root</m:value></m:stringMetric></m:metricSet>   </m:metricSet><m:metricSet><m:name>Xroad Processes</m:name><m:metricSet><m:name>1161</m:name><m:stringMetric><m:name>processId</m:name><m:value>1161</m:value></m:stringMetric><m:stringMetric><m:name>command</m:name><m:value>/usr/lib/jvm/java-1.8.0-openjdk-amd64/bin/java -Xmx192m -XX:MaxMetaspaceSize=100m -Djruby.compile.mode=OFF -Djna.tmpdir=/var/lib/xroad -Djetty.admin.port=8083 -Djetty.public.port=8084 -Daddon.extraClasspath= -Dlogback.configurationFile=/etc/xroad/conf.d/jetty-logback.xml -XX:+UseG1GC -Dfile.encoding=UTF-8 -Xshare:auto -Djdk.tls.ephemeralDHKeySize=2048 -cp /usr/share/xroad/jetty9/start.jar org.eclipse.jetty.start.Main jetty.home=/usr/share/xroad/jetty9</m:value></m:stringMetric><m:stringMetric><m:name>cpuLoad</m:name><m:value>9.3</m:value></m:stringMetric><m:stringMetric><m:name>memUsed</m:name><m:value>2.6</m:value></m:stringMetric><m:stringMetric><m:name>startTime</m:name><m:value>11:22</m:value></m:stringMetric><m:stringMetric><m:name>userId</m:name><m:value>xroad</m:value></m:stringMetric></m:metricSet><m:metricSet><m:name>1162</m:name><m:stringMetric><m:name>processId</m:name><m:value>1162</m:value></m:stringMetric><m:stringMetric><m:name>command</m:name><m:value>/usr/lib/jvm/java-1.8.0-openjdk-amd64/bin/java -Xmx50m -XX:MaxMetaspaceSize=30m -Dlogback.configurationFile=/etc/xroad/conf.d/confclient-logback-service.xml -XX:+UseG1GC -Dfile.encoding=UTF-8 -Xshare:auto -Djdk.tls.ephemeralDHKeySize=2048 -cp /usr/share/xroad/jlib/configuration-client.jar ee.ria.xroad.common.conf.globalconf.ConfigurationClientMain</m:value></m:stringMetric><m:stringMetric><m:name>cpuLoad</m:name><m:value>11.0</m:value></m:stringMetric><m:stringMetric><m:name>memUsed</m:name><m:value>2.7</m:value></m:stringMetric><m:stringMetric><m:name>startTime</m:name><m:value>11:22</m:value></m:stringMetric><m:stringMetric><m:name>userId</m:name><m:value>xroad</m:value></m:stringMetric></m:metricSet></m:metricSet><m:metricSet><m:name>Packages</m:name></m:metricSet></m:metricSet></m:metricSet></m:getSecurityServerMetricsResponse>";
        SOAPElement message = SOAPHelper.xmlStrToSOAPElement(xml);
        NodeList list = message.getElementsByTagNameNS(Constants.NS_ENV_MONITORING_URL, Constants.NS_ENV_MONITORING_ELEM_METRIC_SET);

        Map<String, String> results = SOAPHelper.getXRdVersionInfo(list);
        assertEquals(0, results.size());
    }

    /**
     * Test reading version information
     */
    @Test
    void testGetXRdVersionInfo4() {
        String xml = "<m:getSecurityServerMetricsResponse xmlns:m=\"http://x-road.eu/xsd/monitoring\"><m:metricSet><m:name>SERVER:FI/GOV/MEMBER1/server1</m:name><m:stringMetric><m:name>proxyVersion</m:name><m:value>6.9.0-1.20170104084337gitb8fe14a</m:value></m:stringMetric><m:metricSet><m:name>systemMetrics</m:name><m:histogramMetric><m:name>CommittedVirtualMemory</m:name><m:updated>2017-02-18T05:13:54.864Z</m:updated><m:min>2331078656</m:min><m:max>2331078656</m:max><m:mean>2331078656</m:mean><m:median>2331078656</m:median><m:stddev>0.0</m:stddev></m:histogramMetric><m:numericMetric><m:name>DiskSpaceFree_/</m:name><m:value>4594552832</m:value></m:numericMetric><m:stringMetric><m:name>OperatingSystem</m:name><m:value>Linux version 3.13.0-106-generic (buildd@lcy01-30) (gcc version 4.8.4 (Ubuntu 4.8.4-2ubuntu1~14.04.3) ) #153-Ubuntu SMP Tue Dec 6 15:44:32 UTC 2016</m:value></m:stringMetric><m:metricSet><m:name>Processes</m:name><m:metricSet><m:name>1</m:name><m:stringMetric><m:name>processId</m:name><m:value>1</m:value></m:stringMetric><m:stringMetric><m:name>command</m:name><m:value>init</m:value></m:stringMetric><m:stringMetric><m:name>cpuLoad</m:name><m:value>5.1</m:value></m:stringMetric><m:stringMetric><m:name>memUsed</m:name><m:value>0.0</m:value></m:stringMetric><m:stringMetric><m:name>startTime</m:name><m:value>11:21</m:value></m:stringMetric><m:stringMetric><m:name>userId</m:name><m:value>root</m:value></m:stringMetric></m:metricSet>   </m:metricSet><m:metricSet><m:name>Xroad Processes</m:name><m:metricSet><m:name>1161</m:name><m:stringMetric><m:name>processId</m:name><m:value>1161</m:value></m:stringMetric><m:stringMetric><m:name>command</m:name><m:value>/usr/lib/jvm/java-1.8.0-openjdk-amd64/bin/java -Xmx192m -XX:MaxMetaspaceSize=100m -Djruby.compile.mode=OFF -Djna.tmpdir=/var/lib/xroad -Djetty.admin.port=8083 -Djetty.public.port=8084 -Daddon.extraClasspath= -Dlogback.configurationFile=/etc/xroad/conf.d/jetty-logback.xml -XX:+UseG1GC -Dfile.encoding=UTF-8 -Xshare:auto -Djdk.tls.ephemeralDHKeySize=2048 -cp /usr/share/xroad/jetty9/start.jar org.eclipse.jetty.start.Main jetty.home=/usr/share/xroad/jetty9</m:value></m:stringMetric><m:stringMetric><m:name>cpuLoad</m:name><m:value>9.3</m:value></m:stringMetric><m:stringMetric><m:name>memUsed</m:name><m:value>2.6</m:value></m:stringMetric><m:stringMetric><m:name>startTime</m:name><m:value>11:22</m:value></m:stringMetric><m:stringMetric><m:name>userId</m:name><m:value>xroad</m:value></m:stringMetric></m:metricSet><m:metricSet><m:name>1162</m:name><m:stringMetric><m:name>processId</m:name><m:value>1162</m:value></m:stringMetric><m:stringMetric><m:name>command</m:name><m:value>/usr/lib/jvm/java-1.8.0-openjdk-amd64/bin/java -Xmx50m -XX:MaxMetaspaceSize=30m -Dlogback.configurationFile=/etc/xroad/conf.d/confclient-logback-service.xml -XX:+UseG1GC -Dfile.encoding=UTF-8 -Xshare:auto -Djdk.tls.ephemeralDHKeySize=2048 -cp /usr/share/xroad/jlib/configuration-client.jar ee.ria.xroad.common.conf.globalconf.ConfigurationClientMain</m:value></m:stringMetric><m:stringMetric><m:name>cpuLoad</m:name><m:value>11.0</m:value></m:stringMetric><m:stringMetric><m:name>memUsed</m:name><m:value>2.7</m:value></m:stringMetric><m:stringMetric><m:name>startTime</m:name><m:value>11:22</m:value></m:stringMetric><m:stringMetric><m:name>userId</m:name><m:value>xroad</m:value></m:stringMetric></m:metricSet></m:metricSet><m:metricSet></m:metricSet></m:metricSet></m:metricSet></m:getSecurityServerMetricsResponse>";
        SOAPElement message = SOAPHelper.xmlStrToSOAPElement(xml);
        NodeList list = message.getElementsByTagNameNS(Constants.NS_ENV_MONITORING_URL, Constants.NS_ENV_MONITORING_ELEM_METRIC_SET);

        Map<String, String> results = SOAPHelper.getXRdVersionInfo(list);
        assertEquals(0, results.size());
    }

}
