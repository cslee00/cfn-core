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

import com.digitalascent.base.core.SimpleApplicationObject;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

@SuppressWarnings("unused")
public final class ResourceSpecificationService extends SimpleApplicationObject {

    public ResourceSpecificationService() {
        ResourceSpecificationLoader loader = new ResourceSpecificationLoader();
        this.resourceSpecification = loader.loadResourceSpecification();
    }

    private final ResourceSpecification resourceSpecification;

    private static final Pattern INDEX_PROP_PATTERN = Pattern.compile("\\[[0-9]+\\]$");

    public String findPropertyNameFor(String resourceType, String propertyPath) throws ResourceSpecException {
        String type = resourceType;
        ResourceSpecification.CfnTypeSpecification typeSpec = findResourceType(resourceType);
        List<String> propertyNames = Splitter.on('.').splitToList(propertyPath);
        PropertySpecHolder propertySpecHolder = null;
        for (String propertyName : propertyNames) {

            // normalize out indexed properties, e.g. blockDeviceMappings[0] -> blockDeviceMappings
            propertyName = propertyName.replaceAll("\\[[0-9]+\\]$", "");

            if ("properties".equalsIgnoreCase(propertyName)) {
                continue;
            }
            propertySpecHolder = findPropertySpec(type, propertyPath, typeSpec, propertyName);
            String itemType = propertySpecHolder.getPropertySpecification().getItemType();
            if (isNullOrEmpty(itemType)) {
                continue;
            }
            type = itemType;
            typeSpec = findPropertyType(resourceType, itemType);
        }

        if (propertySpecHolder == null) {
            throw new ResourceSpecException(String.format("Unable to locate resource spec data for '%s' property path = %s", resourceType, propertyPath));
        }

        return propertySpecHolder.getFormalPropertyName();
    }

    private ResourceSpecification.CfnTypeSpecification findPropertyType(String resourceType, String itemType) {
        Set<String> searchPatterns = ImmutableSet.of(resourceType + "." + itemType, itemType);
        ResourceSpecification.CfnTypeSpecification typeSpec;
        for (String searchPattern : searchPatterns) {
            typeSpec = resourceSpecification.getPropertyTypes().get(searchPattern);
            if (typeSpec != null) {
                getLogger().debug("Resolved property type {}", itemType);
                return typeSpec;
            }
        }
        throw new ResourceSpecException("Unable to locate property type: " + itemType);
    }

    private static class PropertySpecHolder {
        private final String formalPropertyName;
        private final ResourceSpecification.CfnPropertySpecification propertySpecification;

        public PropertySpecHolder(String formalPropertyName, ResourceSpecification.CfnPropertySpecification propertySpecification) {
            this.formalPropertyName = checkNotNull(formalPropertyName, "formalPropertyName is required");
            this.propertySpecification = checkNotNull(propertySpecification, "propertySpecification is required");
        }

        public String getFormalPropertyName() {
            return formalPropertyName;
        }

        public ResourceSpecification.CfnPropertySpecification getPropertySpecification() {
            return propertySpecification;
        }
    }

    private PropertySpecHolder findPropertySpec(String resourceType, String propertyPath, ResourceSpecification.CfnTypeSpecification typeSpec, String propertyName) {
        for (String formalPropertyName : typeSpec.getProperties().keySet()) {
            if (formalPropertyName.equalsIgnoreCase(propertyName)) {
                getLogger().debug("Resolved property name '{}' -> '{}'", propertyName, formalPropertyName);
                return new PropertySpecHolder(formalPropertyName, typeSpec.getProperties().get(formalPropertyName));
            }
        }

        throw new ResourceSpecException(String.format("Unable to locate property '%s' on type %s for path %s", propertyName, resourceType, propertyPath));
    }

    private ResourceSpecification.CfnTypeSpecification findResourceType(String resourceType) {
        ResourceSpecification.CfnTypeSpecification typeSpec = resourceSpecification.getResourceTypes().get(resourceType);
        if (typeSpec == null) {
            throw new ResourceSpecException("Unable to locate resource type: " + resourceType);
        }
        getLogger().debug("Resolved resource type {}", resourceType);
        return typeSpec;
    }
}
