package com.seleritycorp.narwhal.client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

/**
 * A Map with an Enum key that loads its string values from a properties file.
 */
public class EnumMapPropertyFile<K extends Enum<K>> extends EnumMap<K, String> {
    private static final Logger LOGGER = Logger.getLogger(EnumMapPropertyFile.class.getName());

    public EnumMapPropertyFile(Class<K> kClass) {
        this(kClass, System.getProperty("config.properties", "config.properties"));
    }

    public EnumMapPropertyFile(Class<K> kClass, String properties) {
        super(kClass);

        final Properties loaded = new Properties();
        try {
            InputStream stream = getResourceAsStream(properties);
            if (stream == null) {
                LOGGER.warning("Failed finding " + properties);
            } else {
                loaded.load(getResourceAsStream(properties));
            }
        } catch (IOException e) {
            LOGGER.warning("Failed loading " + properties + ": " + e);
        }

        Set<K> values = EnumSet.allOf(kClass);
        for (K k : values) {
            final String value = loaded.getProperty(k.name());
            if (value != null) {
                this.put(k, value);
            }
        }
    }

    private static InputStream getResourceAsStream(String resource) {
        final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        InputStream stream = classLoader.getResourceAsStream(resource);
        if (stream != null) {
            return stream;
        }
        // Couldn't be pulled by the class loader, maybe it's just a file
        try {
            stream = new FileInputStream(resource);
        } catch (FileNotFoundException e) {
            LOGGER.warning("Resource " + resource + " not found: " + e);
            return null;
        }
        return stream;
    }
}
