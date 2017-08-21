package com.digitalascent.cfn.core.output;

import com.digitalascent.cfn.core.domain.IntrinsicFunction;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

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
