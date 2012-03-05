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

package com.seleritycorp.narwhal.client.examples;

import com.seleritycorp.cs.standalone.commons.DataListener;
import com.seleritycorp.cs.standalone.commons.logging.DoesLoggingImpl;
import com.seleritycorp.narwhal.client.examples.Config;
import com.seleritycorp.narwhal.client.Request;
import com.seleritycorp.narwhal.client.Response;
import com.seleritycorp.narwhal.client.Session;
import com.seleritycorp.narwhal.client.methods.BDS;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by IntelliJ IDEA.
 * User: haowang
 * Date: 3/5/12
 * Time: 12:49 PM
 * To change this template use File | Settings | File Templates.
 */
public final class HearBeatListener extends DoesLoggingImpl implements DataListener<Response> {
    private AtomicBoolean complete = new AtomicBoolean(false);
    private int count;

    public HearBeatListener(int count) {
        this.count = count;
    }

    public void listen() throws Exception {
        Session session = new Session(Config.getProperty(Config.SERVER_BDS), Config.getProperty(Config.USER), Config.getProperty(Config.CLIENT));

        complete.set(false);
        Request request = new Request(session, BDS.HEART_BEAT, count, TimeUnit.SECONDS.toMillis(2L));

        session.dispatch(request, this);

        while (!complete.get()) {
            TimeUnit.SECONDS.sleep(1L);
        }
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

    public static void main(final String[] args) throws Exception {
        if(args.length != 1) {
            System.out.println("Invalid number of input!");
            return;
        }
        final HearBeatListener hearBeatListener= new HearBeatListener(Integer.parseInt(args[0]));
        hearBeatListener.listen();
    }
}
