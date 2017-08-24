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

package com.digitalascent.cfn.core

import com.digitalascent.cfn.core.cfnresourcespecification.ResourceSpecificationService
import com.digitalascent.cfn.core.domain.CfnResource
import com.digitalascent.cfn.core.output.JsonCloudFormationGenerator
import com.digitalascent.cfn.core.strategy.DefaultImmutabilityStrategy
import groovy.json.JsonSlurper
import spock.lang.Shared
import spock.lang.Specification

class ResourceJsonTest extends Specification{

    @Shared
    CfnResource subject

    @Shared
    ResourceSpecificationService resourceSpecificationService = new ResourceSpecificationService()

    def setupSpec() {
        CfnResource anotherResource = CfnResource.create("resource2","anotherResourceType")

        subject = CfnResource.create("resourceName", "AWS::EC2::Instance")

        subject.dependsOn = "someOtherRes"
        subject.creationPolicy = "creationPolicy"
        subject.properties = {
            instanceType = 'm4.xlarge'
            subnetID = Ref("subnetParam1")
            blockDeviceMappings = [
                    {
                        deviceName = "abc"
                        virtualName = "blah"
                    }
            ]
            otherResource = anotherResource
            tags = [
                    "someTag"   : "someValue",
                    "anotherTag": "anotherValue"]

        }

        subject.properties = {
            anotherProp = "abc"
        }

        subject.makeImmutable(new DefaultImmutabilityStrategy())
    }

    def setup() {

    }

    def "type property marshalled"() {
        when:
        def response = marshall( subject )

        then:
        response["Type"] == "AWS::EC2::Instance"
    }

    def "no extra root properties marshalled"() {
        when:
        def response = marshall( subject )

        then:
        response.keySet() == ["Type","Properties","DependsOn","CreationPolicy"] as Set
    }

    def "properties marshalled"() {
        when:
        def response = marshall( subject )

        then:
        response["Properties"]["InstanceType"] == "m4.xlarge"
    }

    def "ref marshalled"() {
        when:
        def response = marshall( subject )

        then:
        response["Properties"]["SubnetId"]["Ref"] == "subnetParam1"
    }

    def "list marshalled"() {
        when:
        def response = marshall( subject )

        then:
        response["Properties"]["BlockDeviceMappings"].size() == 1
        response["Properties"]["BlockDeviceMappings"][0].DeviceName == "abc"
        response["Properties"]["BlockDeviceMappings"][0].VirtualName == "blah"
    }

    def "tags marshalled"() {
        when:
        def response = marshall( subject )

        then:
        response["Properties"]["Tags"] == ["someTag":"someValue","anotherTag":"anotherValue"]
    }

    def "implicit ref marshalled"() {
        when:
        def response = marshall( subject )

        then:
        response["Properties"]["OtherResource"]["Ref"] == "resource2"
    }

    def "merged properties closures marshalled"() {
        when:
        def response = marshall( subject )

        then:
        response["Properties"]["SubnetId"]["Ref"] == "subnetParam1"
        response["Properties"]["AnotherProp"] == "abc"
    }

    Object marshall(CfnResource resource ) {
        ByteArrayOutputStream os = new ByteArrayOutputStream()
        new JsonCloudFormationGenerator(resourceSpecificationService).generate(resource,os,true)
        return new JsonSlurper().parse(os.toByteArray() )
    }

}
