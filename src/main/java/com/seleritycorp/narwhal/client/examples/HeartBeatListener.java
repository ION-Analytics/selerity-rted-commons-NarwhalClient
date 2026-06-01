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
package com.seleritycorp.narwhal.client.examples;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import com.seleritycorp.narwhal.client.DataListener;
import com.seleritycorp.narwhal.client.EnumMapPropertyFile;
import com.seleritycorp.narwhal.client.Request;
import com.seleritycorp.narwhal.client.Response;
import com.seleritycorp.narwhal.client.Session;
import com.seleritycorp.narwhal.client.methods.BDS;

public final class HeartBeatListener implements DataListener<Response> {
    private static final Logger log = Logger.getLogger(HeartBeatListener.class.getName());

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

        log.info("Response: [" + new Date() + "]: " + (Double) response.getResult());
        count--;
        if (count == 0) {
            complete.set(true);
        }
    }

    @Override
    public void endOfData() {
        log.info("End of responses.");
    }

    public static void main(final String[] args) throws Exception {
        int count = 0;

        final Options options = new Options();
        options.addOption(new Option("c", "c", true, "Number of times to write the counter."));

        CommandLine commandLine = new DefaultParser().parse(options, args);
        if (commandLine.hasOption("c")) {
            count = Integer.parseInt(commandLine.getOptionValue("c"));
        } else {
            System.out.println("Options:");
            System.out.println("-c                           Number of times to write the counter.");
            return;
        }

        if (count != 0) {
            final HeartBeatListener hearBeatListener = new HeartBeatListener(count);
            hearBeatListener.listen();
        }
    }

}
