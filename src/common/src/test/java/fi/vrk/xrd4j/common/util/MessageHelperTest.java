/**
 * The MIT License
 * Copyright © 2017 Population Register Centre (VRK)
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
package fi.vrk.xrd4j.common.util;

import junit.framework.TestCase;

/**
 * Test cases for MessageHelper class. Test values have been calculated using
 * the site below.
 *
 * http://www.webutils.pl/SHA1_Calculator
 *
 * @author Petteri Kivimäki
 */
public class MessageHelperTest extends TestCase {

    /**
     * Test for calculating Base64 encoded hash using SHA-512 algorithm.
     */
    public void testCalculateHash() {
        assertEquals("7iaw3Ur350mqGo7jwQrpkj9hiYB3Lkc/iBml1JQODbJ6wYX4oOHV+E+IvIh/1nsUNzLDBMxfqa2Ob1f1ACio/w==", MessageHelper.calculateHash("test"));
        assertEquals("VxRyq5GZc5xJZQFWKZsUobdzXqg8lwR9Cponu5m6XJgVMjhGVbxdbZBYEsQETnwIgsscb2h8j9rKvm40yqiuPg==", MessageHelper.calculateHash("string hash test"));
    }
}
