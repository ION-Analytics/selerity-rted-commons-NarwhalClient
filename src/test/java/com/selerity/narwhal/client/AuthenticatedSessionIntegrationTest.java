package com.selerity.narwhal.client;

import com.seleritycorp.cs.standalone.commons.logging.DoesLoggingImpl;
import com.selerity.narwhal.client.methods.CS;
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
