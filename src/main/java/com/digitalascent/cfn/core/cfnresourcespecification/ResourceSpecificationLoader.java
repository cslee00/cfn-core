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

import com.digitalascent.core.base.SimpleApplicationObject;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.google.common.io.CharSource;
import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

final class ResourceSpecificationLoader extends SimpleApplicationObject {

    ResourceSpecification loadResourceSpecification() {

        try {
            URL url = Resources.getResource(getClass(), "CloudFormationResourceSpecification.json");
            CharSource cs = Resources.asCharSource(url, StandardCharsets.UTF_8);

            getLogger().info("Loading CFN resource specification from {}", cs);
            ResourceSpecification spec = loadJson(cs);
            getLogger().info("CFN resource specification loaded, version = {}", spec.getResourceSpecificationVersion());
            return spec;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ResourceSpecification loadJson(CharSource cs) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
        mapper.configure(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(cs.openBufferedStream(), ResourceSpecification.class);
    }
}
