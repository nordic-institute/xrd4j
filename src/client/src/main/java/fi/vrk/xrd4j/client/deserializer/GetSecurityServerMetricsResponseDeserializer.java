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
package fi.vrk.xrd4j.client.deserializer;

import fi.vrk.xrd4j.common.util.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;

import javax.xml.soap.Node;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

/**
 * This class is used for deserializing responses of getSecurityServerMetrics
 * monitoring service.
 *
 * @author Petteri Kivimäki
 */
public class GetSecurityServerMetricsResponseDeserializer extends AbstractResponseDeserializer<String, NodeList> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetSecurityServerMetricsResponseDeserializer.class);

    /**
     * Constructs and initializes a new getSecurityServerMetricsResponseDeserializer.
     */
    public GetSecurityServerMetricsResponseDeserializer() {
        super.isMetaServiceResponse = true;
    }

    /**
     * The message pair doesn't contain request data - returns always null.
     *
     * @param requestNode request element of the SOAP message
     * @return null
     * @throws SOAPException if there's a SOAP exception
     */
    @Override
    protected String deserializeRequestData(Node requestNode) throws SOAPException {
        return null;
    }

    /**
     * Deserializes getSecurityServerMetrics environmental monitoring service
     * response from SOAP to NodeList of metricSet objects.
     *
     * @param responseNode response element of the SOAP message
     * @param message entire SOAP message
     * @return NodeList list of metricSet elements
     * @throws SOAPException if there's a SOAP exception
     */
    @Override
    protected NodeList deserializeResponseData(Node responseNode, SOAPMessage message) throws SOAPException {
        // Get list of metricSet elements
        NodeList list = message.getSOAPBody().getElementsByTagNameNS(Constants.NS_ENV_MONITORING_URL, Constants.NS_ENV_MONITORING_ELEM_METRIC_SET);
        LOGGER.debug("Found {} {} elements from SOAP body. ", list.getLength(), Constants.NS_ENV_MONITORING_ELEM_METRIC_SET);
        return list;
    }
}
