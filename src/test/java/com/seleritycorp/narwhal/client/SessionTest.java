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

import com.seleritycorp.cs.standalone.commons.EnumMapPropertyFile;
import com.seleritycorp.cs.standalone.commons.logging.DoesLoggingImpl;
import org.junit.Test;

import java.net.MalformedURLException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SessionTest extends DoesLoggingImpl{
    private EnumMapPropertyFile<Property> config = new EnumMapPropertyFile<Property>(Property.class, "test.properties");

    @Test
    public void testParamValidation1() throws Exception {
        Session session = new Session("http://localhost:8080", config.get(Property.USER), config.get(Property.CLIENT));
        assertNotNull(session);
    }

    @Test
    public void testParamValidation2() throws Exception {
        Session session = new Session("http://localhost:8080", config.get(Property.USER), config.get(Property.CLIENT), true);
        assertNotNull(session);
    }

    @Test
    public void testParamValidation3() throws Exception {
        Session session = new Session("http://localhost:8080", null, null);
        assertNotNull(session);
    }

    @Test(expected = MalformedURLException.class)
    public void testParamValidation4() throws Exception {
        Session session = new Session(null, config.get(Property.USER), config.get(Property.CLIENT));
        assertTrue("A MalformedURLException was expected, but was not thrown", false);
    }
}
