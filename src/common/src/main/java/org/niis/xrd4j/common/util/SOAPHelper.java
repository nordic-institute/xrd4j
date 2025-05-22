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

import org.niis.xrd4j.common.message.AbstractMessage;

import com.sun.xml.messaging.saaj.soap.impl.ElementImpl;
import jakarta.xml.soap.AttachmentPart;
import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.MimeHeaders;
import jakarta.xml.soap.Node;
import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.w3c.dom.Node.ELEMENT_NODE;
import static org.w3c.dom.Node.TEXT_NODE;

/**
 * This class offers some helper methods for handling SOAPMessage objects.
 *
 * @author Petteri Kivimäki
 */
public final class SOAPHelper {
    private static final Charset CHARSET = UTF_8;
    private static final Logger LOGGER = LoggerFactory.getLogger(SOAPHelper.class);
    private static final MessageFactory MSG_FACTORY;

    static {
        try {
            MSG_FACTORY = MessageFactory.newInstance();
        } catch (SOAPException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Constructs and initializes a new SOAPHelper object. Should never be used.
     */
    private SOAPHelper() {
    }

    /**
     * Goes through all the child nodes of the given node and returns the first
     * child that matches the given name. If no child with the given is found,
     * null is returned.
     *
     * @param node     parent node
     * @param nodeName name of the node to be searched
     * @return node with the given name or null
     */
    public static Node getNode(Node node, String nodeName) {
        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
            if (node.getChildNodes().item(i).getNodeType() == ELEMENT_NODE
                    && node.getChildNodes().item(i).getLocalName().equals(nodeName)) {
                return (Node) node.getChildNodes().item(i);
            }
        }
        return null;
    }

    /**
     * Converts the given SOAPMessage to byte array.
     *
     * @param message SOAPMessage object to be converted
     * @return byte array containing the SOAPMessage or null
     */
    public static byte[] toByteArray(SOAPMessage message) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            message.writeTo(out);
            return out.toByteArray();
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return null;
        }
    }

    /**
     * Converts the given SOAPMessage to String.
     *
     * @param message SOAPMessage object to be converted
     * @return String presentation of the given SOAPMessage
     */
    public static String toString(SOAPMessage message) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            message.writeTo(out);
            return out.toString(CHARSET);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return "";
        }
    }

    /**
     * Converts the given Node to String.
     *
     * @param node Node object to be converted
     * @return String presentation of the given Node
     */
    public static String toString(Node node) {
        StringWriter sw = new StringWriter();
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            Transformer t = factory.newTransformer();
            t.setOutputProperty(OutputKeys.ENCODING, CHARSET.toString());
            t.transform(new DOMSource(node), new StreamResult(sw));
            return sw.toString();
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return "";
        }
    }

    /**
     * Converts the given attachment part to string.
     *
     * @param att attachment part to be converted
     * @return string presentation of the attachment or null
     */
    public static String toString(AttachmentPart att) {
        try {
            return new Scanner(att.getRawContent(), CHARSET).useDelimiter("\\A").next();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Converts the given String to SOAPMessage. The String must contain a valid
     * SOAP message, otherwise null is returned.
     *
     * @param soap SOAP string to be converted
     * @return SOAPMessage or null
     */
    public static SOAPMessage toSOAP(String soap) {
        try {
            InputStream is = new ByteArrayInputStream(soap.getBytes(CHARSET));
            return SOAPHelper.toSOAP(is);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return null;
        }
    }

    /**
     * Converts the given InputStream to SOAPMessage. The stream must contain a
     * valid SOAP message, otherwise null is returned.
     *
     * @param is InputStream to be converted
     * @return SOAPMessage or null
     */
    public static SOAPMessage toSOAP(InputStream is) {
        try {
            return createSOAPMessage(new MimeHeaders(), is);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return null;
        }
    }

    /**
     * Converts the given InputStream to SOAPMessage. The stream must contain a
     * valid SOAP message, otherwise null is returned.
     *
     * @param is InputStream to be converted
     * @param mh MIME headers of the SOAP request
     * @return SOAPMessage or null
     */
    public static SOAPMessage toSOAP(InputStream is, MimeHeaders mh) {
        try {
            return createSOAPMessage(mh, is);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return null;
        }
    }

    /**
     * Transfers the given NodeList to a Map that contains all the list items as
     * key-value-pairs, localName as the key and NodeValue as the value. The
     * given NodeList is parsed recursively.
     *
     * @param list NodeList to be transfered
     * @return Map that contains all the list items as key-value-pairs
     */
    public static Map<String, String> nodesToMap(NodeList list) {
        return SOAPHelper.nodesToMap(list, false);
    }

    /**
     * Transfers the given NodeList to a Map that contains all the list items as
     * key-value-pairs, localName as the key and NodeValue as the value. Each
     * key can have only one value. The given NodeList is parsed recursively.
     *
     * @param list      NodeList to be transfered
     * @param upperCase store all keys in upper case
     * @return Map that contains all the list items as key-value-pairs
     */
    public static Map<String, String> nodesToMap(NodeList list, boolean upperCase) {
        Map<String, String> map = new HashMap<>();
        SOAPHelper.nodesToMap(list, upperCase, map);
        return map;
    }

    /**
     * Transfers the given NodeList to a Map that contains all the list items as
     * key-value-pairs, localName as the key and NodeValue as the value. Each
     * key can have only one value. The given NodeList is parsed recursively.
     *
     * @param list      NodeList to be transfered
     * @param upperCase store all keys in upper case
     * @param map       Map for the results
     */
    public static void nodesToMap(NodeList list, boolean upperCase, Map<String, String> map) {
        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i).getNodeType() == ELEMENT_NODE && list.item(i).hasChildNodes()) {
                nodesToMap(list.item(i).getChildNodes(), upperCase, map);
            } else {
                processMapNode(list, i, upperCase, map);
            }
        }
    }

    /**
     * Transfers the given Node to a Map as key - value pair.
     *
     * @param list      NodeList containing the node to be transfered
     * @param index     index of the node to be transfered
     * @param upperCase store all keys in upper case
     * @param map       Map for the results
     */
    private static void processMapNode(NodeList list, int index, boolean upperCase, Map<String, String> map) {
        if (list.item(index).getNodeType() == ELEMENT_NODE && !list.item(index).hasChildNodes()) {
            String key = list.item(index).getLocalName();
            map.put(upperCase ? key.toUpperCase() : key, "");
        } else if (list.item(index).getNodeType() == TEXT_NODE) {
            String key = list.item(index).getParentNode().getLocalName();
            String value = list.item(index).getNodeValue();
            value = value.trim();
            if (!value.isEmpty()) {
                map.put(upperCase ? key.toUpperCase() : key, value);
            }
        }
    }

    /**
     * Transfers the given NodeList to a MultiMap that contains all the list
     * items as key - value list pairs. Each key can have multiple values that
     * are stored in a list. The given NodeList is parsed recursively.
     *
     * @param list NodeList to be transfered
     * @return Map that contains all the list items as key - value list pairs
     */
    public static Map<String, List<String>> nodesToMultiMap(NodeList list) {
        Map<String, List<String>> map = new HashMap<>();
        SOAPHelper.nodesToMultiMap(list, map);
        return map;
    }

    /**
     * Transfers the given NodeList to a MultiMap that contains all the list
     * items as key - value list pairs. Each key can have multiple values that
     * are stored in a list. The given NodeList is parsed recursively.
     *
     * @param list NodeList to be transfered
     * @param map  Map for the results
     */
    public static void nodesToMultiMap(NodeList list, Map<String, List<String>> map) {
        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i).getNodeType() == ELEMENT_NODE && list.item(i).hasChildNodes()) {
                nodesToMultiMap(list.item(i).getChildNodes(), map);
            } else {
                processMultiMapNode(list, i, map);
            }
        }
    }

    /**
     * Transfers the given Node to a MultiMap as key - value list pair.
     *
     * @param list  NodeList containing the Node to be transfered
     * @param index index of the Node to be transfered
     * @param map   Map for the results
     */
    private static void processMultiMapNode(NodeList list, int index, Map<String, List<String>> map) {
        if (list.item(index).getNodeType() == ELEMENT_NODE && !list.item(index).hasChildNodes()) {
            String key = list.item(index).getLocalName();
            if (!map.containsKey(key)) {
                map.put(key, new ArrayList<>());
            }
            map.get(key).add("");
        } else if (list.item(index).getNodeType() == TEXT_NODE) {
            String key = list.item(index).getParentNode().getLocalName();
            String value = list.item(index).getNodeValue();
            value = value.trim();
            if (!value.isEmpty()) {
                if (!map.containsKey(key)) {
                    map.put(key, new ArrayList<>());
                }
                map.get(key).add(value);
            }
        }
    }

    /**
     * Adds the namespace URI and prefix of the ProvideMember related to the
     * given Message to the given Node and all its children. If the Node should
     * have another namespace, the old namespace is first removed and the new
     * namespace is added after that.
     *
     * @param node    Node to be modified
     * @param message Message that contains the ProviderMember which namespace
     * @return changed SOAPElement with added namespace URI and prefix of the ProviderMember
     */
    public static SOAPElement addNamespace(SOAPElement node, AbstractMessage message) {
        if (node.getNodeType() == ELEMENT_NODE) {
            var soapEl = node;

            try {
                soapEl = soapEl.setElementQName(new QName(soapEl.getLocalName()));
                var prefix = message.getProducer().getNamespacePrefix() != null ? message.getProducer().getNamespacePrefix() : "";
                soapEl = soapEl.addNamespaceDeclaration(prefix, message.getProducer().getNamespaceUrl())
                        .setElementQName(soapEl.createQName(soapEl.getLocalName(), prefix));
            } catch (SOAPException e) {
                LOGGER.error("Failed to add provider namespace", e);
                throw new RuntimeException(e);
            }

            soapEl.getChildElements().forEachRemaining(n -> {
                if (n instanceof SOAPElement) {
                    addNamespace((SOAPElement) n, message);
                }
            });
            return soapEl;
        }
        return node;
    }

    /**
     * Removes the namespace from the given Node and all its children.
     *
     * @param node Node to be modified
     */
    public static void removeNamespace(Node node) {
        if (node.getNodeType() == ELEMENT_NODE) {
            node.getOwnerDocument().renameNode(node, null, node.getLocalName());
        }
        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            SOAPHelper.removeNamespace((Node) list.item(i));
        }
    }

    /**
     * Searches the attachment with the given content id and returns its string
     * contents. If there's no attachment with the given content id or its value
     * is not a string, null is returned.
     *
     * @param contentId   content id of the attachment
     * @param attachments list of attachments to be searched
     * @return string value of the attachment or null
     */
    public static String getStringAttachment(String contentId, Iterator<?> attachments) {
        if (attachments == null) {
            return null;
        }
        try {
            while (attachments.hasNext()) {
                AttachmentPart att = (AttachmentPart) attachments.next();
                if (att.getContentId().equals(contentId)) {
                    return new Scanner(att.getRawContent(), CHARSET).useDelimiter("\\A").next();
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Returns the content type of the first SOAP attachment or null if there's
     * no attachments.
     *
     * @param message SOAP message
     * @return content type of the first attachment or null
     */
    public static String getAttachmentContentType(SOAPMessage message) {
        if (message.countAttachments() == 0) {
            return null;
        }
        AttachmentPart att = message.getAttachments().next();
        return att.getContentType();

    }

    /**
     * Checks if the given SOAP message has attachments. Returns true if and
     * only if the message has attachments. Otherwise returns false.
     *
     * @param message SOAP message to be checked
     * @return true if and only if the message has attachments; otherwise false
     */
    public static boolean hasAttachments(SOAPMessage message) {
        if (message == null) {
            return false;
        }
        return message.countAttachments() != 0;
    }

    /**
     * Converts the given XML string to SOAPElement.
     *
     * @param xml XML string
     * @return given XML string as a SOAPElement or null if the conversion
     * failed
     */
    public static SOAPElement xmlStrToSOAPElement(String xml) {
        LOGGER.debug("Convert XML string to SOAPElement. XML : \"{}\"", xml);
        // Try to convert XML string to XML Document
        Document doc = SOAPHelper.xmlStrToDoc(xml);
        if (doc == null) {
            LOGGER.warn("Convertin XML string to SOAP element failed.");
            return null;
        }

        try {
            // Use SAAJ to convert Document to SOAPElement
            // Create SoapMessage
            SOAPMessage message = createSOAPMessage();
            SOAPBody soapBody = message.getSOAPBody();
            // This returns the SOAPBodyElement
            // that contains ONLY the Payload
            SOAPElement payload = soapBody.addDocument(doc);
            if (payload == null) {
                LOGGER.warn("Converting XML string to SOAPElement failed.");
            } else {
                LOGGER.debug("Converting XML string to SOAPElement succeeded.");
            }
            return payload;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            LOGGER.warn("Converting XML document to SOAPElement failed.");
            return null;
        }
    }

    /**
     * Converts the given XML string to XML document. If the conversion fails,
     * null is returned.
     *
     * @param xml XML string to be converted
     * @return XML document
     */
    public static Document xmlStrToDoc(String xml) {
        LOGGER.debug("Convert XML string to XML document.");
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        builderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        builderFactory.setNamespaceAware(true);
        InputStream stream;
        Document doc;
        try {
            stream = new ByteArrayInputStream(xml.getBytes());
            doc = builderFactory.newDocumentBuilder().parse(stream);
            LOGGER.debug("Converting XML string to XML document succeeded.");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            LOGGER.warn("Converting XML string to XML document failed.");
            return null;
        }
        return doc;
    }

    /**
     * Removes all the child nodes from the given node.
     *
     * @param node node to be modified
     */
    public static void removeAllChildren(Node node) {
        while (node.hasChildNodes()) {
            node.removeChild(node.getFirstChild());
        }
    }

    /**
     * Move all the children under from SOAPElement to under to SOAPElement. If
     * updateNamespaceAndPrefix is true and from elements do not have namespace
     * URI yet, to elements namespace URI and prefix are recursively copied to
     * them.
     *
     * @param from                     source element
     * @param to                       target element
     * @param updateNamespaceAndPrefix should elements namespace URI and prefix
     *                                 be applied to all the copied elements if they do not have namespace URI
     *                                 yet
     * @throws SOAPException if there's an error
     */
    public static void moveChildren(SOAPElement from, SOAPElement to, boolean updateNamespaceAndPrefix)
            throws SOAPException {
        NodeList children = from.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = (Node) children.item(i);
            if (updateNamespaceAndPrefix && (child.getNamespaceURI() == null || child.getNamespaceURI().isEmpty())) {
                child = updateNamespaceAndPrefix(child, to.getNamespaceURI(), to.getPrefix());
                updateNamespaceAndPrefix(child.getChildNodes(), to.getNamespaceURI(), to.getPrefix());
            }

            child.setParentElement(to);

            if (!(child instanceof ElementImpl)) {
                LOGGER.info("Could not remove potentially wrong default namespace from childElement ${child}");
                continue;
            }
            // workaround for backwards compatible behaviour due to implementation changes in jakarta.xml.soap
            if (((ElementImpl) child).getNamespaceURI("") == null) {
                // Remove default namespace of child, that we just added, for backwards compatibility
                ((SOAPElement) to.getLastChild()).removeNamespaceDeclaration("");
            }
        }
    }

    /**
     * Updates the namespace URI and prefix of all the nodes in the list, if
     * node does not have namespace URI yet. The list is updated recursively, so
     * also the children of children (and so on) will be updated.
     *
     * @param list      list of nodes to be updated
     * @param namespace target namespace
     * @param prefix    target prefix
     */
    public static void updateNamespaceAndPrefix(NodeList list, String namespace, String prefix) throws SOAPException {
        for (int i = 0; i < list.getLength(); i++) {
            Node node = (Node) list.item(i);
            if (node.getNamespaceURI() == null || node.getNamespaceURI().isEmpty()) {
                node = updateNamespaceAndPrefix(node, namespace, prefix);
            }
            updateNamespaceAndPrefix(node.getChildNodes(), namespace, prefix);
        }
    }

    /**
     * Updates the namespace URI and prefix of the given node with the given
     * values. If prefix is null or empty, only namespace URI is updated.
     *
     * @param node      Node to be updated
     * @param namespace target namespace
     * @param prefix    target prefix
     * @return updated Node
     * @throws SOAPException if renaming xml node throws DOMException
     */
    public static Node updateNamespaceAndPrefix(Node node, String namespace, String prefix) throws SOAPException {
        try {
            if (!(node.getNodeType() == ELEMENT_NODE)) {
                return node;
            }
            ElementImpl elementImpl = (ElementImpl) node;
            if (prefix != null && !prefix.isEmpty()) {
                node = (Node) node.getOwnerDocument().renameNode(elementImpl.getDomElement(), namespace, prefix + ":" + node.getLocalName());
            } else if (namespace != null && !namespace.isEmpty()) {
                node = (Node) node.getOwnerDocument().renameNode(elementImpl.getDomElement(), namespace, node.getLocalName());
            }
            return node;
        } catch (DOMException e) {
            throw new SOAPException("Unable to update namespace and prefix", e);
        }
    }

    /**
     * Reads installed X-Road packages and their version info from environmental
     * monitoring metrics.
     *
     * @param metrics NodeList containing "metricSet" element returned by
     *                environmental monitoring service
     * @return installed X-Road packages as key-value pairs
     */
    public static Map<String, String> getXRdVersionInfo(NodeList metrics) {
        LOGGER.trace("Start reading X-Road version info from metrics.");
        Map<String, String> results = new HashMap<>();
        // Check for null and empty
        if (metrics == null || metrics.getLength() == 0) {
            LOGGER.trace("Metrics set is null or empty.");
            return results;
        }
        // Loop through metrics
        for (int i = 0; i < metrics.getLength(); i++) {
            Node node = SOAPHelper.getNode((Node) metrics.item(i), "name");
            // Jump to next element if this is not Packages
            if (node == null || !Constants.NS_ENV_MONITORING_ELEM_PACKAGES.equals(node.getTextContent())) {
                continue;
            }
            // Loop through packages and add X-Road packages to results
            getXRdPackages(metrics.item(i).getChildNodes(), results);
        }
        LOGGER.trace("Metrics info read. {} X-Road packages found.", results.size());
        return results;
    }

    /**
     * Helper function for creating new SOAP messages
     *
     * @return New SOAP message
     * @throws SOAPException on soap error
     */
    public static SOAPMessage createSOAPMessage() throws SOAPException {
        synchronized (MSG_FACTORY) {
            return MSG_FACTORY.createMessage();
        }
    }

    /**
     * Helper function for creating new SOAP messages
     *
     * @param mimeHeaders needed for creating SOAP message
     * @param is          needed for creating SOAP message
     * @return New SOAP message
     * @throws IOException   on IO error
     * @throws SOAPException on soap error
     */
    public static SOAPMessage createSOAPMessage(MimeHeaders mimeHeaders, InputStream is)
            throws IOException, SOAPException {
        synchronized (MSG_FACTORY) {
            return MSG_FACTORY.createMessage(mimeHeaders, is);
        }
    }

    /**
     * Reads installed X-Road packages and their version info from environmental
     * monitoring metrics.
     *
     * @param packages NodeList containing "metricSet" element which children
     *                 all the installed packages are
     * @param results  Map object for results
     */
    private static void getXRdPackages(NodeList packages, Map<String, String> results) {
        // Loop through packages
        for (int j = 0; j < packages.getLength(); j++) {
            // We're looking for "stringMetric" elements
            if (Constants.NS_ENV_MONITORING_ELEM_STRING_METRIC.equals(packages.item(j).getLocalName())) {
                // Get name and value
                Node name = SOAPHelper.getNode((Node) packages.item(j), "name");
                Node value = SOAPHelper.getNode((Node) packages.item(j), "value");
                // X-Road packages start with "xroad-prefix"
                if (name != null && value != null && name.getTextContent().startsWith("xroad-")) {
                    results.put(name.getTextContent(), value.getTextContent());
                    LOGGER.debug("X-Road package version info found: \"{}\" = \"{}\".", name.getTextContent(),
                            value.getTextContent());
                }
            }
        }
    }

    /**
     * Clones the given SOAP message and removes all the elements from SOAP
     * body. All the SOAP header elements are left as they are. This method can
     * be used for copying SOAP headers from another message.
     *
     * @param source SOAP message to be cloned
     * @return cloned SOAP message with empty SOAP body
     */
    public static SOAPMessage cloneSOAPMsgWithoutBody(SOAPMessage source) {
        try {
            SOAPMessage msg = createSOAPMessage();
            msg.getSOAPPart().setContent(source.getSOAPPart().getContent());
            msg.getSOAPBody().removeContents();
            return msg;
        } catch (SOAPException ex) {
            LOGGER.error(ex.getMessage(), ex);
            return null;
        }
    }

}
