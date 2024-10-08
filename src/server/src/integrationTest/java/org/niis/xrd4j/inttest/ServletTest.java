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
package org.niis.xrd4j.inttest;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.parser.AbstractContentHandler;
import org.apache.james.mime4j.parser.ContentHandler;
import org.apache.james.mime4j.parser.MimeStreamParser;
import org.apache.james.mime4j.stream.BodyDescriptor;
import org.apache.james.mime4j.stream.MimeConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.platform.commons.util.StringUtils;
import org.opentest4j.AssertionFailedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlunit.assertj3.XmlAssert;
import org.xmlunit.placeholder.PlaceholderDifferenceEvaluator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.catalina.startup.Tomcat.addServlet;
import static org.assertj.core.api.Assertions.assertThat;

public class ServletTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServletTest.class);
    public static final String TEST_DATA_DIR = "src/integrationTest/resources/test-data/";

    @TempDir
    private static Path tomcatBaseDir;
    private static Tomcat tomcat;
    private static int serverPort;

    @BeforeAll
    static void startTomcat() throws LifecycleException {
        LOGGER.info("Starting server");
        tomcat = new Tomcat();
        tomcat.setBaseDir(tomcatBaseDir.toString());
        tomcat.setPort(0);
        Context context = tomcat.addContext("", new File(".").getAbsolutePath());

        addServlet(context, "TestServlet", new ExampleServletImpl()).addMapping("/");

        tomcat.start();
        serverPort = tomcat.getConnector().getLocalPort();
        LOGGER.info("Server started at port {}", serverPort);
    }

    @AfterAll
    static void stopTomcat() throws LifecycleException {
        tomcat.stop();
        LOGGER.info("Server stopped");
    }


    @Test
    void getWSDL() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(getServerUri().resolve("?wsdl"))
                .GET()
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    void successfulRequest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "text/xml")
                .uri(getServerUri())
                .POST(HttpRequest.BodyPublishers.ofFile(testData("hello-request.xml")))
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertResponseSuccess(response, "hello-response.xml");

    }

    @Test
    void multipartRequest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .headers("Content-Type", "multipart/related; start=\"<rootpart>\"; boundary=MIME_boundary")
                .uri(getServerUri())
                .POST(HttpRequest.BodyPublishers.ofFile(testData("store-attachments-request.txt")))
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertResponseSuccess(response, "store-attachments-response.xml");

    }

    @Test
    void multipartResponse() throws IOException, InterruptedException, MimeException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "text/xml")
                .uri(getServerUri())
                .POST(HttpRequest.BodyPublishers.ofFile(testData("get-attachments-request.xml")))
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        assertThat(response.statusCode()).isEqualTo(200);
        var parts = parseMultipart(response);
        assertThat(parts).satisfiesExactly(
                part -> assertXmlIdentical(part, "get-attachments-response.xml"),
                part -> assertThat(part).hasSize(100),
                part -> assertThat(part).hasSize(50)
        );


    }

    private List<String> parseMultipart(HttpResponse<InputStream> response) throws MimeException, IOException {
        var contentType = response.headers().firstValue("Content-Type")
                .orElseThrow(() -> new AssertionFailedError("No Content-Type header"));
        List<String> parts = new ArrayList<>();
        MimeStreamParser mimeStreamParser = new MimeStreamParser(MimeConfig.custom().setHeadlessParsing(contentType).build());
        ContentHandler contentHandler = new AbstractContentHandler() {
            @Override
            public void body(BodyDescriptor bd, InputStream is) throws MimeException, IOException {
                parts.add(new String(is.readAllBytes(), UTF_8));
            }
        };
        mimeStreamParser.setContentHandler(contentHandler);
        mimeStreamParser.parse(response.body());
        return parts;
    }


    @Test
    void invalidServiceCode() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "text/xml")
                .uri(getServerUri())
                .POST(HttpRequest.BodyPublishers.ofFile(testData("invalid-service-code-request.xml")))
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(SC_OK);

        assertSoapFaultClientError(response, "Unknown service code.");
    }

    @Test
    void invalidRequestBody() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "text/xml")
                .uri(getServerUri())
                .POST(HttpRequest.BodyPublishers.ofString("<invalid>xml</invalid>"))
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(SC_OK);

        assertSoapFaultClientError(response, "Invalid X-Road SOAP message. Unable to parse the request.");
    }

    @Test
    void invalidContentType() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "invalid")
                .uri(getServerUri())
                .POST(HttpRequest.BodyPublishers.ofFile(testData("hello-request.xml")))
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(SC_OK);

        assertSoapFaultClientError(response, "Invalid content type : \"invalid\".");
    }

    @Test
    void noContentType() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                // no content type in request
                .uri(getServerUri())
                .POST(HttpRequest.BodyPublishers.ofFile(testData("hello-request.xml")))
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(SC_OK);
        assertSoapFaultClientError(response, "Invalid content type : \"null\".");
    }

    private void assertResponseSuccess(HttpResponse<String> response, String expectedContentFilename) {
        assertThat(response.statusCode()).isEqualTo(SC_OK);
        assertXmlIdentical(response.body(), expectedContentFilename);
    }

    private void assertXmlIdentical(String actualXml, String expectedContentFilename) {
        XmlAssert.assertThat(actualXml)
                .and(testData(expectedContentFilename))
                .ignoreWhitespace()
                .areIdentical();
    }

    private void assertSoapFaultClientError(HttpResponse<String> response, String expected) {
        XmlAssert.assertThat(response.body())
                .and(testData("fault-client-error.xml"))
                .withNodeFilter(node -> StringUtils.isNotBlank(node.getTextContent()))
                .ignoreWhitespace()
                .withDifferenceEvaluator(new PlaceholderDifferenceEvaluator())
                .areIdentical();

        XmlAssert.assertThat(response.body())
                .valueByXPath("//faultstring")
                .isEqualTo(expected);
    }

    private URI getServerUri() {
        return URI.create("http://localhost:" + serverPort + "/");
    }

    private Path testData(String filename) {
        return Path.of(TEST_DATA_DIR + filename);
    }


}
