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
        return functionName + '(' + arguments + ')';
    }

    public String getFunctionName() {
        return functionName;
    }

    public Object getArguments() {
        return arguments;
    }
}
