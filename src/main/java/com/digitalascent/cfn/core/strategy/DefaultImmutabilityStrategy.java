package com.digitalascent.cfn.core.strategy;

import com.google.common.collect.ImmutableList;
import groovy.lang.Tuple;

public class DefaultImmutabilityStrategy implements ImmutabilityStrategy {
    @Override
    public Tuple maybeMakeImmutable(String propertyPath, Object propertyValue) {
        return configurableImmutabilityStrategy.maybeMakeImmutable(propertyPath, propertyValue);
    }

    private final ImmutabilityStrategy configurableImmutabilityStrategy = new ConfigurableImmutabilityStrategy(ImmutableList.of("properties.tags"), ImmutableList.of());
}
