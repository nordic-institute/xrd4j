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

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;

@WireMockTest
class AbstractClientTest {

    private static final String PATH = "/items";
    private static final String BODY = "ÄäÅåÖö";
    private static final String CONTENT_TYPE = "text/plain; charset=UTF-8";

    @Test
    void getReturnsStatusReasonContentTypeAndUtf8Body(WireMockRuntimeInfo wm) {
        // No charset in the header: the body must be read as UTF-8 regardless
        stubFor(get(PATH).willReturn(aResponse()
                .withStatus(200)
                .withStatusMessage("All good")
                .withHeader("Content-Type", "text/plain")
                .withBody(BODY.getBytes(StandardCharsets.UTF_8))));

        ClientResponse response = RESTClientFactory.createRESTClient("get").send(wm.getHttpBaseUrl() + PATH, null, null, null);

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getReasonPhrase()).isEqualTo("All good");
        assertThat(response.getContentType()).isEqualTo("text/plain");
        assertThat(response.getData()).isEqualTo(BODY);
    }

    @Test
    void getDecodesBodyWithDeclaredCharset(WireMockRuntimeInfo wm) {
        stubFor(get(PATH).willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "text/plain; charset=ISO-8859-1")
                .withBody(BODY.getBytes(StandardCharsets.ISO_8859_1))));

        ClientResponse response = RESTClientFactory.createRESTClient("get").send(wm.getHttpBaseUrl() + PATH, null, null, null);

        assertThat(response.getData()).isEqualTo(BODY);
    }

    @Test
    void postSendsBodyAndReturnsResponse(WireMockRuntimeInfo wm) {
        stubFor(post(PATH).willReturn(aResponse()
                .withStatus(201)
                .withStatusMessage("Created")
                .withHeader("Content-Type", CONTENT_TYPE)
                .withBody(BODY)));
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        ClientResponse response = RESTClientFactory.createRESTClient("post").send(wm.getHttpBaseUrl() + PATH, "{\"name\":\"" + BODY + "\"}", null, headers);

        assertThat(response.getStatusCode()).isEqualTo(201);
        assertThat(response.getReasonPhrase()).isEqualTo("Created");
        assertThat(response.getContentType()).isEqualTo(CONTENT_TYPE);
        assertThat(response.getData()).isEqualTo(BODY);
        verify(postRequestedFor(urlEqualTo(PATH))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalTo("{\"name\":\"" + BODY + "\"}")));
    }

    @Test
    void nonSuccessStatusStillReturnsResponseWithBody(WireMockRuntimeInfo wm) {
        stubFor(get(PATH).willReturn(aResponse()
                .withStatus(404)
                .withStatusMessage("Not Found")
                .withHeader("Content-Type", CONTENT_TYPE)
                .withBody("missing")));

        ClientResponse response = RESTClientFactory.createRESTClient("get").send(wm.getHttpBaseUrl() + PATH, null, null, null);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(404);
        assertThat(response.getReasonPhrase()).isEqualTo("Not Found");
        assertThat(response.getData()).isEqualTo("missing");
    }

    @Test
    void missingContentTypeHeaderYieldsNullContentType(WireMockRuntimeInfo wm) {
        stubFor(get(PATH).willReturn(aResponse().withStatus(204)));

        ClientResponse response = RESTClientFactory.createRESTClient("get").send(wm.getHttpBaseUrl() + PATH, null, null, null);

        assertThat(response.getStatusCode()).isEqualTo(204);
        assertThat(response.getContentType()).isNull();
        assertThat(response.getData()).isEmpty();
    }

    @Test
    void requestHeadersAndParametersReachServer(WireMockRuntimeInfo wm) {
        stubFor(get(urlEqualTo(PATH + "?key=value")).willReturn(aResponse().withStatus(200)));
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Custom", "custom-value");
        headers.put("Accept", "application/json");
        Map<String, String> params = new HashMap<>();
        params.put("key", "value");

        ClientResponse response = RESTClientFactory.createRESTClient("get").send(wm.getHttpBaseUrl() + PATH, null, params, headers);

        assertThat(response.getStatusCode()).isEqualTo(200);
        verify(getRequestedFor(urlEqualTo(PATH + "?key=value"))
                .withHeader("X-Custom", equalTo("custom-value"))
                .withHeader("Accept", equalTo("application/json")));
    }

    @Test
    void unreachableServerReturnsNull() throws IOException {
        int closedPort;
        try (ServerSocket socket = new ServerSocket(0)) {
            closedPort = socket.getLocalPort();
        }

        ClientResponse response = RESTClientFactory.createRESTClient("get").send("http://localhost:" + closedPort + PATH, null, null, null);

        assertThat(response).isNull();
    }
}
