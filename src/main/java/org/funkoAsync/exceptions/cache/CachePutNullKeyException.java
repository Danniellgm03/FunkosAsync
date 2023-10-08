package org.funkoAsync.exceptions.cache;

public class CachePutNullKeyException extends RuntimeException{
    public CachePutNullKeyException(String msg) {
        super(msg);
    }
}
