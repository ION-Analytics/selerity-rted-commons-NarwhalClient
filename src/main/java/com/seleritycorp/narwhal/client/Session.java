/*
 * (c) Copyright Selerity, Inc. 2009-2012. All rights reserved. This source code is confidential
 * and proprietary information of Selerity Inc. and may be used only by a recipient designated
 * by and for the purposes permitted by Selerity Inc. in writing.  Reproduction of, dissemination
 * of, modifications to or creation of derivative works from this source code, whether in source
 * or binary forms, by any means and in any form or manner, is expressly prohibited, except with
 * the prior written permission of Selerity Inc..  THIS CODE AND INFORMATION ARE PROVIDED "AS IS"
 * WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE. This notice may not be
 * removed from the software by any user thereof.
 */

package com.seleritycorp.narwhal.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.seleritycorp.cs.standalone.commons.DataListener;
import com.seleritycorp.cs.standalone.commons.StartStop;
import com.seleritycorp.cs.standalone.commons.logging.DoesLoggingImpl;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

// TODO: Add support for fall back servers
/**
 * A class representing an rpc session.
 */
public class Session extends DoesLoggingImpl {
    public static final GsonBuilder GSON_BUILDER = new GsonBuilder().serializeNulls().disableHtmlEscaping();
    private final Boolean extensions;
    private final String client;
    private final URL serverURL;
    private String username;
    private int connectionTimeout = 45;
    private TimeUnit connectionTimeoutUnit = TimeUnit.SECONDS;
    private int readTimeout = 10;
    private TimeUnit readTimeoutUnit = TimeUnit.MINUTES;
    private String token = null;
    private boolean debug = false;

    static {
        // Allow for localhost
        HttpsURLConnection.setDefaultHostnameVerifier(new javax.net.ssl.HostnameVerifier() {
            public boolean verify(String hostname, SSLSession sslSession) {
                return "localhost".equals(hostname);
            }
        });
    }

    /**
     * Constructor Session creates a new Session instance. Defaults to non-extension mode.
     *
     * @param serverURL of type String
     * @param username  of type String
     * @param client    of type String
     * @throws MalformedURLException when
     */
    public Session(String serverURL, String username, String client) throws MalformedURLException {
        this(serverURL, username, client, false);
    }

    /**
     * Constructor Session creates a new Session instance.
     *
     * @param serverURL  of type String
     * @param username   of type String
     * @param client     of type String
     * @param extensions of type Boolean
     * @throws MalformedURLException when
     */
    public Session(String serverURL, String username, String client, Boolean extensions) throws MalformedURLException {
        this.serverURL = new URL(serverURL);
        this.extensions = extensions;
        this.client = client;
        this.username = username;

    }

    /**
     * Method getUsername returns the username of this Session object.
     *
     * @return the username (type String) of this Session object.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Method setUsername sets the username of this Session object.
     *
     * @param username the username of this Session object.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Method getExtensions returns the extensions of this Session object.
     *
     * @return the extensions (type Boolean) of this Session object.
     */
    public Boolean getExtensions() {
        return extensions;
    }

    /**
     * Method getClient returns the client of this Session object.
     *
     * @return the client (type String) of this Session object.
     */
    public String getClient() {
        return client;
    }

    /**
     * Method getServerURL returns the serverURL of this Session object.
     *
     * @return the serverURL (type String) of this Session object.
     */
    public String getServerURL() {
        return serverURL.toString();
    }

    /**
     * Method getToken returns the token of this Session object.
     *
     * @return the token (type String) of this Session object.
     */
    public String getToken() {
        return token;
    }

    /**
     * Method setToken sets the token of this Session object.
     *
     * @param token the token of this Session object.
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Return the debugging status of the Session.
     *
     * @return debug value
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * Set the debugging status of the Session.
     *
     * @param debug the value
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
        if (debug) {
            getLogger().info(this.toString());
        }
    }

    /**
     * Method dispatch a request to the SeleritySync API service.
     *
     * @param request of type Request
     * @return Object
     * @throws DispatchException when the request can not be dispatched
     */
    public Response dispatch(Request request) throws DispatchException {
        final Gson gson = GSON_BUILDER.create();
        if (debug) {
            getLogger().info("Request " + request.toString());
        }

        URLConnection connection = null;

        try {
            connection = serverURL.openConnection();
            TypeAdapter<Request> requestTypeAdapter = gson.getAdapter(Request.class);
            writeRequest(connection, request, requestTypeAdapter);
            JsonReader jsonReader = new JsonReader(new InputStreamReader(connection.getInputStream()));
            TypeAdapter<Response> responseTypeAdapter = gson.getAdapter(Response.class);
            Response response = responseTypeAdapter.read(jsonReader);
            if (debug) {
                getLogger().info("Response: " + response);
            }
            return response;
        } catch (Exception e) {
            getLogger().warning("Failed request to " + serverURL + ": " + request);
            throw new DispatchException("Remote dispatch to failed: " + e, e);
        } finally {
            try {
                if (connection != null) {
                    connection.getInputStream().close();
                }
            } catch (IOException e) {
                getLogger().warning("URLConnection close issues: " + e);
            }
        }
    }

    /**
     * Dispatches a request, creating a thread to listen for responses which it sends along to the listener.
     *
     * @param request The request to dispatch
     * @param listener the listener to receive the responses
     * @return a control reference to the reader
     * @throws DispatchException if the request can not be dispatched
     */
    public StartStop dispatch(Request request, DataListener<Response> listener) throws DispatchException {
        final Gson gson = GSON_BUILDER.create();

        if (debug) {
            getLogger().info("Request " + request.toString());
        }

        URLConnection connection;

        try {
            connection = serverURL.openConnection();
            TypeAdapter<Request> requestTypeAdapter = gson.getAdapter(Request.class);
            writeRequest(connection, request, requestTypeAdapter);
        } catch (IOException e) {
            throw new DispatchException("Remote dispatch failed", e);
        }

        // Read the response(s)
        TypeAdapter<Response> responseTypeAdapter = gson.getAdapter(Response.class);
        ResponseReader streamer = new ResponseReader(connection, listener, responseTypeAdapter);
        Thread thread = new Thread(streamer);
        thread.setDaemon(true);
        thread.start();
        return streamer;
    }


    @Override
    public String toString() {
        return toJson(this);
    }

    /**
     * Method getGsonBuilder returns the gsonBuilder of this Session object.
     *
     * @return the gsonBuilder (type GsonBuilder) of this Session object.
     */
    public static GsonBuilder getGsonBuilder() {
        return GSON_BUILDER;
    }

    /**
     * Method getConnectionTimeout returns the connectionTimeout of this Session object.
     *
     * @return the connectionTimeout (type int) of this Session object.
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * Get the TimeUnit of the connection timeout.
     *
     * @return the TimeUnit
     */
    public TimeUnit getConnectionTimeoutUnit() {
        return connectionTimeoutUnit;
    }

    /**
     * Set the connection timeout.
     *
     * @param timeUnit the time unit
     * @param timeout  the unit value
     */
    public void setConnectionTimeout(TimeUnit timeUnit, int timeout) {
        connectionTimeoutUnit = timeUnit;
        connectionTimeout = timeout;
    }

    /**
     * Method getReadTimeout returns the readTimeout of this Session object.
     *
     * @return the readTimeout (type int) of this Session object.
     */
    public int getReadTimeout() {
        return readTimeout;
    }

    /**
     * Get the TimeUnit of the read timeout.
     *
     * @return the TimeUnit
     */
    public TimeUnit getReadTimeoutUnit() {
        return readTimeoutUnit;
    }

    /**
     * Set the read timeout for the session.
     *
     * @param timeUnit the unit
     * @param timeout  the value
     */
    public void setReadTimeout(TimeUnit timeUnit, int timeout) {
        readTimeoutUnit = timeUnit;
        readTimeout = timeout;
    }

    /**
     * Inefficient but simple way to convert an object to it's JSON string representation.
     *
     * @param o  the object to convert to JSON
     * @return the resultant JSON
     */
    public static String toJson(Object o) {
        final Gson gson = GSON_BUILDER.create();
        return gson.toJson(o);
    }

    /**
     * Writes a request to the given connection.
     *
     * @param connection the connection to write to
     * @param request the request to write
     * @param requestTypeAdapter the type adapter to use
     * @throws IOException if the write does not succeed
     */
    private void writeRequest(URLConnection connection, Request request, TypeAdapter<Request> requestTypeAdapter) throws IOException {
        connection.setConnectTimeout((int) connectionTimeoutUnit.toMillis(connectionTimeout));
        connection.setReadTimeout((int) readTimeoutUnit.toMillis(readTimeout));
        connection.setRequestProperty("Accept", "text/plain");
        connection.setRequestProperty("Content-type", "application/json");
        connection.setRequestProperty("User-Agent", getClient());
        connection.setDoOutput(true);

        // Send the json
        JsonWriter jsonWriter = new JsonWriter(new OutputStreamWriter(connection.getOutputStream()));

        requestTypeAdapter.write(jsonWriter, request);
        jsonWriter.close();
    }

    /**
     * Reads responses from a connection sending them to the listener until the connection closes.
     */
    private class ResponseReader implements Runnable, StartStop {
        private final URLConnection connection;
        private final DataListener<Response> listener;
        private final TypeAdapter<Response> responseTypeAdapter;
        private final AtomicInteger alive = new AtomicInteger(0);

        public ResponseReader(URLConnection connection, DataListener<Response> listener, TypeAdapter<Response> responseTypeAdapter) {
            this.connection = connection;
            this.listener = listener;
            this.responseTypeAdapter = responseTypeAdapter;
        }

        @Override
        public void start() throws Exception {
            while (alive.get() == 0) {
                TimeUnit.MILLISECONDS.sleep(5);
            }
        }

        /**
         * This should be true after start, and false before endOfData.
         * @return is this alive
         */
        @Override
        public boolean isAlive() {
            return alive.get() == 1;
        }

        @Override
        public void stop() {
            if (isAlive()) {
                alive.set(2);
                listener.endOfData();
            }
        }

        @Override
        public void run() {
            JsonReader jsonReader;
            alive.set(1);
            try {
                jsonReader = new JsonReader(new InputStreamReader(connection.getInputStream()));
                jsonReader.setLenient(true);
                while (alive.get() == 1) {
                    if (jsonReader.peek() == JsonToken.END_DOCUMENT) {
                        break;
                    }
                    Response response = responseTypeAdapter.read(jsonReader);
                    listener.receive(response);
                }
            } catch (IOException io) {
                getLogger().warning("IOException: " + io);
            } finally {
                try {
                    connection.getInputStream().close();
                } catch (IOException e) {
                    getLogger().warning("URLConnection close issues: " + e);
                }
            }
            stop();
        }
    }
}
