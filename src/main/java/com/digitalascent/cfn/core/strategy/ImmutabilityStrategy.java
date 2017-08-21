package com.digitalascent.cfn.core.strategy;

import groovy.lang.Tuple;

public interface ImmutabilityStrategy {
    Tuple maybeMakeImmutable(String propertyPath, Object propertyValue);
}
