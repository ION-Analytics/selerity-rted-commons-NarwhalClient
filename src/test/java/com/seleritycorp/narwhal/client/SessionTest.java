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
