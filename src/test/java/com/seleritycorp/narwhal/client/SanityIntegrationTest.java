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

package com.seleritycorp.narwhal.client;

import com.seleritycorp.cs.standalone.commons.EnumMapPropertyFile;
import com.seleritycorp.narwhal.client.methods.CS;
import com.seleritycorp.narwhal.client.methods.OBS;
import org.junit.Test;

import static junit.framework.Assert.*;


public class SanityIntegrationTest {
    private EnumMapPropertyFile<Property> config = new EnumMapPropertyFile<Property>(Property.class, "test.properties");


    @Test
    public void testSanity() throws Exception {

        Session session = new Session(config.get(Property.SERVER_CS), config.get(Property.USER), config.get(Property.CLIENT));
//        session.setDebug(true);

        // create an authentication user
        Request request = new Request(session, CS.AUTHENTICATE, config.get(Property.USER), "foo");
        Response response = session.dispatch(request);

        assertNotNull(response);
        assertTrue(response.hasError());
        assertEquals(-1000, response.getError().getCode());

        request = new Request(session, CS.AUTHENTICATE, config.get(Property.USER), config.get(Property.PASSWORD));
        response = session.dispatch(request);

        assertNotNull(response);
        assertFalse(response.hasError());

        String token = response.getResult().toString();
        assertNotNull(token);
        session.setToken(token);

        request = new Request(session, CS.INVALIDATE);
        response = session.dispatch(request);

        assertNotNull(response);
    }

    @Test
    public void testArgo() throws Exception {

        Session session = new Session(config.get(Property.SERVER_OBS), config.get(Property.USER), config.get(Property.CLIENT));
        //session.setDebug(true);

        Request request = new Request(session, OBS.PING);
        Response response = session.dispatch(request);

        assertFalse(response.hasError());
    }
}
