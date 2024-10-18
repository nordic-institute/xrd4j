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
package org.niis.xrd4j.client.util;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocketFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;

@WireMockTest(httpsEnabled = true)
class ClientUtilTest {

    private SSLSocketFactory defaultSSLSocketFactory;
    private HostnameVerifier defaultHostnameVerifier;

    @BeforeEach
    void setUp() {
        defaultSSLSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory();
        defaultHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
    }

    @AfterEach
    void tearDown() {
        HttpsURLConnection.setDefaultSSLSocketFactory(defaultSSLSocketFactory);
        HttpsURLConnection.setDefaultHostnameVerifier(defaultHostnameVerifier);
    }


    @Test
    void certificateNotTrusted(WireMockRuntimeInfo wm) throws Exception {
        stubFor(get("/").willReturn(ok()));

        URL url = new URL(wm.getHttpsBaseUrl());
        var connection = (HttpURLConnection) url.openConnection();

        Assertions.assertThatThrownBy(connection::getResponseCode)
                .isInstanceOf(SSLHandshakeException.class)
                .hasMessageStartingWith("PKIX path building failed");

    }

    @Test
    void doTrustToCertificates(WireMockRuntimeInfo wm) throws Exception {
        stubFor(get("/").willReturn(ok()));

        ClientUtil.doTrustToCertificates();

        var responseCode = getResponseCode(wm.getHttpsBaseUrl());

        assertThat(responseCode).isEqualTo(SC_OK);
    }

    private int getResponseCode(String url) throws IOException {
        var connection = (HttpURLConnection) new URL(url).openConnection();
        return connection.getResponseCode();
    }

}
