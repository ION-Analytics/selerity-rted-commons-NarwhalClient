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

import com.seleritycorp.cs.standalone.commons.DataListener;
import com.seleritycorp.cs.standalone.commons.StartStop;
import com.seleritycorp.narwhal.client.methods.CS;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * For a session that requires authentication.
 */
public class AuthenticatedSession extends Session {
    private final Date expiration = new Date(0L);
    private TimeUnit leaseTimeUnit = TimeUnit.MINUTES;
    private int lease = 5;
    private String password;

    public AuthenticatedSession(String serverURL, String username, String client) throws MalformedURLException {
        super(serverURL, username, client);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public AuthenticatedSession(String serverURL, String username, String client, Boolean extensions) throws MalformedURLException {
        super(serverURL, username, client, extensions);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public final void connect() throws DispatchException {
        final Date now = new Date();

        if (now.getTime() < expiration.getTime()) {
            // We've got time left on this lease
            return;
        }

        try {
            setToken(authenticate(password));
        } catch (RemoteException re) {
            getLogger().warning("Authentication refused: " + re);
            throw new DispatchException(re.getMessage());
        } catch (RpcException e) {
            throw new DispatchException(e.getMessage());
        }
        expiration.setTime(now.getTime() + leaseTimeUnit.toMillis(lease));
    }

    public final void disconnect() {
        expiration.setTime(0L);
        invalidate();
        setToken(null);
    }

    protected String authenticate(String password) throws RpcException {
        final Request request = new Request(this, CS.AUTHENTICATE, getUsername(), password);
        final Response response = noAuthDispatch(request);
        if (response.hasError()) {
            throw new RemoteException(response.getError());
        }
        return response.getResult().toString();
    }

    protected void invalidate() {
        final Request request = new Request(this, CS.INVALIDATE);
        try {
            dispatch(request);
        } catch (DispatchException e) {
            getLogger().warning("Failed invalidating session: " + e);
        }
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Response noAuthDispatch(Request request) throws DispatchException {
        return super.dispatch(request);
    }

    public TimeUnit getLeaseTimeUnit() {
        return leaseTimeUnit;
    }

    public void setLease(TimeUnit leaseTimeUnit, int lease) {
        this.leaseTimeUnit = leaseTimeUnit;
        this.lease = lease;
    }

    public int getLease() {
        return lease;
    }

    @Override
    public Response dispatch(Request request) throws DispatchException {
        connect();
        request.setToken(getToken());
        return super.dispatch(request);
    }

    @Override
    public StartStop dispatch(Request request, DataListener<Response> listener) throws DispatchException {
        connect();
        request.setToken(getToken());
        return super.dispatch(request, listener);
    }
}
