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

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;

/**
 * This class implements symmetric encryption. The same key and initialization
 * vector (IV) must be used for both encryption and decryption. This means that
 * sender and receiver must exchange the key and initialization vector in
 * addition to the data. The key and IV can be sent together with the data if
 * they are encrypted using asymmetric encryption.
 *
 * @author Petteri Kivimäki
 */
public class SymmetricEncrypter extends AbstractEncrypter implements Encrypter {

    private final Key key;
    private final byte[] iv;
    private String transformation;

    /**
     * Constructs and initializes a new SymmetricEncrypter object. The default
     * transformation is "AES/CBC/PKCS5Padding".
     *
     * @param key Key object that's used for encryption
     * @param iv initialization vector for encryption. The same initialization
     * vector must be used for the decryption too.
     */
    public SymmetricEncrypter(Key key, byte[] iv) {
        this.key = key;
        this.iv = iv;
        this.transformation = "AES/CBC/PKCS5Padding";
    }

    /**
     * Constructs and initializes a new SymmetricEncrypter object.
     *
     * @param key Key object that's used for encryption
     * @param iv initialization vector for encryption. The same initialization
     * vector must be used for the decryption too.
     * @param transformation transformation that the cipher uses
     */
    public SymmetricEncrypter(Key key, byte[] iv, String transformation) {
        this.key = key;
        this.iv = iv;
        this.transformation = transformation;
    }

    /**
     * Encrypts the given byte array using "AES/CBC/PKCS5Padding" cipher.
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
    protected byte[] encrypt(byte[] plaintext) throws NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(this.transformation);
        cipher.init(Cipher.ENCRYPT_MODE, this.getKey(), new IvParameterSpec(this.getIv()));
        return cipher.doFinal(plaintext);
    }

    /**
     * Returns the AES key key that's used for encryption.
     *
     * @return AES key key that's used for encryption
     */
    public Key getKey() {
        return key;
    }

    /**
     * Returns the initialization vector (IV) that's used for encryption.
     *
     * @return initialization vector (IV) that's used for encryption.
     */
    public byte[] getIv() {
        return iv;
    }
}
