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
package org.niis.xrd4j.exampleadapter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.xmlunit.assertj3.XmlAssert;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.ComparisonType;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.DifferenceEvaluator;
import org.xmlunit.diff.DifferenceEvaluators;
import org.xmlunit.diff.ElementSelectors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ExampleAdapterIT {

    private static final String ENDPOINT = "/Endpoint";
    private static final String MTOM_ENDPOINT = "/cxf/mtomgenerator";
    private static final String CXF_DEFAULT_MTOM_ENDPOINT = "/services/mtomgenerator";
    private static final String WSDL_QUERY = "?wsdl";
    private static final String PRODUCER_NS = "http://test.x-road.global/producer";
    private static final String REQUEST_HASH = "requestHash";
    private static final String RANDOM_DATA = "data";

    private static final DifferenceEvaluator IGNORE_RANDOM_DATA = (comparison, outcome) ->
            outcome == ComparisonResult.DIFFERENT
                    && comparison.getType() == ComparisonType.TEXT_VALUE
                    && RANDOM_DATA.equals(comparison.getControlDetails().getTarget().getParentNode().getLocalName())
                    ? ComparisonResult.SIMILAR : outcome;

    @Autowired
    private TestRestTemplate rest;

    @ParameterizedTest
    @ValueSource(strings = {"getRandom", "helloService", "listPeople", "personDetails"})
    void serviceResponseMatchesExample(String service) throws IOException {
        ResponseEntity<String> response = post(ENDPOINT, readExample(service + "Request.xml"));

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        XmlAssert.assertThat(response.getBody()).and(readExample(service + "Response.xml"))
                .ignoreWhitespace()
                .ignoreComments()
                .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byName))
                .withNodeFilter(node -> !REQUEST_HASH.equals(node.getLocalName()))
                .withDifferenceEvaluator(DifferenceEvaluators.chain(DifferenceEvaluators.Default, IGNORE_RANDOM_DATA))
                .areSimilar();
    }

    @Test
    void getRandomReturnsNumberBetweenZeroAndHundred() throws IOException {
        ResponseEntity<String> response = post(ENDPOINT, readExample("getRandomRequest.xml"));

        XmlAssert.assertThat(response.getBody())
                .withNamespaceContext(Map.of("ts1", PRODUCER_NS))
                .valueByXPath("//ts1:getRandomResponse/ts1:data")
                .asInt()
                .isBetween(0, 100);
    }

    @Test
    void serviceDescriptionIsServed() {
        assertWsdlServed(ENDPOINT);
    }

    @ParameterizedTest
    @ValueSource(strings = {MTOM_ENDPOINT, CXF_DEFAULT_MTOM_ENDPOINT})
    void mtomServiceDescriptionIsServed(String path) {
        assertWsdlServed(path);
    }

    private void assertWsdlServed(String path) {
        ResponseEntity<String> response = rest.getForEntity(path + WSDL_QUERY, String.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).contains("<wsdl:definitions");
    }

    private ResponseEntity<String> post(String path, String body) {
        return rest.exchange(RequestEntity.post(path).contentType(MediaType.TEXT_XML).body(body), String.class);
    }

    private static String readExample(String fileName) throws IOException {
        return Files.readString(Paths.get("examples", fileName));
    }
}
