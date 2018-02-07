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

import fi.vrk.xrd4j.common.member.ConsumerMember;
import fi.vrk.xrd4j.common.member.ObjectType;
import fi.vrk.xrd4j.common.member.ProducerMember;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * This class offers some helper methods for handling ServiceRequest and
 * ServiceResponse messages.
 *
 * @author Petteri Kivimäki
 */
public final class MessageHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageHelper.class);

    /**
     * Constructs and initializes a new MessageHelper object. Should never be
     * used.
     */
    private MessageHelper() {
    }

    /**
     * Returns the ObjectType of the given ConsumerMember object. The object
     * type is member if subsystem code is not defined. If subsystem code is
     * defined, the object type is subsystem.
     *
     * @param consumer the ConsumerMember object which ObjectType needs to be
     * fetched.
     * @return ObjectType of the given ConsumerMember, member or subsystem
     */
    public static ObjectType getObjectType(ConsumerMember consumer) {
        if (consumer.getSubsystemCode() == null || consumer.getSubsystemCode().isEmpty()) {
            return ObjectType.MEMBER;
        }
        return ObjectType.SUBSYSTEM;
    }

    /**
     * Returns the ObjectType of the given ProducerMember object. The object
     * type is central service if member class is not defined. If member class
     * is defined, the object type is service.
     *
     * @param producer the ProducerMember object which ObjectType needs to be
     * fetched.
     * @return ObjectType of the given ProducerMember, central service or
     * service
     */
    public static ObjectType getObjectType(ProducerMember producer) {
        if (producer.getMemberClass() == null) {
            return ObjectType.CENTRALSERVICE;
        }
        return ObjectType.SERVICE;
    }

    /**
     * Generates a universally unique identifier and returns it as a string.
     *
     * @return universally unique identifier as a string
     */
    public static String generateId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Parses the string argument as a signed decimal integer. If parsing of the
     * string fails, zero is returned.
     *
     * @param source a String containing the int representation to be parsed
     * @return the integer value represented by the argument in decimal
     */
    public static int strToInt(String source) {
        try {
            return Integer.parseInt(source);
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

    /**
     * Converts the given string to boolean. The comparison is case insensitive.
     *
     * @param source boolean value as a string
     * @return boolean value
     */
    public static boolean strToBool(String source) {
        if (source == null || source.isEmpty()) {
            return false;
        }
        return "true".equalsIgnoreCase(source);
    }

    /**
     * Calculates Base64 encoded hash of the given data string. SHA-512
     * algorithm is used for calculating the hash.
     *
     * @param data string to be hashed
     * @return Base64 encoded hash of the given data string
     */
    public static String calculateHash(String data) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-512");

            // must specify "UTF-8" encoding
            sha.update(data.getBytes("UTF-8"));
            byte[] hashedByteArray = sha.digest();

            // Use Base64 encoding here -->
            return DatatypeConverter.printBase64Binary(hashedByteArray);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            LOGGER.error(ex.getMessage(), ex);
            return null;
        }
    }

    /**
     * Decodes the given base64 coded image string to image.
     *
     * @param imgStr base64 coded image string to decode
     * @return decoded image
     */
    public static BufferedImage decodeStr2Image(String imgStr) {

        BufferedImage image = null;
        byte[] imageByte;
        try {
            imageByte = DatatypeConverter.parseBase64Binary(imgStr);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return image;
    }

    /**
     * Encodes the given image to a base64 coded string.
     *
     * @param image image to encode
     * @param type type of the image: jpeg, bmp, png, gif etc.
     * @return encoded string
     */
    public static String encodeImg2String(BufferedImage image, String type) {
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, type, bos);
            byte[] imageBytes = bos.toByteArray();
            imageString = DatatypeConverter.printBase64Binary(imageBytes);
            bos.close();
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return imageString;
    }

    /**
     * Encodes the given image InputStream to a base64 coded string.
     *
     * @param is image InputStream to encode
     * @param type type of the image: jpeg, bmp, png, gif etc.
     * @return encoded string
     */
    public static String encodeImg2String(InputStream is, String type) {
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            BufferedImage image = ImageIO.read(is);
            ImageIO.write(image, type, bos);
            byte[] imageBytes = bos.toByteArray();
            imageString = DatatypeConverter.printBase64Binary(imageBytes);
            bos.close();
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return imageString;
    }
}
