package com.seleritycorp.narwhal.client;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.logging.Logger;

public class URLUtilConnection {
    private static final Logger LOGGER = Logger.getLogger(URLUtilConnection.class.getName());

    public static String closeInputStream(URLConnection urlConnection) {

        String errorStreamValue = null;

        if (urlConnection instanceof HttpURLConnection) {
            InputStream es = null;
            try {
                es = ((HttpURLConnection) urlConnection).getErrorStream();
            } catch (Exception e) {
                // e.printStackTrace();
            } finally {
                if (es != null) {
                    try {
                        es.close();
                    } catch (Exception e) {
                    }
                }
            }
        }

        try {
            if (urlConnection != null) {
                AutoCloseable autoCloseable = urlConnection.getInputStream();
                if (autoCloseable != null) {
                    autoCloseable.close();
                }
            }
        } catch (Exception e) {
            LOGGER.warning("Failed to close URLConnection: " + e);
        }

        disconnect(urlConnection);

        return errorStreamValue;
    }

    private static void disconnect(URLConnection urlConnection) {
        if (!isKeepAliveConnection()) {
            if (urlConnection instanceof HttpURLConnection) {
                // even when we do disconnect, Java may reuse connection several times
                ((HttpURLConnection) urlConnection).disconnect();
            }
        }
    }

    private static boolean isKeepAliveConnection() {
        if (!isKeepAlive()) {
            return false;
        }
        return true;
    }

    private static boolean isKeepAlive() {
        String httpKeepAlive = System.getProperty("http.keepAlive");
        if (httpKeepAlive == null || httpKeepAlive.length() == 0) {
            // default: true
            return true;
        }
        return Boolean.parseBoolean(httpKeepAlive);
    }

}
