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
package org.niis.xrd4j.common.security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;

/**
 * This class implements asymmetric encryption. The encryption should be made
 * using the public key of the receiver. The receiver then decrypts the data
 * using his private key.
 *
 * @author Petteri Kivimäki
 */
public class AsymmetricEncrypter extends AbstractEncrypter implements Encrypter {

    /**
     * Receivers public key that's used for encryption.
     */
    private final PublicKey publicKey;
    /**
     * Transformation that the cipher uses, e.g. "RSA/ECB/PKCS1Padding"
     */
    private String transformation;

    /**
     * Constructs and initializes a new AsymmetricEncrypter object. During the
     * initialization the public key used for decryption is fetched from the
     * defined trust store. If fetching the key fails for some reason, an
     * exception is thrown.
     *
     * @param path absolute path of the trust store file
     * @param password trust store password
     * @param publicKeyAlias alias of the public key in the trust store
     * @throws KeyStoreException if there's an error
     * @throws IOException if there's an error
     * @throws NoSuchAlgorithmException if there's an error
     * @throws CertificateException if there's an error
     */
    public AsymmetricEncrypter(String path, String password, String publicKeyAlias) throws KeyStoreException,
        IOException, NoSuchAlgorithmException, CertificateException {
        this.publicKey = CryptoHelper.getPublicKey(path, password, publicKeyAlias);
        this.transformation = "RSA/ECB/PKCS1Padding";
    }

    /**
     * Constructs and initializes a new AsymmetricEncrypter object. During the
     * initialization the public key used for decryption is fetched from the
     * defined trust store. If fetching the key fails for some reason, an
     * exception is thrown. The default transformation is
     * "RSA/ECB/PKCS1Padding".
     *
     * @param path absolute path of the trust store file
     * @param password trust store password
     * @param publicKeyAlias alias of the public key in the trust store
     * @param transformation transformation that the cipher uses
     * @throws KeyStoreException if there's an error
     * @throws IOException if there's an error
     * @throws NoSuchAlgorithmException if there's an error
     * @throws CertificateException if there's an error
     */
    public AsymmetricEncrypter(String path, String password, String publicKeyAlias, String transformation)
        throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        this(path, password, publicKeyAlias);
        this.transformation = transformation;
    }

    /**
     * Encrypts the given byte array using "RSA/ECB/PKCS1Padding" cipher.
     *
     * @param plaintext byte array to be encrypted
     * @return encrypted byte array
     * @throws NoSuchAlgorithmException if there's an error
     * @throws InvalidKeyException if there's an error
     * @throws InvalidAlgorithmParameterException if there's an error
     * @throws NoSuchPaddingException if there's an error
     * @throws IllegalBlockSizeException if there's an error
     * @throws BadPaddingException if there's an error
     */
    @Override
    protected byte[] encrypt(byte[] plaintext) throws NoSuchAlgorithmException, InvalidKeyException,
        InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(this.transformation);
        cipher.init(Cipher.ENCRYPT_MODE, this.getPublicKey());
        return cipher.doFinal(plaintext);
    }

    /**
     * Returns the public key used by this encrypter.
     *
     * @return the public key used by this encrypter
     */
    public PublicKey getPublicKey() {
        return this.publicKey;
    }
}
