package com.digitalascent.cfn.core.cfnresourcespecification

import spock.lang.Specification

class ResourceSpecificationServiceTest extends Specification {
    def "invalid property throws exception"() {
        when:
        ResourceSpecificationService service = new ResourceSpecificationService()
        service.findPropertyNameFor("AWS::EC2::Instance","foo")

        then:
        thrown ResourceSpecException
    }

    def "property resolved"() {
        when:
        ResourceSpecificationService service = new ResourceSpecificationService()
        String propertyName = service.findPropertyNameFor("AWS::EC2::Instance","properties.instanceType")

        then:
        propertyName == "InstanceType"
    }

    def "nested property resolved"() {
        when:
        ResourceSpecificationService service = new ResourceSpecificationService()
        String propertyName = service.findPropertyNameFor("AWS::AutoScaling::LaunchConfiguration","blockDeviceMappings.deviceName")

        then:
        propertyName == "DeviceName"
    }

    def "tags resolved"() {
        when:
        ResourceSpecificationService service = new ResourceSpecificationService()
        String propertyName = service.findPropertyNameFor("AWS::EC2::Instance","tags")

        then:
        propertyName == "Tags"
    }
}
