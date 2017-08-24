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

package com.digitalascent.cfn.core.output;

import com.digitalascent.cfn.core.cfnresourcespecification.ResourceSpecificationService;
import com.digitalascent.cfn.core.domain.CfnObject;
import com.digitalascent.cfn.core.domain.IntrinsicFunction;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.io.OutputStream;

import static com.google.common.base.Preconditions.checkNotNull;

public final class JsonCloudFormationGenerator implements CloudFormationGenerator {

    private final ResourceSpecificationService resourceSpecificationService;

    public JsonCloudFormationGenerator(ResourceSpecificationService resourceSpecificationService) {
        this.resourceSpecificationService = checkNotNull(resourceSpecificationService, "resourceSpecificationService is required");
    }

    @Override
    public void generate(Object obj, OutputStream os, boolean prettyPrint) {
        ObjectMapper mapper = new ObjectMapper();

        mapper.configure(SerializationFeature.CLOSE_CLOSEABLE, false);
        mapper.configure(SerializationFeature.FLUSH_AFTER_WRITE_VALUE, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        if (prettyPrint) {
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
        }

        SimpleModule module = new SimpleModule();
        module.addSerializer(CfnObject.class, new CfnObjectSerializer(resourceSpecificationService));
        module.addSerializer(IntrinsicFunction.class, new IntrinsicFunctionSerializer());
        mapper.registerModule(module);

        try {
            mapper.writeValue(os, obj);

            os.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
