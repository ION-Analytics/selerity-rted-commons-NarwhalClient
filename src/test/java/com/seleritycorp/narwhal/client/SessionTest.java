package com.seleritycorp.narwhal.client;

import com.seleritycorp.cs.standalone.commons.logging.DoesLoggingImpl;
import org.junit.Test;

import java.net.MalformedURLException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: haowang
 * Date: 2/23/12
 * Time: 1:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class SessionTest extends DoesLoggingImpl{
    @Test
    public void testParamValidation1() throws Exception {
        Session session = new Session("http://localhost:8080", "testuser1", "test");
        assertNotNull(session);
    }

    @Test
    public void testParamValidation2() throws Exception {
        Session session = new Session("http://localhost:8080", "testuser1", "test", true);
        assertNotNull(session);
    }

    @Test
    public void testParamValidation3() throws Exception {
        Session session = new Session("http://localhost:8080", null, null);
        assertNotNull(session);
    }

    @Test(expected = MalformedURLException.class)
    public void testParamValidation4() throws Exception {
        Session session = new Session(null, "testuser1", "test");
        assertTrue("A MalformedURLException was expected, but was not thrown", false);
    }
}
