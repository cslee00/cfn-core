package com.digitalascent.base.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SimpleApplicationObject {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected final Logger getLogger() {
        return logger;
    }
}
