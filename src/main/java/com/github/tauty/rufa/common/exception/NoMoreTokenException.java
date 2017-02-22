package com.github.tauty.rufa.common.exception;

import com.github.tauty.rufa.common.util.Const;

public class NoMoreTokenException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -1027160501289177579L;

    public NoMoreTokenException(String src) {
        super("No token is available. The source string is :" + Const.CRLF + src);
    }

}