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

import com.seleritycorp.narwhal.client.methods.BDS;
import com.seleritycorp.narwhal.client.methods.CS;
import com.seleritycorp.narwhal.client.methods.OBS;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeNotNull;

public class SessionIntegrationTest implements DataListener<Response> {
    private EnumMapPropertyFile<Property> config = new EnumMapPropertyFile<>(Property.class, "test.properties");
    private static final int RESPONSES = 2;
    private AtomicBoolean complete = new AtomicBoolean(false);
    private int count;

    @Test
    public void testDispatch() throws Exception {
        System.out.println("\n\n\n=== testDispatch ===\n\n\n");
        Session session = new Session(config.get(Property.SERVER_CS), config.get(Property.USER), config.get(Property.CLIENT));
        Request request = new Request(session, CS.SERVER_TIME, "UTC");
        Response response = session.dispatch(request);
        assertNotNull(response);
        assertFalse(response.hasError());
        assertNotNull(response.getResult());
    }

    @Test
    public void testStreamedResponses() throws Exception {
        System.out.println("\n\n\n=== testStreamedResponses ===\n\n\n");
        Request request;

        Session session = new Session(config.get(Property.SERVER_BDS), config.get(Property.USER), config.get(Property.CLIENT));

        count = RESPONSES;
        complete.set(false);
        request = new Request(session, BDS.HEART_BEAT, count, TimeUnit.SECONDS.toMillis(2L));
        session.dispatch(request, this);

        while (!complete.get()) {
            TimeUnit.SECONDS.sleep(1L);
        }
        assertEquals(0, count);
    }

    @Test
    public void testObsSub() throws Exception {
        System.out.println("\n\n\n=== testObsSub ===\n\n\n");

        Request request;
        Response response;

        Session csSession = new Session(config.get(Property.SERVER_CS), config.get(Property.USER), config.get(Property.CLIENT));
        csSession.setDebug(Boolean.parseBoolean(config.get(Property.RPC_DEBUG)));
        request = new Request(csSession, CS.AUTHENTICATE, config.get(Property.USER), config.get(Property.PASSWORD));
        response = csSession.dispatch(request);
        assertFalse(response.hasError());
        csSession.setToken(response.getResult().toString());

        Session obsSession = new Session(config.get(Property.SERVER_OBS), config.get(Property.USER), config.get(Property.CLIENT));
        obsSession.setDebug(csSession.isDebug());
        obsSession.setToken(csSession.getToken());
        count = RESPONSES;
        complete.set(false);
        request = new Request(obsSession, OBS.HEART_BEAT, count + 2, TimeUnit.SECONDS.toMillis(2L));
        StartStop startStop = obsSession.dispatch(request, this);
        startStop.start();
        while (!complete.get() && startStop.isAlive()) {
            TimeUnit.SECONDS.sleep(1L);
        }
        startStop.stop();
        assertEquals(0, count);

        request = new Request(csSession, CS.INVALIDATE);
        response = csSession.dispatch(request);
        assertFalse(response.hasError());
    }

    // This requires a keystore setup and command line args
    @Ignore
    @Test
    public void testHTTPS() throws Exception {
        System.out.println("\n\n\n=== testHTTPS ===\n\n\n");
        assumeNotNull(config.get(Property.HTTPS_AUTH));
        Session session = new Session(config.get(Property.HTTPS_AUTH), config.get(Property.USER), config.get(Property.CLIENT));
        session.setDebug(Boolean.parseBoolean(config.get(Property.RPC_DEBUG)));
        Request request = new Request(session, CS.AUTHENTICATE, config.get(Property.USER), config.get(Property.PASSWORD));
        Response response = session.dispatch(request);
        assertFalse(response.hasError());
    }

    @Ignore
    @Test
    public void testBDS() throws Exception {
        Request request;
        Session session = new Session(config.get(Property.SERVER_BDS), config.get(Property.USER), config.get(Property.CLIENT));
        session.setDebug(true);
        request = new Request(session, BDS.GET_ALL_TAGS);
        Response response = session.dispatch(request);
    }

    @Override
    public void receive(Response response) {
        System.out.println("Response: [" + new Date() + "]: " + response);
        if (response.hasError()) {
            complete.set(true);
        }
        count--;
        if (count == 0) {
            complete.set(true);
        }
    }

    @Override
    public void endOfData() {
        System.out.println("End of responses.");
    }

}
