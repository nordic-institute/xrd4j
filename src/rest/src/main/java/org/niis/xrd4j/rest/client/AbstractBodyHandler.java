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

import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * This abstract class offers method for adding HTTP request body to the HTTP
 * request. Classes implementing POST, PUT or DELETE requests can extend this
 * class.
 *
 * @author Petteri Kivimäki
 */
public abstract class AbstractBodyHandler extends AbstractClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBodyHandler.class);

    /**
     * Builds a new StringEntity object that's used as HTTP request body.
     * Content type of the request is set according to the given headers. If the
     * given headers do not contain Content-Type header, "application/xml" is
     * used. If the given request body is null or empty, null is returned.
     *
     * @param requestBody request body
     * @param headers HTTP headers to be added to the request
     * @return new StringEntity object or null
     */
    protected StringEntity buildRequestEntity(String requestBody, Map<String, String> headers) {
        String contentTypeHeader = "Content-Type";
        LOGGER.debug("Build new request entity.");

        // If request body is not null or empty
        if (requestBody != null && !requestBody.isEmpty()) {
            LOGGER.debug("Request body found.");
            // Set content type of the request, default is "application/xml"
            String reqContentType = "application/xml";
            if (headers != null && !headers.isEmpty()) {
                if (headers.get(contentTypeHeader) != null && !headers.get(contentTypeHeader).isEmpty()) {
                    reqContentType = headers.get(contentTypeHeader);
                } else {
                    LOGGER.warn("\"Content-Type\" header is missing. Use \"application/xml\" as default.");
                    // No value set, use default value
                    headers.put(contentTypeHeader, reqContentType);
                }
            }
            // Create request entity that's used as request body
            return new StringEntity(requestBody, ContentType.create(reqContentType, UTF_8));
        }
        LOGGER.debug("No request body found for request. Null is returned");
        return null;
    }
}
