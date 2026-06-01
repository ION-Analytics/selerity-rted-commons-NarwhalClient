/*
 * (c) Copyright Selerity, Inc. 2009-2019. All rights reserved. This source code is confidential
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

import java.util.UUID;

/**
 * Represents an out going JSON RPC Narwhal request.
 */
public class Request {
    private String method;
    private Object[] params;
    private Header header;
    private String id;

    public Request() {
    }

    /**
     * @param method
     * @param params
     */
    public Request(String method, Object[] params) {
        this();
        id = UUID.randomUUID().toString();
        this.method = method;
        this.params = (params != null ? params : new Object[0]);
    }

    /**
     * Constructor Request creates a new Request instance.
     *
     * @param session of type Session
     * @param method  of type Method
     * @param params  of type Object[]
     */
    public Request(Session session, String method, Object... params) {
        this(method, params);
        header = new Header();
        if (session.getUsername() != null) {
            header.setUser(session.getUsername());
        }
        if (session.getToken() != null) {
            header.setToken(session.getToken());
        }
        header.setClient(session.getClient());
        header.setMode(session.getExtensions() ? "extension" : "core");
    }

    /**
     * Retrieve the method field of the request.
     *
     * @return then method name
     */
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * The array of argument objects.
     *
     * @return the arguments
     */
    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    /**
     * The Request id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return Session.toJson(this);
    }

    /**
     * Set the token on a request explicitly.
     *
     * @param token the token
     */
    public void setToken(String token) {
        header.setToken(token);
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    /**
     * The request header.
     */
    public static class Header {
        private String user;
        private String token;
        private String client;
        private String mode;
        private boolean canStream;

        public Header() {
            setCanStream(true);
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getClient() {
            return client;
        }

        public void setClient(String client) {
            this.client = client;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public boolean getCanStream() {
            return canStream;
        }

        public void setCanStream(boolean canStream) {
            this.canStream = canStream;
        }

        @Override
        public String toString() {
            return Session.toJson(this);
        }
    }
}




