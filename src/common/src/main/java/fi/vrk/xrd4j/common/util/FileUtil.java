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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class offers helper methods for handling files.
 *
 * @author Petteri Kivimäki
 */
public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**
     * Constructs and initializes a new FileUtil object. Should never be used.
     */
    private FileUtil() {
    }

    /**
     * Reads the contents of the file denoted by the path name. If the file doesn't
     * exist, an empty string is returned.
     *
     * @param filePath
     *            file path
     * @return contents of the file
     */
    public static String read(String filePath) {
        try {
            File file = getFile(filePath);
            byte[] encoded = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
            return new String(encoded, Charset.forName("UTF-8"));
        } catch (IOException | IllegalArgumentException e) {
            logger.error("Could not read file {}.", filePath, e);
            return "";
        }
    }

    private static File getFile(String filePath) {
        File file = new File(FileUtil.class.getClassLoader().getResource(filePath).getFile());

        if (!file.exists()) {
            logger.warn("Resource file is not found!");
            file = new File(filePath);
            if (!file.exists()) {
                throw new IllegalArgumentException("File doesn't exist: " + filePath);
            }
        }

        return file;
    }
}
