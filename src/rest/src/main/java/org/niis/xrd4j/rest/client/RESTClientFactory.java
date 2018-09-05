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
package org.niis.xrd4j.rest.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a factory class that's responsible for creating REST client objects
 * according to the given parameters.
 *
 * @author Petteri Kivimäki
 */
public final class RESTClientFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(RESTClientFactory.class);
    private static final boolean USE_PROXY = true;
    private static final boolean NO_PROXY = false;

    /**
     * Constructs and initializes a new RESTClientFactory object. Should never
     * be used.
     */
    private RESTClientFactory() {
    }

    /**
     * Creates a new RESTClient object matching the given HTTP verb. If no
     * matching RESTClient is found, null is returned.
     *
     * @param httpVerb HTTP verb (GET, POST, PUT, DELETE)
     * @return RESTClient object matching the given HTTP verb or null
     */
    public static RESTClient createRESTClient(String httpVerb) {
        return createRESTClient(httpVerb, NO_PROXY, null, -1);
    }

    /**
     * Creates a new RESTClient object matching the given HTTP verb. If no
     * matching RESTClient is found, null is returned.
     *
     * RESTClient will use a http proxy.
     *
     * @param httpVerb HTTP verb (GET, POST, PUT, DELETE)
     * @param proxyHost proxy host
     * @param proxyPort proxy port
     * @return RESTClient object matching the given HTTP verb or null
     */
    public static RESTClient createRESTClient(String httpVerb, String proxyHost, int proxyPort) {
        return createRESTClient(httpVerb, USE_PROXY, proxyHost, proxyPort);
    }


    private static RESTClient createRESTClient(String httpVerb,
                                               boolean useProxy,
                                               String proxyHost,
                                               int proxyPort) {
        if (httpVerb == null || httpVerb.isEmpty()) {
            LOGGER.warn("HTTP verb can't be null or empty. Null is returned.");
            return null;
        }
        LOGGER.trace("Create new REST client.");
        AbstractClient client = null;
        if ("get".equalsIgnoreCase(httpVerb)) {
            LOGGER.debug("New GET client created.");
            client = new GetClient();
        } else if ("post".equalsIgnoreCase(httpVerb)) {
            LOGGER.debug("New POST client created.");
            client = new PostClient();
        } else if ("put".equalsIgnoreCase(httpVerb)) {
            LOGGER.debug("New PUT client created.");
            client = new PutClient();
        } else if ("delete".equalsIgnoreCase(httpVerb)) {
            LOGGER.debug("New DELETE client created.");
            client = new DeleteClient();
        } else {
            LOGGER.warn("Unable to create a new REST client. Invalid HTTP verb : \"{}\". Null is returned.", httpVerb);
            return null;
        }
        if (useProxy) {
            client.setProxy(proxyHost, proxyPort);
        }
        return client;
    }

}
