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
