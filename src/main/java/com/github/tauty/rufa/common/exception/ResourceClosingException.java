package com.github.tauty.rufa.common.exception;

import com.github.tauty.rufa.common.util.Const;

public class ResourceClosingException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -2600393047737652150L;

    public ResourceClosingException(Throwable cause) {
        super("Additional exception has occured while closing resources :"
                + Const.CRLF + cause.getMessage(), cause);
    }
}