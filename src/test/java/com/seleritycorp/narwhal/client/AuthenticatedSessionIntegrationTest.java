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

import com.seleritycorp.cs.standalone.commons.logging.DoesLoggingImpl;
import com.seleritycorp.narwhal.client.methods.CS;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 * Created by IntelliJ IDEA.
 * User: haowang
 * Date: 2/22/12
 * Time: 6:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class AuthenticatedSessionIntegrationTest extends DoesLoggingImpl{
        @Test
    public void testAuthenticate() throws Exception {
        AuthenticatedSession session = new AuthenticatedSession(Config.getProperty(Config.SERVER_CS), Config.getProperty(Config.USER), Config.getProperty(Config.CLIENT));
        assertNotNull(session);
        session.setPassword(Config.getProperty(Config.PASSWORD));

        Request request;
        Response response;

        // Try a no auth method
        request = new Request(session, CS.SERVER_TIME, "UTC");
        response = session.noAuthDispatch(request);
        assertNotNull(response);
        getLogger().info(response.toString());
        assertFalse(response.hasError());

        // Now an Auth one
        request = new Request(session, CS.INVALIDATE);
        response = session.dispatch(request);
        assertNotNull(response);
        getLogger().info(response.toString());
        assertFalse(response.hasError());

        session.disconnect();
        assertNull(session.getToken());
    }
}
