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

/**
 * Exception making a Narwhal RPC call.
 */
public class RpcException extends Exception {

    /**
     * Constructor creating a new instance.
     *
     * @param s of type String is the message
     */
    public RpcException(String s) {
        super(s);
    }

    /**
     * Constructor creating a new instance.
     *
     * @param s of type String is the message
     * @param throwable of type Throwable is the throwable exception
     */
    public RpcException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
