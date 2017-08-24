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

import com.digitalascent.cfn.core.domain.IntrinsicFunction;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * Jackson serializer that serializes CloudFormation intrinsic functions.
 */
final class IntrinsicFunctionSerializer extends StdSerializer<IntrinsicFunction> {

    private static final long serialVersionUID = 84929048592303L;

    IntrinsicFunctionSerializer() {
        super(IntrinsicFunction.class);
    }

    @Override
    public void serialize(IntrinsicFunction intrinsicFunction, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeFieldName(intrinsicFunction.getFunctionName());
        gen.writeObject(intrinsicFunction.getArguments());
        gen.writeEndObject();
    }
}
