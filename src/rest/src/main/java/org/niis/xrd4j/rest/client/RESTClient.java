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
package org.niis.xrd4j.rest.client;

import org.niis.xrd4j.rest.ClientResponse;

import java.util.Map;

/**
 * This class defines an interface for all the REST client classes that
 * implement GET, POST, PUT and DELETE clients.
 *
 * @author Petteri Kivimäki
 */
@FunctionalInterface
public interface RESTClient {

    /**
     * Makes a HTTP request to the given URL using the given request body,
     * parameters and HTTP headers. The parameters are used as URL parameters,
     * but if there's a parameter "resourceId", it's added directly to the end
     * of the URL. If there's no request body, the value can be null.
     *
     * @param url URL where the request is sent
     * @param params request parameters
     * @param requestBody request body
     * @param headers HTTP headers to be added to the request
     * @return response as string
     */
    ClientResponse send(String url, String requestBody, Map<String, ?> params, Map<String, String> headers);
}
