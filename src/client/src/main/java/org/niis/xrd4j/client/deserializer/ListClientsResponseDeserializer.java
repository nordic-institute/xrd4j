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
package org.niis.xrd4j.client.deserializer;

import org.niis.xrd4j.common.deserializer.AbstractHeaderDeserializer;
import org.niis.xrd4j.common.exception.XRd4JException;
import org.niis.xrd4j.common.member.ConsumerMember;
import org.niis.xrd4j.common.member.ObjectType;
import org.niis.xrd4j.common.util.Constants;
import org.niis.xrd4j.common.util.SOAPHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class implements a deserializer for deserializing the response of the
 * listClients meta service.
 *
 * @author Petteri Kivimäki
 */
public class ListClientsResponseDeserializer extends AbstractHeaderDeserializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListClientsResponseDeserializer.class);
    
    /**
     * Deserializes a list of ConsumerMember objects from the given XML. If
     * deserialiing the XML fails, null is returned.
     *
     * @param xml XML to be deserialized
     * @return list of ConsumerMember objects
     */
    public List<ConsumerMember> deserializeConsumerList(String xml) {
        LOGGER.debug("Deserialize a list of consumers from XML.");
        // Convert XML string to XML Document for deserializing
        Document doc = SOAPHelper.xmlStrToDoc(xml);
        try {
            // Get list of consumers
            return this.deserializeMembers(doc);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Deserializes member elements from the given XML document.
     *
     * @param doc XML document to be deserialized
     * @return list of ConsumerMember objects
     * @throws XRd4JException if there's a XRd4J error
     */
    private List<ConsumerMember> deserializeMembers(final Document doc)
            throws XRd4JException {
        List<ConsumerMember> results = new ArrayList<>();
        LOGGER.debug("Deserialize \"{}\".", Constants.NS_XRD_ELEM_CLIENT_LIST);

        NodeList list = doc.getElementsByTagNameNS(Constants.NS_XRD_URL, Constants.NS_XRD_ELEM_ID);
        LOGGER.debug("Found {} {} elements from XML. ", list.getLength(), Constants.NS_XRD_ELEM_ID);
        for (int i = 0; i < list.getLength(); i++) {
            // Client object type
            ObjectType clientObjectType = super.deserializeObjectType(list.item(i));
            // Client headers
            Map<String, String> client = SOAPHelper.nodesToMap(list.item(i).getChildNodes());
            LOGGER.trace("Element found : \"{}\"", Constants.NS_XRD_ELEM_ID);
            results.add(super.getConsumerMember(client, clientObjectType));
        }
        LOGGER.debug("Deserialized {} \"{}\" elements from the given XML document.", results.size(), Constants.NS_XRD_ELEM_ID);
        return results;
    }
}
