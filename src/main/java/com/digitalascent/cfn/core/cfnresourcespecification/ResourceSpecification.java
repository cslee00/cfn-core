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

@SuppressWarnings("unused")
public final class ResourceSpecification {
    public static class CfnTypeSpecification {
        private String documentation;

        private final Map<String, CfnPropertySpecification> properties = new HashMap<>();

        public String getDocumentation() {
            return documentation;
        }

        public Map<String, CfnPropertySpecification> getProperties() {
            return properties;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("documentation", documentation)
                    .add("properties", properties)
                    .toString();
        }
    }

    public static class CfnPropertySpecification {
        private String documentation;
        private String primitiveType;
        private boolean required;
        private String updateType;

        public String getItemType() {
            return itemType;
        }

        private String itemType;

        public String getDocumentation() {
            return documentation;
        }

        public String getPrimitiveType() {
            return primitiveType;
        }

        public boolean isRequired() {
            return required;
        }

        public String getUpdateType() {
            return updateType;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("documentation", documentation)
                    .add("primitiveType", primitiveType)
                    .add("required", required)
                    .add("updateType", updateType)
                    .toString();
        }
    }

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
