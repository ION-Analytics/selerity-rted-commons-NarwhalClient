/*
 * (c) Copyright Selerity, Inc. 2009-2013. All rights reserved. This source code is confidential
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
import com.seleritycorp.cs.standalone.commons.EnumMapPropertyFile;
import com.seleritycorp.cs.standalone.commons.logging.DoesLoggingImpl;
import com.seleritycorp.narwhal.client.Request;
import com.seleritycorp.narwhal.client.Response;
import com.seleritycorp.narwhal.client.Session;
import com.seleritycorp.narwhal.client.methods.BDS;
import gnu.getopt.Getopt;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class HeartBeatListener extends DoesLoggingImpl implements DataListener<Response> {
    private EnumMapPropertyFile<Property> config = new EnumMapPropertyFile<>(Property.class);
    private AtomicBoolean complete = new AtomicBoolean(false);
    private int count;

    public HeartBeatListener(int count) {
        this.count = count;
    }

    public void listen() throws Exception {
        Session session = new Session(config.get(Property.SERVER_BDS), config.get(Property.USER), config.get(Property.CLIENT));

        complete.set(false);
        Request request = new Request(session, BDS.HEART_BEAT, count, TimeUnit.SECONDS.toMillis(2L));

        session.dispatch(request, this);

        while (!complete.get()) {
            TimeUnit.SECONDS.sleep(1L);
        }
    }

    @Override
    public void receive(Response response) {

        getLogger().info("Response: [" + new Date() + "]: " + (Double) response.getResult());
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
        int count = 0;

        Getopt getopt = new Getopt("HeartBeatListener", args, "c:");
        int c;
        while ((c = getopt.getopt()) != -1) {
            switch (c) {
                case 'c':
                    count = Integer.parseInt(getopt.getOptarg());
                    break;
                case '?':
                    System.out.println("Options:");
                    System.out.println("-c                           Number of times to write the counter.");
                    return;
            }
        }

        if (count != 0) {
            final HeartBeatListener hearBeatListener = new HeartBeatListener(count);
            hearBeatListener.listen();
        }
    }
}
