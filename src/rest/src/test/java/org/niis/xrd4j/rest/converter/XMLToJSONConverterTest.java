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
package org.niis.xrd4j.rest.converter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test cases for JSONToXMLConverter class.
 *
 * @author Markus Törnqvist
 */
class XMLToJSONConverterTest {

    private Converter converter;

    /**
     * Set up instance variables used in test cases.
     *
     * @throws Exception
     */
    @BeforeEach
    void setUp() throws Exception {
        this.converter = new XMLToJSONConverter();
    }

    /**
     * Test converting single string element.
     */
    @Test
    void testSingleStrElement() {
        String correctJsonString = "{\"key1\":\"value1\"}";
        JSONObject correctJson = new JSONObject(correctJsonString);

        String xml = "<key1>value1</key1>";
        JSONObject json = new JSONObject(this.converter.convert(xml));
        assertEquals(correctJson.toString(), json.toString());
    }

    /**
     * Test converting multiple string elements.
     */
    @Test
    void testMultipleStrElements() {
        String correctJsonString = "{\"key4\":\"value4\",\"key3\":\"value3\",\"key2\":\"value2\",\"key1\":\"value1\"}";
        JSONObject correctJson = new JSONObject(correctJsonString);

        String xml = "<key4>value4</key4><key3>value3</key3><key2>value2</key2><key1>value1</key1>";
        JSONObject json = new JSONObject(this.converter.convert(xml));
        assertEquals(correctJson.toString(), json.toString());
    }

    /**
     * Test converting single string element.
     */
    @Test
    void testSingleIntElement() {
        String correctJsonString = "{\"key1\":1}";
        JSONObject correctJson = new JSONObject(correctJsonString);

        String xml = "<key1>1</key1>";
        JSONObject json = new JSONObject(this.converter.convert(xml));
        assertEquals(correctJson.toString(), json.toString());
    }

    /**
     * Test converting multiple string, int and boolean elements.
     */
    @Test
    void testMultipleElements() {
        String correctJsonString = "{\"key3\":\"value3\",\"key2\":true,\"key1\":1}";
        JSONObject correctJson = new JSONObject(correctJsonString);

        String xml = "<key3>value3</key3><key2>true</key2><key1>1</key1>";
        JSONObject json = new JSONObject(this.converter.convert(xml));
        assertEquals(correctJson.toString(), json.toString());
    }

    /**
     * Test converting nested element.
     */
    @Test
    void testNestedElement() {
        String correctJsonString = "{\"request\":{\"key1\":\"value1\"}}";
        JSONObject correctJson = new JSONObject(correctJsonString);

        String xml = "<request><key1>value1</key1></request>";
        JSONObject json = new JSONObject(this.converter.convert(xml));
        assertEquals(correctJson.toString(), json.toString());
    }

    /**
     * Test converting nested elements.
     */
    @Test
    void testNestedElements1() {
        String correctJsonString = "{\"request\":{\"key3\":3,\"key2\":true,\"key1\":\"value1\"}}";
        JSONObject correctJson = new JSONObject(correctJsonString);

        String xml = "<request><key1>value1</key1><key2>true</key2><key3>3</key3></request>";
        JSONObject json = new JSONObject(this.converter.convert(xml));
        assertEquals(correctJson.toString(), json.toString());
    }

    /**
     * Test converting nested elements.
     */
    @Test
    void testNestedElements2() {
        String correctJsonString = "{\"menu\":{\"id\":\"file\",\"popup\":{\"menuitem\":[{\"value\":\"New\",\"onclick\":\"CreateNewDoc()\"},{\"value\":\"Open\",\"onclick\":\"OpenDoc()\"},{\"value\":\"Close\",\"onclick\":\"CloseDoc()\"}]},\"value\":\"File\"}}";
        JSONObject correctJson = new JSONObject(correctJsonString);

        String xml = "<menu><id>file</id><popup><menuitem><value>New</value><onclick>CreateNewDoc()</onclick></menuitem><menuitem><value>Open</value><onclick>OpenDoc()</onclick></menuitem><menuitem><value>Close</value><onclick>CloseDoc()</onclick></menuitem></popup><value>File</value></menu>";
        JSONObject json = new JSONObject(this.converter.convert(xml));
        assertEquals(correctJson.toString(), json.toString());
    }

    /**
     * Test converting nested elements.
     */
    @Test
    void testNestedElements3() {
        String correctJsonString = "{\"id\":49,\"name_en\":\"City of Espoo\",\"name_sv\":\"Esbo stad\",\"data_source_url\":\"www.espoo.fi\",\"name_fi\":\"Espoon kaupunki\"}";
        JSONObject correctJson = new JSONObject(correctJsonString);

        String xml = "<id>49</id><name_en>City of Espoo</name_en><name_sv>Esbo stad</name_sv><data_source_url>www.espoo.fi</data_source_url><name_fi>Espoon kaupunki</name_fi>";
        JSONObject json = new JSONObject(this.converter.convert(xml));
        assertEquals(correctJson.toString(), json.toString());
    }

    /**
     * Test converting an array.
     */
    @Test
    void testArray() {
        List<String> elements = new ArrayList<>();
        elements.add("\"id\":48");
        elements.add("\"name_fi\":\"Espoon kaupunki\"");
        elements.add("\"name_sv\":\"Esbo stad\"");
        elements.add("\"name_en\":\"City of Espoo\"");
        elements.add("\"data_source_url\":\"www.espoo.fi\"");
        elements.add("\"id\":91");
        elements.add("\"name_fi\":\"Helsingin kaupunki\"");
        elements.add("\"name_sv\":\"Helsingfors stad\"");
        elements.add("\"name_en\":\"City of Helsinki\"");
        elements.add("\"data_source_url\":\"www.hel.fi\"");

        String xml = "<array><id>48</id><name_en>City of Espoo</name_en><name_sv>Esbo stad</name_sv><data_source_url>www.espoo.fi</data_source_url><name_fi>Espoon kaupunki</name_fi></array><array><id>91</id><name_en>City of Helsinki</name_en><name_sv>Helsingfors stad</name_sv><data_source_url>www.hel.fi</data_source_url><name_fi>Helsingin kaupunki</name_fi></array>";

        String jsonString = this.converter.convert(xml);
        JSONArray json = new JSONArray(jsonString);
        jsonString = json.toString();

        if (jsonString.length() != 250) {
            fail();
        }

        for (String element : elements) {
            if (!jsonString.contains(element)) {
                fail();
            }
        }
    }

    /**
     * Test normalizing subject array. This behavior could be considered a bug
     * in org.json.
     */
    @Test
    void testNormalize() {
        // This is what would happen if we didn't normalize
        // String correctJsonString = "{\"DATA\": {\"array\":[\"one\",\"two\",\"three\"]}}";
        String correctJsonString = "{\"DATA\": [\"one\",\"two\",\"three\"]}";
        JSONObject correctJson = new JSONObject(correctJsonString);

        String xml = "<DATA><array>one</array><array>two</array><array>three</array></DATA>";
        String jsonString = this.converter.convert(xml);
        JSONObject json = new JSONObject(jsonString);
        assertEquals(correctJson.toString(), json.toString());
    }

    /**
     * Test normalizing subject arrays. This behavior could be considered a bug
     * in org.json.
     */
    @Test
    void testNormalize1() {
        String correctJsonString = "{\"DATA1\": [\"one\",\"two\",\"three\"], \"DATA2\": [1, 2, 3]}";
        JSONObject correctJson = new JSONObject(correctJsonString);

        String xml = "<DATA1><array>one</array><array>two</array><array>three</array></DATA1>";
        xml += "<DATA2><array>1</array><array>2</array><array>3</array></DATA2>";
        String jsonString = this.converter.convert(xml);
        JSONObject json = new JSONObject(jsonString);
        assertEquals(correctJson.toString(), json.toString());
    }

    /**
     * Test normalizing deep subject arrays. This behavior could be considered a
     * bug in org.json.
     */
    @Test
    void testNormalize2() {
        List<String> elements = new ArrayList<>();
        elements.add("\"one\"");
        elements.add("\"two\"");
        elements.add("\"three\"");
        elements.add("\"realm\"");
        elements.add("1");
        elements.add("2");
        elements.add("3");

        String xml = "<DATA><array>one</array><array>two</array><array>three</array></DATA>";
        xml += "<DEEPDATA><realm><array>1</array><array>2</array><array>3</array></realm></DEEPDATA>";

        String jsonString = this.converter.convert(xml);
        JSONObject json = new JSONObject(jsonString);
        jsonString = json.toString();

        if (jsonString.length() != 59) {
            fail();
        }
        if (!jsonString.matches("^\\{\"DEEPDATA\":.+{16}\\},\"DATA\":.+{21}\\}$") && !jsonString.matches("^\\{\"DATA\":.+{20}],\"DEEPDATA\":.+{16}\\}\\}$")) {
            fail(jsonString);
        }
        for (String element : elements) {
            if (!jsonString.contains(element)) {
                fail();
            }
        }
    }

    /**
     * Test normalizing deep subject arrays. This behavior could be considered a
     * bug in org.json.
     */
    @Test
    void testNormalize3() {
        List<String> elements = new ArrayList<>();
        elements.add("\"one\"");
        elements.add("\"two\"");
        elements.add("\"three\"");
        elements.add("\"realm\"");
        elements.add("1");
        elements.add("2");
        elements.add("3");

        String xml = "<DEEPDATA><realm>1</realm><realm>2</realm><realm>3</realm></DEEPDATA><DATA>one</DATA><DATA>two</DATA><DATA>three</DATA>";

        String jsonString = this.converter.convert(xml);
        JSONObject json = new JSONObject(jsonString);
        jsonString = json.toString();

        if (jsonString.length() != 59) {
            fail();
        }
        if (!jsonString.matches("^\\{\"DEEPDATA\":.+{16}\\},\"DATA\":.+{21}\\}$") && !jsonString.matches("^\\{\"DATA\":.+{20}],\"DEEPDATA\":.+{16}\\}\\}$")) {
            fail(jsonString);
        }
        for (String element : elements) {
            if (!jsonString.contains(element)) {
                fail();
            }
        }
    }

    /**
     * Test converting XML containing JSON-LD to JSON-LD.
     */
    @Test
    void testJSONLD1() {
        List<String> elements = new ArrayList<>();
        elements.add("\"@id\":\"http://dbpedia.org/resource/John_Lennon\"");
        elements.add("\"name\":\"John Lennon\"");
        elements.add("\"@context\":\"http://json-ld.org/contexts/person.jsonld\"");

        String xml = "<__at__context>http://json-ld.org/contexts/person.jsonld</__at__context><name>John Lennon</name><__at__id>http://dbpedia.org/resource/John_Lennon</__at__id>";

        String jsonString = this.converter.convert(xml);
        JSONObject json = new JSONObject(jsonString);
        jsonString = json.toString();

        if (jsonString.length() != 125) {
            fail();
        }
        for (String element : elements) {
            if (!jsonString.contains(element)) {
                fail();
            }
        }
    }
}
