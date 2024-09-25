package org.niis.xrd4j.inttest;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlunit.assertj3.XmlAssert;
import org.xmlunit.placeholder.PlaceholderDifferenceEvaluator;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;

import static org.apache.catalina.startup.Tomcat.addServlet;
import static org.assertj.core.api.Assertions.assertThat;

public class ServletTest {
    private static final Logger logger = LoggerFactory.getLogger(ServletTest.class);

    @TempDir
    private static Path tomcatBaseDir;
    private static Tomcat tomcat;
    private static int serverPort;

    @BeforeAll
    static void startTomcat() throws LifecycleException {
        logger.info("Starting server");
        tomcat = new Tomcat();
        tomcat.setBaseDir(tomcatBaseDir.toString());
        tomcat.setPort(0);
        Context context = tomcat.addContext("", new File(".").getAbsolutePath());

        addServlet(context, "TestServlet", new TestServlet()).addMapping("/");

        tomcat.start();
        serverPort = tomcat.getConnector().getLocalPort();
        logger.info("Server started at port {}", serverPort);
    }

    @AfterAll
    static void stopTomcat() throws LifecycleException {
        tomcat.stop();
        logger.info("Server stopped");
    }


    @Test
    void successfulRequest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "text/xml")
                .uri(getServerUri())
                .POST(HttpRequest.BodyPublishers.ofFile(Path.of("src/integrationTest/resources/test-data/hello-request.xml")))
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        XmlAssert.assertThat(response.body())
                .and(Path.of("src/integrationTest/resources/test-data/hello-response.xml"))
                .ignoreWhitespace()
                .areIdentical();

    }

    @Test
    void invalidContentType() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "invalid")
                .uri(getServerUri())
                .POST(HttpRequest.BodyPublishers.ofFile(Path.of("src/integrationTest/resources/test-data/hello-request.xml")))
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);

        assertSoapFaultClientError(response, "Invalid content type : \"invalid\".");

    }

    @Test
    void noContentType() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                // no content type in request
                .uri(getServerUri())
                .POST(HttpRequest.BodyPublishers.ofFile(Path.of("src/integrationTest/resources/test-data/hello-request.xml")))
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertSoapFaultClientError(response, "Invalid content type : \"null\".");

    }

    private void assertSoapFaultClientError(HttpResponse<String> response, String expected) {
        XmlAssert.assertThat(response.body())
                .and(Path.of("src/integrationTest/resources/test-data/fault-client-error.xml"))
                .ignoreWhitespace()
                .withDifferenceEvaluator(new PlaceholderDifferenceEvaluator())
                .areIdentical();

        XmlAssert.assertThat(response.body())
                .valueByXPath("//faultstring")
                .isEqualTo(expected);
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
        XmlAssert.assertThat(response.body())
                .and(Path.of("src/integrationTest/resources/test-servlet.wsdl"))
                .ignoreWhitespace()
                .areIdentical();

    }

    private URI getServerUri() {
        return URI.create("http://localhost:" + serverPort + "/");
    }


}
