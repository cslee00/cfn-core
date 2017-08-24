/*
 * Copyright 2017-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.digitalascent.cfn.core.domain;

import com.digitalascent.cfn.core.strategy.ImmutabilityStrategy;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.MissingPropertyException;
import groovy.lang.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.ImmutableList.toImmutableList;

public class CfnObject extends GroovyObjectSupport implements CfnIntrinsicFunctions, CfnPseudoParameters {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Map<String, Object> dynamicProperties = new LinkedHashMap<>();
    private final String propertyPath;
    private final String resourceType;
    private Set<String> frozenProperties = new HashSet<>();

    public CfnObject(String resourceType, String propertyPath) {
        checkArgument(!isNullOrEmpty(resourceType), "resourceType is required to be non-null & not empty: %s", resourceType);
        checkArgument(!isNullOrEmpty(propertyPath), "propertyPath is required to be non-null & not empty: %s", propertyPath);
        this.propertyPath = propertyPath;
        this.resourceType = resourceType;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getPropertyPath() {
        return propertyPath;
    }

    protected final Logger getLogger() {
        return logger;
    }

    public final Map<String, Object> getProperties() {
        return ImmutableMap.copyOf(dynamicProperties);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void setProperty(final String propertyName, Object propertyValue) {
        checkArgument(!isNullOrEmpty(propertyName), "propertyName is required to be non-null & not empty: %s", propertyName);

        if (frozenProperties.contains(propertyName)) {
            throw new PropertyFrozenException(propertyPath + '.' + propertyName + " is frozen");
        }

        Object targetValue = propertyValue;
        final Object currentValue = dynamicProperties.get(propertyName);

        // execute closures inside of lists, sets
        if (propertyValue instanceof Collection) {
            targetValue = handleCollection((Collection<Object>) propertyValue, propertyName);
        }

        // execute closures
        if (propertyValue instanceof Closure) {
            targetValue = handleClosure(propertyName, (Closure) propertyValue, currentValue);
        }

        // replace with Ref to other resource
        if (propertyValue instanceof CfnResource) {
            targetValue = Ref(propertyValue);
        }

        // only generate log info on properties, not entire nested objects
        if (!(targetValue instanceof CfnObject)) {
            if (currentValue == null) {
                logger.info("{}: setting '{}' to '{}'", propertyPath, propertyName, targetValue);
            } else {
                logger.info("{}: overriding '{}'; replacing '{}' with '{}'", propertyPath, propertyName, currentValue, targetValue);
            }

        }
        dynamicProperties.put(propertyName, targetValue);
    }

    @Override
    public final String toString() {
        return dynamicProperties.toString();
    }

    private List<Object> handleCollection(final Collection<Object> collection, final String property) {
        AtomicLong counter = new AtomicLong();

        return collection.stream().map(item -> {
            if (item instanceof Closure) {
                return handleClosure(String.format("%s[%d]", property, counter.getAndIncrement()), (Closure) item, null);
            }
            if (item instanceof CfnResource) {
                return Ref(item);
            }
            return item;
        }).collect(toImmutableList());
    }

    @Override
    public final Object getProperty(String property) {
        if (!dynamicProperties.containsKey(property)) {
            throw new MissingPropertyException(property);
        }

        return dynamicProperties.get(property);
    }

    private CfnObject handleClosure(final String name, Closure<?> closure, @Nullable final Object currentValue) {
        if ((currentValue != null) && !(currentValue instanceof CfnObject)) {
            throw new IllegalArgumentException("Cannot overwrite CfnObject with " + currentValue.getClass());
        }

        // merge properties into existing object if it exists
        CfnObject delegate = (currentValue != null) ? (CfnObject) currentValue : new CfnObject(resourceType, propertyPath + '.' + name);

        closure.setDelegate(delegate);
        closure.setResolveStrategy(Closure.DELEGATE_FIRST);
        closure.call();
        return delegate;
    }

    public final void makeImmutable(final ImmutabilityStrategy immutabilityStrategy) {
        String propertyPathRoot = propertyPath;
        propertyPathRoot = propertyPathRoot.contains(".") ? propertyPathRoot.substring(propertyPathRoot.indexOf('.') + 1) : "";

        String finalPropertyPathRoot = propertyPathRoot;
        final Set<String> tempFrozenProperties = new LinkedHashSet<>();
        dynamicProperties.forEach((key, value) -> {
            String propertyPath = finalPropertyPathRoot + '.' + key;
            if (finalPropertyPathRoot.isEmpty()) {
                propertyPath = key;
            }

            Tuple result = immutabilityStrategy.maybeMakeImmutable(propertyPath, value);
            if (result != null) {
                // prevent overwriting the property entirely
                tempFrozenProperties.add(key);

                // store (semi)immutable value (collections, maps, CfnObjects inside collections & maps)
                dynamicProperties.put(key, result.get(0));
            }
        });

        frozenProperties = ImmutableSet.copyOf(tempFrozenProperties);
    }
}
