package com.seleritycorp.narwhal.client;

import com.seleritycorp.narwhal.client.methods.CS;
import com.seleritycorp.narwhal.client.methods.OBS;
import org.junit.Test;

import static junit.framework.Assert.*;


/**
 * Created by IntelliJ IDEA.
 * User: haowang
 * Date: 2/22/12
 * Time: 6:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class SanityIntegrationTest {

    @Test
    public void testSanity() throws Exception {

        Session session = new Session(Config.getProperty(Config.SERVER_CS), Config.getProperty(Config.USER), Config.getProperty(Config.CLIENT));
//        session.setDebug(true);

        // create an authentication user
        Request request = new Request(session, CS.AUTHENTICATE, Config.getProperty(Config.USER), "foo");
        Response response = session.dispatch(request);

        assertNotNull(response);
        assertTrue(response.hasError());
        assertEquals(-1000, response.getError().getCode());

        request = new Request(session, CS.AUTHENTICATE, Config.getProperty(Config.USER), Config.getProperty(Config.PASSWORD));
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

        Session session = new Session(Config.getProperty(Config.SERVER_OBS), Config.getProperty(Config.USER), Config.getProperty(Config.CLIENT));
//        session.setDebug(true);

        Request request = new Request(session, OBS.PING);
        Response response = session.dispatch(request);

        assertFalse(response.hasError());
    }
}
