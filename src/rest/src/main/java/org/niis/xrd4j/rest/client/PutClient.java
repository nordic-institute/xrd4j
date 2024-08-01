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

import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * This class offers a REST client for HTTP PUT requests.
 *
 * @author Petteri Kivimäki
 */
public class PutClient extends AbstractBodyHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(PutClient.class);

    /**
     * Builds a new HTTP PUT request with the given URL and request body.
     * Content type of the request is set according to the given headers. If the
     * given headers do not contain Content-Type header, "application/xml" is
     * used.
     *
     * @param url URL where the request is sent
     * @param requestBody request body
     * @param headers HTTP headers to be added to the request
     * @return new HttpUriRequest object
     */
    @Override
    protected HttpUriRequest buildtHttpRequest(String url, String requestBody, Map<String, String> headers, RequestConfig config) {
        LOGGER.debug("Build new HTTP PUT request.");
        HttpPut request = new HttpPut(url);
        // Create request entity that's used as request body
        StringEntity requestEntity = super.buildRequestEntity(requestBody, headers);
        request.setConfig(config);
        if (requestEntity != null) {
            request.setEntity(requestEntity);
        } else {
            LOGGER.debug("No request body found for HTTP PUT request.");
        }
        return request;
    }
}
