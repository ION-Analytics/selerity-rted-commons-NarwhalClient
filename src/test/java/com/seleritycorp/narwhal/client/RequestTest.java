package com.seleritycorp.narwhal.client;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonWriter;
import com.seleritycorp.cs.standalone.commons.logging.DoesLoggingImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.StringWriter;

import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: haowang
 * Date: 2/22/12
 * Time: 4:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class RequestTest extends DoesLoggingImpl{

    private Session session = null;

    @Before
    public void setup() throws Exception {
        if(session == null) {
            session = new Session("http://localhost:8080", "testuser1", "test");
        }
    }

    @Test
    public void testParamValidation1() throws Exception {
        Request request = new Request(session, "testuser1", "test");
        assertNotNull(request);
    }

    @Test
    public void testParamValidation2() throws Exception {
        Request request = new Request(session, null, null);
        assertNotNull(request);
    }

    @Test(expected = NullPointerException.class)
    public void testParamValidation3() throws Exception {
        Request request = new Request(null, "testuser1", "test");
        assertTrue("A NullPointerException was expected, but was not thrown", false);
    }

    @Test
    public void testRandomUUID() throws Exception {
        Request request1 = new Request(session, "testuser1", "test");
        Request request2 = new Request(session, "testuser1", "test");
        assertNotSame(request1.toString(), request2.toString());
    }

    @Test
    public void testTypeAdapter() throws Exception {
        final Request request = new Request(session, "Echo.echo", "hello world");

        TypeAdapter<Request> typeAdapter = Session.GSON_BUILDER.create().getAdapter(Request.class);
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);
        typeAdapter.write(jsonWriter,request);
        jsonWriter.flush();
        jsonWriter.close();
        stringWriter.flush();
        getLogger().info(stringWriter.toString());
        stringWriter.close();

    }
}
