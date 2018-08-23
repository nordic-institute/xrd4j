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

import org.niis.xrd4j.rest.ClientResponse;
import org.niis.xrd4j.rest.util.ClientUtil;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * This is an abstract base class for classes implementing GET, POST, PUT and
 * DELETE HTTP clients.
 *
 * @author Petteri Kivimäki
 */
public abstract class AbstractClient implements RESTClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractClient.class);
    public static final int HOVERFLY_PROXY_PORT = 8500;
    public static final int SQUID_PROXY_PORT = 3128;
    public static final int PROXY_PORT = HOVERFLY_PROXY_PORT;
    public static final boolean USE_PROXY = "true".equalsIgnoreCase(System.getProperty("useProducerProxy"));

    /**
     * @param url URL where the request is sent
     * @param requestBody request body
     * @param headers HTTP headers to be added to the request
     * @param config RequestConfig that can be used to set http proxy for the request
     * @return new HttpUriRequest object
     */
    protected abstract HttpUriRequest buildtHttpRequest(String url, String requestBody, Map<String, String> headers, RequestConfig config);

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
    @Override
    public ClientResponse send(String url, String requestBody, Map<String, ?> params, Map<String, String> headers) {
        // Build target URL
        url = ClientUtil.buildTargetURL(url, params);
        // Build request
        RequestConfig config = null;
        if (USE_PROXY && (!url.contains("Consumer") && !url.contains("Provider"))) {
            // use only for outgoing requests, temp hack above ^
            URL parsedUrl;
            try {
                parsedUrl = new URL(url);
            } catch (MalformedURLException e) {
                throw new RuntimeException("Cannot determine proxy request scheme, malformed URL: " + url, e);
            }
            LOGGER.info("using proxy, sending request to: " + url + " with protocol: " + parsedUrl.getProtocol());
            LOGGER.info("trustStore: {}", System.getProperty("javax.net.ssl.trustStore"));
            LOGGER.info("trustStoreType: {}", System.getProperty("javax.net.ssl.trustStoreType"));
            LOGGER.info("trustStorePassword: {}", System.getProperty("javax.net.ssl.trustStorePassword"));
//            HttpHost proxy = new HttpHost("127.0.0.1", PROXY_PORT, parsedUrl.getProtocol());
            HttpHost proxy = new HttpHost("127.0.0.1", PROXY_PORT, "http");
            HttpHost proxy2 = new HttpHost("127.0.0.1", PROXY_PORT);
            config = RequestConfig.custom()
                    .setProxy(proxy)
                    .build();
            LOGGER.info("request body: " + requestBody);
        } else {
            LOGGER.info("not using proxy for: " + url);
        }

        HttpUriRequest request = this.buildtHttpRequest(url, requestBody, headers, config);

        LOGGER.info("Starting HTTP {} operation.", request.getMethod());

        // Add headers
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                LOGGER.debug("Add header : \"{}\" = \"{}\"", entry.getKey(), entry.getValue());
                request.setHeader(entry.getKey(), entry.getValue());
            }
        }


        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
//        try (CloseableHttpClient httpClient = createClient(url.startsWith("https"))) {

            //Send the request; It will immediately return the response in HttpResponse object
            CloseableHttpResponse response = httpClient.execute(request);
            // Get Content-Type header
            Header[] contentTypeHeader = response.getHeaders("Content-Type");
            String contentType = null;
            // Check for null and empty
            if (contentTypeHeader != null && contentTypeHeader.length > 0) {
                contentType = contentTypeHeader[0].getValue();
            }
            // Get Status Code
            int statusCode = response.getStatusLine().getStatusCode();
            // Get reason phrase
            String reasonPhrase = response.getStatusLine().getReasonPhrase();

            // Get response payload
            String responseStr = ClientUtil.getResponseString(response.getEntity());

            if (USE_PROXY) {
                LOGGER.info("response body from proxy: " + responseStr);
            }

            response.close();
            httpClient.close();
            LOGGER.debug("REST response content type: \"{}\".", contentType);
            LOGGER.debug("REST response status code: \"{}\".", statusCode);
            LOGGER.debug("REST response reason phrase: \"{}\".", reasonPhrase);
            LOGGER.debug("REST response : \"{}\".", responseStr);
            LOGGER.info("HTTP {} operation completed.", request.getMethod());
            return new ClientResponse(responseStr, contentType, statusCode, reasonPhrase);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            LOGGER.warn("HTTP {} operation failed. An empty string is returned.", request.getMethod());
            return null;
        }
    }

//    private CloseableHttpClient createClient(boolean ssl) {
//        if (ssl) {
//            LOGGER.info("creating trust-all-certs HttpClient");
//            try {
//                TrustStrategy trustStrategy = new TrustStrategy() {
//                    @Override
//                    public boolean isTrusted(X509Certificate[] chain, String authType) {
//                        return true;
//                    }
//                };
//                HostnameVerifier hostnameVerifier = new HostnameVerifier() {
//                    @Override
//                    public boolean verify(String hostname, SSLSession session) {
//                        return true;
//                    }
//                };
//                return HttpClients.custom()
//                        .setSSLSocketFactory(new SSLConnectionSocketFactory(
//                                new SSLContextBuilder().loadTrustMaterial(trustStrategy).build(),
//                                hostnameVerifier))
//                        .build();
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        } else {
//            LOGGER.info("creating default HttpClient");
//            return HttpClients.createDefault();
//        }
//    }
}
