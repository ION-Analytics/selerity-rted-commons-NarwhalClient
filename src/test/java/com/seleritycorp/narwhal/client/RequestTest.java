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

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonWriter;
import com.seleritycorp.cs.standalone.commons.EnumMapPropertyFile;
import com.seleritycorp.cs.standalone.commons.logging.DoesLoggingImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.StringWriter;

import static org.junit.Assert.*;


public class RequestTest extends DoesLoggingImpl {
    private EnumMapPropertyFile<Property> config = new EnumMapPropertyFile<Property>(Property.class, "test.properties");
    private Session session = null;

    @Before
    public void setup() throws Exception {
        if (session == null) {
            session = new Session("http://localhost:8080", config.get(Property.USER), config.get(Property.CLIENT));
        }
    }

    @Test
    public void testParamValidation1() throws Exception {
        Request request = new Request(session, config.get(Property.USER), config.get(Property.CLIENT));
        assertNotNull(request);
    }

    @Test
    public void testParamValidation2() throws Exception {
        Request request = new Request(session, null, null);
        assertNotNull(request);
    }

    @Test(expected = NullPointerException.class)
    public void testParamValidation3() throws Exception {
        Request request = new Request(null, config.get(Property.USER), config.get(Property.CLIENT));
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
        typeAdapter.write(jsonWriter, request);
        jsonWriter.flush();
        jsonWriter.close();
        stringWriter.flush();
        getLogger().info(stringWriter.toString());
        stringWriter.close();

    }
}
