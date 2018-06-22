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
package fi.vrk.xrd4j.common.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * This abstract class serves as a base class for symmetric and asymmetric
 * encrypter classes. This class implements the encrypt method that takes care
 * of converting the data in the right format before and after the encryption.
 * Child classes have to implement the actual encryption.
 *
 * @author Petteri Kivimäki
 */
public abstract class AbstractEncrypter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractEncrypter.class);

    protected abstract byte[] encrypt(byte[] plaintext) throws NoSuchAlgorithmException, InvalidKeyException,
        InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException;

    /**
     * Encrypts the given string and returns it as a base 64 encoded string.
     *
     * @param plainText string to be encrypted
     * @return encrypted plainText as base 64 encoded string
     */
    public String encrypt(String plainText) {
        try {
            byte[] decrypted = plainText.getBytes(StandardCharsets.UTF_8);
            byte[] encrypted = encrypt(decrypted);
            return CryptoHelper.encodeBase64(encrypted);
        } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException
            | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException ex) {
            LOGGER.error(ex.getMessage(), ex);
            return null;
        }
    }
}
