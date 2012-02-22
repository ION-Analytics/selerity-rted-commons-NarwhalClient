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

package com.selerity.narwhal.client;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents an out going JSON RPC Narwhal request.
 */
public class Request {
    private final String method;
    private final Object[] params;
    private final Map<String,Object> header = new HashMap<>();
    private final String id = UUID.randomUUID().toString();
    
    /**
     * Constructor Request creates a new Request instance.
     *
     * @param session of type Session
     * @param method of type Method
     * @param params of type Object[]
     */
    public Request(Session session, String method, Object... params) {
        this.method = method;
        this.params = (params != null ? params : new Object[0]);
        if (session.getUsername() != null) {
            header.put("user", session.getUsername());
        }
        if (session.getToken() != null) {
            header.put("token", session.getToken());
        }
        header.put("canStream",true);
        header.put("client", session.getClient());
        header.put("mode", (session.getExtensions() ? "extension" : "core"));
    }

    /**
     * Retrieve the method field of the request.
     * @return then method name
     */
    public String getMethod() {
        return method;
    }

    /**
     * The array of argument objects.
     * @return the arguments
     */
    public Object[] getParams() {
        return params;
    }

    /**
     * The Request id.
     * @return the id
     */
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return Session.toJson(this);
    }

    /**
     * Set the token on a request explicitly.
     * @param token the token
     */
    public void setToken(String token) {
        if (token != null) {
            header.put("token",token);
        } else {
            header.remove("token");
        }
    }
}




