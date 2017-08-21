package com.digitalascent.cfn.core.domain;

public class PropertyFrozenException extends RuntimeException {
    private static final long serialVersionUID = 49278495938212904L;
    public PropertyFrozenException(String message) {
        super(message);
    }
}
