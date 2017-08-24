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

import com.google.common.base.MoreObjects;

import java.util.HashMap;
import java.util.Map;

/**
 * http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/cfn-resource-specification.html
 */
@SuppressWarnings("unused")
public final class ResourceSpecification {

    private final Map<String, CfnTypeSpecification> resourceTypes = new HashMap<>();

    public Map<String, CfnTypeSpecification> getPropertyTypes() {
        return propertyTypes;
    }

    private final Map<String, CfnTypeSpecification> propertyTypes = new HashMap<>();

    public String getResourceSpecificationVersion() {
        return resourceSpecificationVersion;
    }

    private String resourceSpecificationVersion;

    public Map<String, CfnTypeSpecification> getResourceTypes() {
        return resourceTypes;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("resourceSpecificationVersion", resourceSpecificationVersion)
                .add("resourceTypes", resourceTypes)
                .add("propertyTypes", propertyTypes)
                .toString();
    }
}
