package com.digitalascent.cfndsl.base

import com.digitalascent.cfn.core.domain.CfnObject
import com.digitalascent.cfn.core.domain.IntrinsicFunction
import com.digitalascent.cfn.core.domain.CfnIntrinsicFunctions
import com.digitalascent.cfn.core.domain.PropertyFrozenException
import com.digitalascent.cfn.core.domain.Resource
import com.digitalascent.cfn.core.strategy.DefaultImmutabilityStrategy
import com.digitalascent.cfn.core.strategy.ImmutabilityStrategy
import spock.lang.Specification

class CfnObjectTest extends Specification {
    def "test dynamic property get/set"() {
        setup:
        CfnObject obj = new CfnObject("type", "abc")

        when:
        obj.someProperty = "someValue"

        then: "property set"
        obj.someProperty == "someValue"
        obj.getProperty("someProperty") == "someValue"
    }

    def "test property list"() {
        setup:
        CfnObject obj = new CfnObject("type", "abc")

        when:
        obj.someProperty = "someValue"

        then: "property set"
        obj.getProperties().keySet() == ["someProperty"] as Set
    }

    def "closures evaluated"() {
        setup:
        CfnObject obj = new CfnObject("type", "abc")

        when:
        obj.someProperty = {
            a = "b"
            c = "d"
        }

        then:
        obj.someProperty != null
        obj.someProperty.getClass() == CfnObject.class
        obj.someProperty.a == "b"
        obj.someProperty.c == "d"
    }

    def "closures evaluated in iterable"() {
        setup:
        CfnObject obj = new CfnObject("type", "abc")

        when:
        obj.someProperty = [{
                                a = "b"
                                c = "d"
                            },
                            {
                                e = "f"
                                g = "h"
                            }]

        then:
        obj.someProperty != null
        obj.someProperty instanceof Iterable
        obj.someProperty.size() == 2
        obj.someProperty[0].a == "b"
        obj.someProperty[0].c == "d"
        obj.someProperty[1].e == "f"
        obj.someProperty[1].g == "h"
    }

    def "resource replaced with ref"() {
        setup:
        CfnObject obj = new CfnObject("type", "abc")
        Resource res = Resource.create("myResource1", "AWS::EC2::Instance")

        when:
        obj.someProperty = res

        then:
        obj.someProperty != null
        obj.someProperty.getClass() == IntrinsicFunction.class
        obj.someProperty.functionName == "Ref"
        obj.someProperty.arguments == "myResource1"
    }

    def "cannot overwrite closure"() {
        setup:
        CfnObject obj = new CfnObject("type", "abc")

        when:
        obj.someProperty = "abc"
        obj.someProperty = {
            a = "b"
        }

        then:
        thrown IllegalArgumentException
    }

    // merge closures
    def "closures merged"() {
        setup:
        CfnObject obj = new CfnObject("type", "abc")

        when:
        obj.someProperty = {
            a = "b"
        }
        obj.someProperty = {
            c = "d"
        }

        then:
        obj.someProperty != null
        obj.someProperty.a == "b"
        obj.someProperty.c == "d"
    }

    def "intrinsic functions resolved"() {
        setup:
        CfnObject obj = new CfnObject("type", "abc")

        when:
        obj.properties = {
            a = ref "b"
        }

        then:
        obj.properties.a.functionName == "Ref"
        obj.properties.a.arguments == "b"
    }



    def "properties frozen"() {
        setup:
        CfnObject obj = new CfnObject("type", "abc")
        obj.properties = {
            a = "b"
        }
        ImmutabilityStrategy strategy = new DefaultImmutabilityStrategy()
        obj.makeImmutable(strategy)

        when:
        obj.properties.a = "c"

        then:
        thrown PropertyFrozenException

    }

    def "properties frozen via map access"() {
        setup:
        CfnObject obj = new CfnObject("type", "abc")
        obj.properties = {
            a = "b"
        }
        ImmutabilityStrategy strategy = new DefaultImmutabilityStrategy()
        obj.makeImmutable(strategy)

        when:
        obj.properties["a"] = "c"

        then:
        thrown PropertyFrozenException

    }

    def "properties frozen via map access 2"() {
        setup:
        CfnObject obj = new CfnObject("type", "abc")
        obj.properties = {
            a = "b"
        }
        ImmutabilityStrategy strategy = new DefaultImmutabilityStrategy()
        obj.makeImmutable(strategy)


        when:
        obj.getProperties()["properties"].a = "c"

        then:
        thrown PropertyFrozenException

    }

    def "properties frozen via map access 3"() {
        setup:
        CfnObject obj = new CfnObject("type", "abc")
        obj.properties = {
            a = "b"
        }
        ImmutabilityStrategy strategy = new DefaultImmutabilityStrategy()
        obj.makeImmutable(strategy)


        when:
        obj.getProperties()["properties"] = "c"

        then:
        thrown UnsupportedOperationException

    }

    def "multi-level properties frozen"() {
        setup:
        CfnObject obj = new CfnObject("type", "abc")
        obj.properties = {
            a = {
                b = "c"
            }
        }
        ImmutabilityStrategy strategy = new DefaultImmutabilityStrategy()
        obj.makeImmutable(strategy)

        when:
        obj.properties.a.b = "d"

        then:
        thrown PropertyFrozenException

    }

    def "collection properties frozen"() {
        setup:
        CfnObject obj = new CfnObject("type", "abc")
        obj.properties = {
            a = ["a","b","c"]
        }
        ImmutabilityStrategy strategy = new DefaultImmutabilityStrategy()
        obj.makeImmutable(strategy)

        when:
        obj.properties.a.clear()

        then:
        thrown UnsupportedOperationException
    }

    def "map properties frozen"() {
        setup:
        CfnObject obj = new CfnObject("type", "abc")
        obj.properties = {
            a = ["a":"b","c":"d"]
        }
        ImmutabilityStrategy strategy = new DefaultImmutabilityStrategy()
        obj.makeImmutable(strategy)

        when:
        obj.properties.a.put "a", "ddd"

        then:
        thrown UnsupportedOperationException
    }

    def "cfnObject inside collection frozen"() {
        setup:
        CfnObject obj = new CfnObject("type", "abc")
        obj.properties = {
            a = ["a","b",{
                c = "d"
            }]
        }
        ImmutabilityStrategy strategy = new DefaultImmutabilityStrategy()
        obj.makeImmutable(strategy)

        when:
        obj.properties.a[2].c="e"

        then:
        thrown PropertyFrozenException
    }

    def "properties frozen new properties allowed"() {
        setup:
        CfnObject obj = new CfnObject("type", "abc")
        obj.properties = {
            a = "b"
        }
        ImmutabilityStrategy strategy = new DefaultImmutabilityStrategy()
        obj.makeImmutable(strategy)

        when:
        obj.properties.c = "d"

        then:
        obj.properties.c == "d"
    }

    def "properties frozen can add tag"() {
        setup:
        CfnObject obj = new CfnObject("type", "abc")
        obj.properties = {
            tags = ["a":"b"]
        }
        ImmutabilityStrategy strategy = new DefaultImmutabilityStrategy()
        obj.makeImmutable(strategy)

        when:
        obj.properties.tags.put "c", "d"

        then:
        obj.properties.tags == ["a":"b","c":"d"]
    }
}
