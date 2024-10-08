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

import org.niis.xrd4j.common.exception.XRd4JException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * This class offers some helper methods for SOAP client.
 *
 * @author Petteri Kivimäki
 */
public final class ClientUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientUtil.class);

    /**
     * Constructs and initializes a new ClientUtil object. Should never be used.
     */
    private ClientUtil() {
    }

    /**
     * Calling this method creates a trust manager that does not validate
     * certificate chains like the default. In practice, to access a HTTPS URL
     * without having the certificate in the truststore.
     *
     * This method is for testing purposes only, and it must not be used in
     * production. This method should only be used in scenarios, where it is not
     * possible to install the required certificates using keytool e.g. local
     * testing with temporary certificates.
     *
     * @throws XRd4JException if there's an error
     */
    public static void doTrustToCertificates() throws XRd4JException {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                    // perform no validation, trust everyone
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                    // perform no validation, trust everyone
                }
            }
        };

        try {
            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HostnameVerifier hv = (String urlHostName, SSLSession session) -> {
                if (!urlHostName.equalsIgnoreCase(session.getPeerHost())) {
                    LOGGER.warn("Warning: URL host \"{}\" is different to SSLSession host \"{}\".", urlHostName, session.getPeerHost());
                    return false;
                }
                return true;
            };
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
            // Now you it's possible to acces a https URL without having the
            // certificate in the truststore
        } catch (NoSuchAlgorithmException | KeyManagementException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new XRd4JException(ex.getMessage());
        }

    }

    /**
     * This methods overrides the default host name verifier that can cause
     * problems with self signed or untrusted SSL certificates. The problem
     * occurs when the common name (CN) in the certificate doesn't match the
     * host name of service URL. The default host name verifier checks if a host
     * name matches the names stored inside the server's X.509 certificate. This
     * custom verifier verifies that the host name is an acceptable match with
     * the server's authentication scheme.
     *
     * This approach poses some security risks. Therefore, be mindful of that
     * especially if you consider this solution for a production deployment.
     */
    public static void setCustomHostNameVerifier() {
        HostnameVerifier hv = (String urlHostName, SSLSession session) -> {
            if (!urlHostName.equalsIgnoreCase(session.getPeerHost())) {
                LOGGER.warn("Warning: URL host \"{}\" is different to SSLSession host \"{}\".", urlHostName, session.getPeerHost());
                return false;
            }
            return true;
        };
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }
}
