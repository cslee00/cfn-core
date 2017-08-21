package com.digitalascent.cfn.core.cfnresourcespecification;

import com.google.common.base.MoreObjects;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public final class ResourceSpecification {
    public static class CfnTypeSpecification {
        private String documentation;

        private Map<String, CfnPropertySpecification> properties = new HashMap<>();

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

    private Map<String, CfnTypeSpecification> resourceTypes = new HashMap<>();

    public Map<String, CfnTypeSpecification> getPropertyTypes() {
        return propertyTypes;
    }

    private Map<String, CfnTypeSpecification> propertyTypes = new HashMap<>();

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
