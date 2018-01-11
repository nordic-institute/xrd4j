/**
 * The MIT License
 * Copyright © 2017 Population Register Centre (VRK)
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

import fi.vrk.xrd4j.common.member.ObjectType;
import fi.vrk.xrd4j.common.member.ProducerMember;
import fi.vrk.xrd4j.common.util.Constants;
import fi.vrk.xrd4j.common.util.SOAPHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;

import javax.xml.soap.Node;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class is used for deserializing responses of listMethods and
 * allowedMethods meta services.
 *
 * @author Petteri Kivimäki
 */
public class ListServicesResponseDeserializer extends AbstractResponseDeserializer<String, List<ProducerMember>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListServicesResponseDeserializer.class);
    
    /**
     * Constructs and initializes a new ListServicesResponseDeserializer.
     */
    public ListServicesResponseDeserializer() {
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
     * Deserializes listMethods and allowedMethods meta service responses from
     * SOAP to list of ProducerMember objects.
     *
     * @param responseNode response element of the SOAP message
     * @param message entire SOAP message
     * @return list ProducerMember objects
     * @throws SOAPException if there's a SOAP exception
     */
    @Override
    protected List<ProducerMember> deserializeResponseData(Node responseNode, SOAPMessage message) throws SOAPException {
        List<ProducerMember> producers = new ArrayList<>();
        // Get list of services
        NodeList list = message.getSOAPBody().getElementsByTagNameNS(Constants.NS_XRD_URL, Constants.NS_XRD_ELEM_SERVICE);
        LOGGER.debug("Found {} {} elements from SOAP body. ", list.getLength(), Constants.NS_XRD_ELEM_SERVICE);

        for (int i = 0; i < list.getLength(); i++) {
            LOGGER.debug("Deserialize \"{}\".", Constants.NS_XRD_ELEM_SERVICE);
            // Service headers
            Map<String, String> service = SOAPHelper.nodesToMap(list.item(i).getChildNodes());
            // Service object type
            ObjectType serviceObjectType = this.deserializeObjectType((Node) list.item(i));
            try {
                producers.add(super.getProducerMember(service, serviceObjectType));
            } catch (Exception e) {
                LOGGER.error("Deserializing \"{}\" failed - skip.", Constants.NS_XRD_ELEM_SERVICE);
                LOGGER.error(e.getMessage(), e);
            }
        }
        return producers;
    }
}
