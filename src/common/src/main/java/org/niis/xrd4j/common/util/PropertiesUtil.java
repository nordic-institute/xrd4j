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
package org.niis.xrd4j.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

/**
 * This class offers methods for loading properties files. All the loaded files
 * are cached, which means that they are read from disk only once.
 *
 * @author Petteri Kivimäki
 */
public final class PropertiesUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesUtil.class);
    private HashMap<String, Properties> loadedProps;
    private static PropertiesUtil ref;

    /**
     * Constructs and initializes a new PropertiesUtil object. Should never be
     * used outside this class.
     */
    private PropertiesUtil() {
        this.loadedProps = new HashMap<>();
    }

    /**
     * Returns a reference to a PropertiesUtil instance. If the instance does
     * not exist yet, it's first initialized.
     *
     * @return PropertiesUtil object
     */
    public static PropertiesUtil getInstance() {
        if (ref == null) {
            ref = new PropertiesUtil();
        }
        return ref;
    }

    /**
     * Loads a properties file from the classpath.
     *
     * @param propsName path to the properties file
     * @return Properties properties that were loaded from the given file or
     * null, if loading the properties fails
     */
    public Properties load(String propsName) {
        return PropertiesUtil.getInstance().load(propsName, true);
    }

    /**
     * Loads a properties file from the classpath or from file system.
     *
     * @param propsName path to the properties file
     * @param fromClasspath when true the file is searched from classpath,
     * otherwise directly from the file system
     * @return Properties properties that were loaded from the given file or
     * null, if loading the properties fails
     */
    public Properties load(String propsName, boolean fromClasspath) {
        LOGGER.debug("Load properties from file : \"" + propsName + "\".");
        // If file is already loaded, return it from cache
        if (loadedProps.containsKey(propsName)) {
            LOGGER.debug("File already loaded. Use cache.");
            return loadedProps.get(propsName);
        }

        Properties props = null;
        InputStream is = null;
        try {
            if (fromClasspath) {
                LOGGER.debug("Load properties from classpath.");
                is = this.getClass().getResourceAsStream(propsName);
            } else {
                LOGGER.debug("Load properties from file system.");
                is = new FileInputStream(propsName);
            }
            props = new Properties();
            props.load(is);
            loadedProps.put(propsName, props);
            LOGGER.debug("Properties were succesfully loaded.");
        } catch (Exception e) {
            LOGGER.error("Loading properties failed.");
            LOGGER.error(e.getMessage(), e);
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                    LOGGER.error("Closing input stream failed.");
                    LOGGER.error(ex.getMessage(), ex);
                }
            }
        }
        return props;
    }
}
