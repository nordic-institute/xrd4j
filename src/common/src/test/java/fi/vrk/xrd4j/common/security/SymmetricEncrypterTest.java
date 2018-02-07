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
package fi.vrk.xrd4j.common.security;

import junit.framework.TestCase;

import java.security.Key;
import java.security.NoSuchAlgorithmException;

/**
 * Test cases for SymmetricEncrypter class. The cases cover decryption too.
 *
 * @author Petteri Kivimäki
 */
public class SymmetricEncrypterTest extends TestCase {

    /**
     * Test encrypting and decrypting a simple string containing diacritics.
     *
     * @throws NoSuchAlgorithmException
     */
    public void testEncryption1() throws NoSuchAlgorithmException {
        String data = "This is a test string. ÄäÅåÖö Библиотека Каллио";
        Key key = CryptoHelper.generateAESKey(128);
        byte[] iv = CryptoHelper.generateIV();
        Encrypter se = new SymmetricEncrypter(key, iv);
        String encrypted = se.encrypt(data);

        Decrypter sd = new SymmetricDecrypter(key, iv);
        String decrypted = sd.decrypt(encrypted);
        assertEquals(data, decrypted);
    }

    /**
     * Test encrypting and decrypting a complete SOAP message.
     *
     * @throws NoSuchAlgorithmException
     */
    public void testEncryption2() throws NoSuchAlgorithmException {
        String data = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\"><SOAP-ENV:Header><xrd:client id:objectType=\"SUBSYSTEM\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>GOV</id:memberClass><id:memberCode>MEMBER1</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode></xrd:client><xrd:service id:objectType=\"SERVICE\"><id:xRoadInstance>FI</id:xRoadInstance><id:memberClass>COM</id:memberClass><id:memberCode>MEMBER2</id:memberCode><id:subsystemCode>subsystem</id:subsystemCode><id:serviceCode>getRandom</id:serviceCode><id:serviceVersion>v1</id:serviceVersion></xrd:service><xrd:userId>EE1234567890</xrd:userId><xrd:id>ID-1234567890</xrd:id><xrd:protocolVersion>4.0</xrd:protocolVersion><xrd:requestHash algorithmId=\"SHA-512\">ZPbWPAOcJxzE81EmSk//R3DUQtqwMcuMMF9tsccJypdNcukzICQtlhhr3a/bTmexDrn8e/BrBVyl2t0ni/cUvw==</xrd:requestHash></SOAP-ENV:Header><SOAP-ENV:Body><ns1:getRandomResponse xmlns:ns1=\"http://producer.x-road.ee\"><request><data>1234567890</data></request><response><data>9876543210</data></response></ns1:getRandomResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        Key key = CryptoHelper.generateAESKey(128);
        byte[] iv = CryptoHelper.generateIV();
        Encrypter se = new SymmetricEncrypter(key, iv);
        String encrypted = se.encrypt(data);

        Decrypter sd = new SymmetricDecrypter(key, iv);
        String decrypted = sd.decrypt(encrypted);
        assertEquals(data, decrypted);
    }
}
