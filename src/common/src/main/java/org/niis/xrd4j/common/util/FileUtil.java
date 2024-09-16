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
package org.niis.xrd4j.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This class offers helper methods for handling files.
 *
 * @author Petteri Kivimäki
 */
public final class FileUtil {

    private static final int BUFFER_SIZE = 8192;
    private static final String UTF_8 = "UTF-8";
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

    /**
     * Constructs and initializes a new FileUtil object. Should never be used.
     */
    private FileUtil() {
    }

    /**
     * Reads the contents of the file denoted by the path name. Prefers reading file
     * as a resource and secondly as a file in file system. If the file doesn't
     * exist, an empty string is returned.
     *
     * @param filePath
     *            resource or file path
     * @return contents of the file
     */
    public static String read(String filePath) {
        try {
            byte[] content = readFileAsBytes(filePath);
            return new String(content, Charset.forName(UTF_8));
        } catch (IOException | IllegalArgumentException e) {
            LOGGER.error("Could not read file {}.", filePath, e);
            return "";
        }
    }

    private static byte[] readFileAsBytes(String filePath) throws IOException {
        try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            while ((bytesRead = stream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            LOGGER.warn("Resource was not found! Trying to read '" + filePath + "' as a file.");
            File file = new File(filePath);
            if (!file.exists()) {
                throw new IllegalArgumentException("File '" + filePath + "' doesn't exist.");
            }
            
            return Files.readAllBytes(Paths.get(file.getAbsolutePath()));
        }

    }
}
