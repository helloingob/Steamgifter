package com.helloingob.gifter.utilities.exception;

import org.apache.http.client.ClientProtocolException;

public class CircularException extends Exception {

    private static final long serialVersionUID = 1L;
    private ClientProtocolException clientProtocolException;

    public CircularException(ClientProtocolException clientProtocolException) {
        this.clientProtocolException = clientProtocolException;
    }

    public ClientProtocolException getException() {
        return clientProtocolException;
    }

    public Boolean isSuspended() {
        return clientProtocolException.getCause().getMessage().contains("http://www.steamgifts.com/suspensions");
    }

}
