package com.digitalascent.cfn.core.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

public final class IntrinsicFunction {
    private final String functionName;
    private final Object arguments;

    public IntrinsicFunction(String functionName, Object arguments) {
        checkArgument(!isNullOrEmpty(functionName), "functionName is required to be non-null & not empty: %s", functionName);
        this.functionName = functionName;
        this.arguments = checkNotNull(arguments, "arguments is required");
    }

    @Override
    public String toString() {
        return functionName + "(" + arguments + ")";
    }

    public String getFunctionName() {
        return functionName;
    }

    public Object getArguments() {
        return arguments;
    }
}
