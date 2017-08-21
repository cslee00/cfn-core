package com.digitalascent.cfndsl.base

import com.digitalascent.cfn.core.cfnresourcespecification.ResourceSpecificationService
import com.digitalascent.cfn.core.domain.Resource
import com.digitalascent.cfn.core.output.JsonOutputter
import com.digitalascent.cfn.core.strategy.DefaultImmutabilityStrategy
import groovy.json.JsonSlurper
import spock.lang.Shared
import spock.lang.Specification

class ResourceJsonTest extends Specification{

    @Shared
    Resource subject

    @Shared
    ResourceSpecificationService resourceSpecificationService = new ResourceSpecificationService()

    def setupSpec() {
        Resource anotherResource = Resource.create("resource2","anotherResourceType")

        subject = Resource.create("resourceName", "AWS::EC2::Instance")

        subject.dependsOn = "someOtherRes"
        subject.creationPolicy = "creationPolicy"
        subject.properties = {
            instanceType = 'm4.xlarge'
            subnetID = ref("subnetParam1")
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

    Object marshall( Resource resource ) {
        ByteArrayOutputStream os = new ByteArrayOutputStream()
        new JsonOutputter(resourceSpecificationService).output(resource,os,true)
        return new JsonSlurper().parse(os.toByteArray() )
    }

}
