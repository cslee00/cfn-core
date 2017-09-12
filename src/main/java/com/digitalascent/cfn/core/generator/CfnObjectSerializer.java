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

package com.digitalascent.cfn.core.generator;

import com.digitalascent.cfn.core.cfnresourcespecification.ResourceSpecException;
import com.digitalascent.cfn.core.cfnresourcespecification.ResourceSpecificationService;
import com.digitalascent.cfn.core.domain.CfnObject;
import com.digitalascent.cfn.core.domain.CfnResource;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableMap.toImmutableMap;

/**
 * Jackson serializer that handles CfnObjects, marshalling their dynamic properties.
 * <p>
 * Resolves property names against the <a href="http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/cfn-resource-specification.html">CloudFormation CfnResource Specification</a>
 * via {@link com.digitalascent.cfn.core.cfnresourcespecification.ResourceSpecificationService}, to ensure the correct case.
 *
 * Unknown property names are generate with their first character in upper case (all other characters unchanged), with a warning emitted.
 * </p>
 *
 */
final class CfnObjectSerializer extends StdSerializer<CfnObject> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final long serialVersionUID = 83930204L;
    private final ResourceSpecificationService resourceSpecificationService;

    private final PropertyNamingStrategy.UpperCamelCaseStrategy namingStrategy = new PropertyNamingStrategy.UpperCamelCaseStrategy();

    private final Map<String,String> resourceAttributeMap;

    CfnObjectSerializer(ResourceSpecificationService resourceSpecificationService) {
        super(CfnObject.class);
        this.resourceSpecificationService = checkNotNull(resourceSpecificationService, "resourceSpecificationService is required");

        Set<String> resourceAttributes = new LinkedHashSet<>();
        resourceAttributes.add("CreationPolicy");
        resourceAttributes.add("DeletionPolicy");
        resourceAttributes.add("UpdatePolicy");
        resourceAttributes.add("DependsOn");
        resourceAttributes.add("Metadata");
        resourceAttributes.add("Type");
        resourceAttributes.add("Properties");

        resourceAttributeMap = resourceAttributes.stream().collect( toImmutableMap( String::toLowerCase, Function.identity()));
    }

    @Override
    public void serialize(CfnObject cfnObject, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        cfnObject.getProperties().forEach((key, value) -> {
            try {
                gen.writeFieldName(fieldNameForProperty( key, cfnObject ));
                gen.writeObject(value);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        gen.writeEndObject();
    }

    private String fieldNameForProperty(String propertyName, CfnObject cfnObject ) {
        if( cfnObject instanceof CfnResource) {
            String retVal = resourceAttributeMap.get(propertyName.toLowerCase());
            if( retVal == null ) {
                throw new RuntimeException(String.format("Unable to map resource attribute '%s'", propertyName) );
            }
            return retVal;
        }

        try {
            return resourceSpecificationService.findPropertyNameFor(cfnObject.getResourceType(),cfnObject.getPropertyPath() + '.' + propertyName );
        } catch( ResourceSpecException e ) {
            logger.warn("Unknown property name '{}' : {}", propertyName, e.getMessage() );
        }

        // fallback, uppercase the first character
        return namingStrategy.translate(propertyName);
    }
}
