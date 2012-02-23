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

package com.selerity.narwhal.client;

/**
 * Exception from the remote service.
 */
public class RemoteException extends RpcException {
    /**
     * The numeric error code returned from the server.
     */
    public final long errorCode;

    /**
     * Constructor RemoteException creates a new RemoteException instance.
     *
     * @param message of type String is the message from rhino
     * @param errorCode of type long is error code from rhino
     */
    public RemoteException(String message, long errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Instantiate a Remote exception with a Response.Error
     * @param error the error
     */
    public RemoteException(Response.Error error) {
              this(error.getMessage(), error.getCode());
    }

    @Override
    public String toString() {
        return super.toString() + " errorCode: " + errorCode;
    }
}
