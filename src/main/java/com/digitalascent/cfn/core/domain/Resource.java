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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

public final class Resource extends CfnObject {
    private final String resourceName;
    private static final Pattern LOGICAL_ID_REGEX = Pattern.compile("[a-zA-Z0-9]+");
    private static final int MAX_LOGICAL_ID_LENGTH = 255;

    public Resource(String name, String type) {
        super(type, name);
        checkArgument(!isNullOrEmpty(type), "type is required to be non-null & not empty: %s", type);
        setProperty("type", type);
        this.resourceName = name;
        checkArgument(LOGICAL_ID_REGEX.matcher(name).matches(),
                "Invalid resource logical ID '%s'; must be alphanumeric [A-Za-z0-9]: http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/resources-section-structure.html", name);
        checkArgument(name.length() <= MAX_LOGICAL_ID_LENGTH, "Invalid resource LogicalID '%s'; length must be <= 255", name);
    }

    public static Resource create(String name, String type) {
        Resource res = new Resource(name, type);
        res.setProperty("properties", new CfnObject(type, "properties"));
        return res;
    }

    @SuppressWarnings({"unchecked","rawtypes"})
    public void tag(final String key, final String value) {
        CfnObject properties = (CfnObject) getProperty("properties");
        Map tags = (Map) properties.getProperty("tags");
        if (tags == null) {
            tags = new LinkedHashMap<>();
            properties.setProperty("tags", tags);
        }

        getLogger().info( "{}: tagging with {}={}", resourceName, key, value );
        tags.put(key, value);
    }

    public String getResourceName() {
        return resourceName;
    }
}
