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

package com.digitalascent.cfn.core.cfnresourcespecification;

import com.digitalascent.core.base.SimpleApplicationObject;
import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/cfn-resource-specification.html
 */
@SuppressWarnings("unused")
public final class ResourceSpecificationServiceImpl extends SimpleApplicationObject implements ResourceSpecificationService {
    private final PropertyNameResolver propertyNameResolver;
    private final List<String> resourceTypes;

    public ResourceSpecificationServiceImpl() {
        ResourceSpecificationLoader loader = new ResourceSpecificationLoader();
        ResourceSpecification resourceSpecification = loader.loadResourceSpecification();
        this.propertyNameResolver = new PropertyNameResolver(resourceSpecification);
        this.resourceTypes = ImmutableList.copyOf(resourceSpecification.getResourceTypes().keySet());
    }

    @Override
    public String findPropertyNameFor(String resourceType, String propertyPath) throws ResourceSpecException {
        return propertyNameResolver.resolvePropertyName(resourceType,propertyPath);
    }

    @Override
    public List<String> listResourceTypes() {
        return resourceTypes;
    }
}
