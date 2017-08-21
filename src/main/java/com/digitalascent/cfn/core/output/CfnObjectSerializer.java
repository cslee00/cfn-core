package com.digitalascent.cfn.core.output;

import com.digitalascent.cfn.core.cfnresourcespecification.ResourceSpecException;
import com.digitalascent.cfn.core.cfnresourcespecification.ResourceSpecificationService;
import com.digitalascent.cfn.core.domain.CfnObject;
import com.digitalascent.cfn.core.domain.Resource;
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
                gen.writeFieldName(fieldNameFor( key, cfnObject ));
                gen.writeObject(value);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        gen.writeEndObject();
    }

    private String fieldNameFor( String propertyName, CfnObject cfnObject ) {
        if( cfnObject instanceof Resource) {
            String retVal = resourceAttributeMap.get(propertyName.toLowerCase());
            if( retVal == null ) {
                throw new RuntimeException(String.format("Unable to map resource attribute %s", propertyName) );
            }
            return retVal;
        }

        try {
            propertyName = resourceSpecificationService.findPropertyNameFor(cfnObject.getResourceType(),cfnObject.getPropertyPath() + "." + propertyName );
        } catch( ResourceSpecException e ) {
            logger.warn("Unknown property name '{}' : {}", propertyName, e.getMessage() );
        }

        // fallback, uppercase the first character
        return namingStrategy.translate(propertyName);
    }
}