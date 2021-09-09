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

package org.niis.xrd4j.exampleadapter.data;

/**
 * This class implements a simple POJO for the "contact" complexType defined
 * in the "example.wsdl" file that's located in WEB-INF/classes folder. The name 
 * of the WSDL file and the namespace is configured in the 
 * WEB-INF/classes/xrd-servlet.properties file.
 *
 * @author Raido Kaju
 */

public class PersonContact {
    private PersonContactType type;
    private String name;
    private String value;

    public PersonContact(final PersonContactType type, final String name, final String value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public PersonContactType getType() {
        return type;
    }

    public void setType(final PersonContactType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public enum PersonContactType {
        PHONE("phone"),
        MOBILE("mobile"),
        EMAIL("email");

        private final String name;

        private PersonContactType(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}

