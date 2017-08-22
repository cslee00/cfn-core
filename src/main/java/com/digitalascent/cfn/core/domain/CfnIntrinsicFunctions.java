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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Contains <a href="http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference.html">CloudFormation intrinsic functions</a>
 */
@SuppressWarnings("unused")
public interface CfnIntrinsicFunctions {
    default IntrinsicFunction ref(Object ref) {
        checkNotNull(ref, "ref is required");

        String name = null;
        if (ref instanceof String) {
            name = (String) ref;
        }

        if (ref instanceof Resource) {
            name = ((Resource) ref).getResourceName();
        }
        checkArgument(name != null, "name != null; unknown type for ref : %s or resource name is null", ref.getClass());

        return new IntrinsicFunction("Ref", name);
    }

    default IntrinsicFunction fnSub(String input, Map<String,Object> replacementMap) {
        List<Object> list = new ArrayList<>(2);
        list.add(input);
        if ( !replacementMap.isEmpty()) {
            list.add(replacementMap);
        }
        return new IntrinsicFunction("Fn::Sub", list);
    }

    default IntrinsicFunction fnSub(String input) {
        return fnSub(input, ImmutableMap.of());
    }

    default IntrinsicFunction fnSplit(String delimiter, Object sourceString) {
        return new IntrinsicFunction("Fn::Split", ImmutableList.of(delimiter, sourceString));
    }

    default IntrinsicFunction fnSelect(int index, List<Object> list) {
        return new IntrinsicFunction("Fn::Select", ImmutableList.of(index, list));
    }

    default IntrinsicFunction fnImportValue(Object value) {
        return new IntrinsicFunction("Fn::ImportValue", value);
    }

    default IntrinsicFunction fnGetAZs(Object value) {
        return new IntrinsicFunction("Fn::GetAZs", value);
    }

    default IntrinsicFunction fnGetAtt(String logicalResourceName, Object attributeName) {
        return new IntrinsicFunction("Fn::GetAtt", ImmutableList.of(logicalResourceName, attributeName));
    }

    default IntrinsicFunction fnFindInMap(Object mapName, Object topLevelKey, Object secondLevelKey) {
        return new IntrinsicFunction("Fn::FindInMap", ImmutableList.of(mapName, topLevelKey, secondLevelKey));
    }

    default IntrinsicFunction fnBase64(Object valueToEncode) {
        return new IntrinsicFunction("Fn::Base64", valueToEncode);
    }

    @SuppressWarnings("rawtypes")
    default IntrinsicFunction fnAnd(List conditions) {
        return new IntrinsicFunction("Fn::And", conditions);
    }

    default IntrinsicFunction fnEquals(Object value1, Object value2) {
        return new IntrinsicFunction("Fn::Equals", ImmutableList.of(value1, value2));
    }

    default IntrinsicFunction fnIf(Object conditionName, Object valueIfTrue, Object valueIfFalse) {
        return new IntrinsicFunction("Fn::If", ImmutableList.of(conditionName, valueIfTrue, valueIfFalse));
    }

    default IntrinsicFunction fnNot(Object condition) {
        return new IntrinsicFunction("Fn::Not", ImmutableList.of(condition));
    }

    @SuppressWarnings("rawtypes")
    default IntrinsicFunction fnOr(List conditions) {
        return new IntrinsicFunction("Fn::Or", conditions);
    }
}
