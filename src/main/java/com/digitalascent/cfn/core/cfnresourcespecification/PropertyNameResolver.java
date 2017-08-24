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

final class PropertyNameResolver extends SimpleApplicationObject {
    private static final Pattern INDEX_PATTERN = Pattern.compile("\\[[0-9]+]$");

    private final ResourceSpecification resourceSpecification;

    PropertyNameResolver(ResourceSpecification resourceSpecification) {
        this.resourceSpecification = checkNotNull(resourceSpecification, "resourceSpecification is required");
    }

    String resolvePropertyName(String resourceType, String propertyPath) throws ResourceSpecException {
        String type = resourceType;
        CfnTypeSpecification typeSpec = findResourceType(resourceType);
        List<String> propertyNames = Splitter.on('.').splitToList(propertyPath);
        PropertySpecHolder propertySpecHolder = null;
        for (String propertyName : propertyNames) {

            // normalize out indexed properties, e.g. blockDeviceMappings[0] -> blockDeviceMappings
            propertyName = INDEX_PATTERN.matcher(propertyName).replaceAll("");

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

    private CfnTypeSpecification findPropertyType(String resourceType, String itemType) {
        Set<String> searchPatterns = ImmutableSet.of(resourceType + '.' + itemType, itemType);
        for (String searchPattern : searchPatterns) {
            CfnTypeSpecification typeSpec = resourceSpecification.getPropertyTypes().get(searchPattern);
            if (typeSpec != null) {
                getLogger().debug("Resolved property type {}", itemType);
                return typeSpec;
            }
        }
        throw new ResourceSpecException("Unable to locate property type: " + itemType);
    }

    private static class PropertySpecHolder {
        private final String formalPropertyName;
        private final CfnPropertySpecification propertySpecification;

        PropertySpecHolder(String formalPropertyName, CfnPropertySpecification propertySpecification) {
            this.formalPropertyName = checkNotNull(formalPropertyName, "formalPropertyName is required");
            this.propertySpecification = checkNotNull(propertySpecification, "propertySpecification is required");
        }

        public String getFormalPropertyName() {
            return formalPropertyName;
        }

        public CfnPropertySpecification getPropertySpecification() {
            return propertySpecification;
        }
    }

    private PropertySpecHolder findPropertySpec(String resourceType, String propertyPath, CfnTypeSpecification typeSpec, String propertyName) {
        for (String formalPropertyName : typeSpec.getProperties().keySet()) {
            if (formalPropertyName.equalsIgnoreCase(propertyName)) {
                getLogger().debug("Resolved property name '{}' -> '{}'", propertyName, formalPropertyName);
                return new PropertySpecHolder(formalPropertyName, typeSpec.getProperties().get(formalPropertyName));
            }
        }

        throw new ResourceSpecException(String.format("Unable to locate property '%s' on type %s for path %s", propertyName, resourceType, propertyPath));
    }

    private CfnTypeSpecification findResourceType(String resourceType) {
        CfnTypeSpecification typeSpec = resourceSpecification.getResourceTypes().get(resourceType);
        if (typeSpec == null) {
            throw new ResourceSpecException("Unable to locate resource type: " + resourceType);
        }
        getLogger().debug("Resolved resource type {}", resourceType);
        return typeSpec;
    }
}
