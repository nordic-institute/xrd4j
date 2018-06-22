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

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

/**
 * This class implements asymmetric decryption. The decryption should be made
 * using the private key of the receiver. The sender has encrypted the data
 * using the public key of the receiver.
 *
 * @author Petteri Kivimäki
 */
public class AsymmetricDecrypter extends AbstractDecrypter implements Decrypter {

    /**
     * Private key that's used for decryption.
     */
    private final PrivateKey privateKey;
    /**
     * Transformation that the cipher uses, e.g. "RSA/ECB/PKCS1Padding"
     */
    private String transformation;

    /**
     * Constructs and initializes a new AsymmetricDecrypter object. During the
     * initialization the private key used for decryption is fetched from the
     * defined key store. If fetching the key fails for some reason, an
     * exception is thrown. The default transformation is
     * "RSA/ECB/PKCS1Padding".
     *
     * @param path absolute path of the key store file
     * @param storePassword password of the key store
     * @param privateKeyAlias alias of the private key in the key store
     * @param keyPassword password of the private key
     * @throws KeyStoreException if there's an error
     * @throws IOException if there's an error
     * @throws NoSuchAlgorithmException if there's an error
     * @throws CertificateException if there's an error
     * @throws UnrecoverableEntryException if there's an error
     */
    public AsymmetricDecrypter(String path, String storePassword, String privateKeyAlias, String keyPassword)
        throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableEntryException {
        this.privateKey = CryptoHelper.getPrivateKey(path, storePassword, privateKeyAlias, keyPassword);
        this.transformation = "RSA/ECB/PKCS1Padding";
    }

    /**
     * Constructs and initializes a new AsymmetricDecrypter object. During the
     * initialization the private key used for decryption is fetched from the
     * defined key store. If fetching the key fails for some reason, an
     * exception is thrown.
     *
     * @param path absolute path of the key store file
     * @param storePassword password of the key store
     * @param privateKeyAlias alias of the private key in the key store
     * @param keyPassword password of the private key
     * @param transformation transformation that the cipher uses
     * @throws KeyStoreException if there's an error
     * @throws IOException if there's an error
     * @throws NoSuchAlgorithmException if there's an error
     * @throws CertificateException if there's an error
     * @throws UnrecoverableEntryException if there's an error
     */
    public AsymmetricDecrypter(String path, String storePassword, String privateKeyAlias, String keyPassword,
                               String transformation) throws KeyStoreException, IOException, NoSuchAlgorithmException,
        CertificateException, UnrecoverableEntryException {
        this(path, storePassword, privateKeyAlias, keyPassword);
        this.transformation = transformation;
    }

    /**
     * Decrypts the given byte array using "RSA/ECB/PKCS1Padding" cipher.
     *
     * @param cipherText byte array to be decrypted
     * @return decrypted byte array
     * @throws NoSuchAlgorithmException if there's an error
     * @throws InvalidKeyException if there's an error
     * @throws InvalidAlgorithmParameterException if there's an error
     * @throws NoSuchPaddingException if there's an error
     * @throws IllegalBlockSizeException if there's an error
     * @throws BadPaddingException if there's an error
     */
    @Override
    protected byte[] decrypt(byte[] cipherText) throws NoSuchAlgorithmException, InvalidKeyException,
        InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(this.transformation);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(cipherText);
    }

    /**
     * Returns the private key used by this decrypter.
     *
     * @return the private key used by this decrypter
     */
    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }
}
