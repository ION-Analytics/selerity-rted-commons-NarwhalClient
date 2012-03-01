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
import com.seleritycorp.cs.standalone.commons.logging.DoesLoggingImpl;
import  com.seleritycorp.narwhal.client.methods.CS;
import  com.seleritycorp.narwhal.client.methods.BDS;
import  com.seleritycorp.narwhal.client.methods.OBS;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static junit.framework.Assert.*;


/**
 * Created by IntelliJ IDEA.
 * User: haowang
 * Date: 2/22/12
 * Time: 7:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class SessionIntegrationTest extends DoesLoggingImpl implements DataListener<Response>{
    private static final int RESPONSES = 2;
    private AtomicBoolean complete = new AtomicBoolean(false);
    private int count;

    @Test
    public void testDispatch() throws Exception {
        Session session = new Session(Config.getProperty(Config.SERVER_CS), Config.getProperty(Config.USER), Config.getProperty(Config.CLIENT));
        Request request = new Request(session, CS.SERVER_TIME, "UTC");
        Response response = session.dispatch(request);
        assertNotNull(response);
        assertFalse(response.hasError());
        assertNotNull(response.getResult());
    }

    @Test
    public void testStreamedResponses() throws Exception {
        Request request;

        Session session = new Session(Config.getProperty(Config.SERVER_BDS), Config.getProperty(Config.USER), Config.getProperty(Config.CLIENT));

        count = RESPONSES;
        complete.set(false);
        request = new Request(session, BDS.HEART_BEAT, count, TimeUnit.SECONDS.toMillis(2L));
        session.dispatch(request, this);

        while (!complete.get()) {
            TimeUnit.SECONDS.sleep(1L);
        }
        assertEquals(0,count);
    }

    @Test
    public void testObsSub() throws Exception {
        Request request;
        Response response;

        Session session = new Session(Config.getProperty(Config.SERVER_OBS), Config.getProperty(Config.USER), Config.getProperty(Config.CLIENT));
        session.setDebug(true);
        request = new Request(session, OBS.AUTHENTICATE, Config.getProperty(Config.USER), Config.getProperty(Config.PASSWORD));
        response = session.dispatch(request);
        assertFalse(response.hasError());
        session.setToken(response.getResult().toString());

        count = RESPONSES;
        complete.set(false);
        request = new Request(session, OBS.HEART_BEAT, count + 2, TimeUnit.SECONDS.toMillis(2L));
        StartStop startStop = session.dispatch(request, this);
        startStop.start();
        while (!complete.get() && startStop.isAlive() ) {
            TimeUnit.SECONDS.sleep(1L);
        }
        startStop.stop();
        assertEquals(0,count);
        request = new Request(session, OBS.INVALIDATE);
        response = session.dispatch(request);
        assertFalse(response.hasError());
    }

    @Ignore
    @Test
    public void testBDS() throws Exception {
        Request request;

        Session session = new Session(Config.getProperty(Config.SERVER_BDS), Config.getProperty(Config.USER), Config.getProperty(Config.CLIENT));
        session.setDebug(true);
        request = new Request(session, BDS.GET_ALL_TAGS);
        Response response = session.dispatch(request);
    }

    @Override
    public void receive(Response response) {

        getLogger().info("Response: [" + new Date() + "]: " + response);
        count--;
        if (count == 0) {
            complete.set(true);
        }
    }

    @Override
    public void endOfData() {
        getLogger().info("End of responses.");
    }
}
