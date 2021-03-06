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

import com.google.common.base.MoreObjects;

@SuppressWarnings("unused")
public final class CfnPropertySpecification {
    private String primitiveType;
    private boolean required;
    private String updateType;
    private String itemType;

    CfnPropertySpecification() {
        // EMPTY
    }

    public String getItemType() {
        return itemType;
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
                .add("primitiveType", primitiveType)
                .add("required", required)
                .add("updateType", updateType)
                .add("itemType", itemType)
                .toString();
    }
}
