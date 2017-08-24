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

package com.digitalascent.cfn.core.cfnresourcespecification

import spock.lang.Shared
import spock.lang.Specification

class ResourceSpecificationServiceTest extends Specification {

    @Shared
    ResourceSpecificationService resourceSpecificationService;

    def setupSpec() {
        resourceSpecificationService = new ResourceSpecificationServiceImpl()
    }

    def "invalid type throws exception"() {
        when:
        resourceSpecificationService.findPropertyNameFor("AWS::EC2::SomeInvalidResourceType","foo")

        then:
        thrown ResourceSpecException
    }

    def "invalid property throws exception"() {
        when:
        resourceSpecificationService.findPropertyNameFor("AWS::EC2::Instance","foo")

        then:
        thrown ResourceSpecException
    }

    def "property resolved"() {
        when:
        String propertyName = resourceSpecificationService.findPropertyNameFor("AWS::EC2::Instance","properties.instanceType")

        then:
        propertyName == "InstanceType"
    }

    def "nested property resolved"() {
        when:
        String propertyName = resourceSpecificationService.findPropertyNameFor("AWS::AutoScaling::LaunchConfiguration","blockDeviceMappings.deviceName")

        then:
        propertyName == "DeviceName"
    }

    def "indexed property resolved"() {
        when:
        String propertyName = resourceSpecificationService.findPropertyNameFor("AWS::AutoScaling::LaunchConfiguration","blockDeviceMappings[0].deviceName")

        then:
        propertyName == "DeviceName"
    }

    def "tags resolved"() {
        when:
        String propertyName = resourceSpecificationService.findPropertyNameFor("AWS::EC2::Instance","tags")

        then:
        propertyName == "Tags"
    }
}
