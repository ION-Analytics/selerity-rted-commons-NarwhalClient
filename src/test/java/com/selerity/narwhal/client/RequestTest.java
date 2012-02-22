package com.selerity.narwhal.client;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonWriter;
import com.seleritycorp.cs.standalone.commons.logging.DoesLoggingImpl;
import org.junit.Test;

import java.io.StringWriter;

/**
 * Created by IntelliJ IDEA.
 * User: haowang
 * Date: 2/22/12
 * Time: 4:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class RequestTest extends DoesLoggingImpl{
    @Test
    public void testTypeAdapter() throws Exception {
        final Session session = new Session("http://localhost:8080", "testuser1", "test");
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
