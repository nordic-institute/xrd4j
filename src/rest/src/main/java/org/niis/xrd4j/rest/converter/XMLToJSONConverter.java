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
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class converts XML strings to JSON strings. If XML element names
 * start with '__at__' string, it's converted to '@' character that's used
 * in JSON-LD as the first character in keys.
 *
 * @author Petteri Kivimäki
 * @author Markus Törnqvist
 */
public class XMLToJSONConverter implements Converter {
    private static final String ARRAY = "array";
    private static final Logger LOGGER = LoggerFactory.getLogger(XMLToJSONConverter.class);

    /**
     * Converts the given XML string to JSON string. class.
     *
     * @param data XML string
     * @return JSON string or an empty string if the conversion fails
     */
    @Override
    public String convert(String data) {
        LOGGER.debug("CONVERTING " + data);
        try {
            JSONObject asJson = XML.toJSONObject(data);
            if (asJson.has(ARRAY)) {
                // If the JSON object has an "array" key, it's an array
                JSONArray jsonArray = asJson.getJSONArray(ARRAY);
                LOGGER.debug("RETURN ARRAY " + jsonArray.toString());
                return jsonArray.toString();
            } else {
                // Did not have top-level array key.
                this.normalizeObject(asJson);
                String jsonStr = asJson.toString();
                // JSON-LD uses '@' characters in keys and they're not allowed
                // in XML element names. Replace '__at__' with '@' in keys.
                jsonStr = jsonStr.replaceAll("\"__at__(.+?\"\\s*:)", "\"@$1");
                LOGGER.debug("NORMALIZED TO " + jsonStr);
                return jsonStr;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            LOGGER.warn("Converting XML to JSON failed! An empty String is returned.");
            return "";
        }
    }

    protected JSONObject normalizeObject(JSONObject obj) {
        LOGGER.debug("NORM: " + obj.toString());
        for (String key : JSONObject.getNames(obj)) {
            JSONObject subtree = obj.optJSONObject(key);
            if (subtree != null) {
                if (subtree.has(ARRAY)) {
                    // Set the array as the direct value
                    JSONArray subarray = subtree.getJSONArray(ARRAY);
                    obj.put(key, subarray);
                    LOGGER.debug("recurse with {}: {}", key, subtree.toString());
                }

                // See if there's more to do in this subtree
                normalizeObject(subtree);
            }
        }

        return obj;
    }
}
