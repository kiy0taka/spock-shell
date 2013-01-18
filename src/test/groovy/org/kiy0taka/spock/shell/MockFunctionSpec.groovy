package org.kiy0taka.spock.shell

import spock.lang.Specification

/**
 * @author Kiyotaka Oku
 */
class MockFunctionSpec extends Specification {

    ShellContext ctx

    void setup() {
        ctx = Mock()
    }

    def "stdout"() {

        setup:
        def mock = new MockFunction(ctx:ctx, func:{
            println 'Hello World'
        })

        when:
        def actual = mock.exec()

        then:
        actual == '''|cat << EOF
                     |Hello World
                     |EOF'''.stripMargin()
    }

    def "stderr"() {

        setup:
        def mock = new MockFunction(ctx:ctx, func:{
            System.err.println 'Hello World'
        })

        when:
        def actual = mock.exec()

        then:
        actual == '''|cat >&2 << EOF
                     |Hello World
                     |EOF'''.stripMargin()
    }

    def "stdout & stderr"() {

        setup:
        def mock = new MockFunction(ctx:ctx, func:{
            println '1'
            System.err.println '2'
            println '3'
            System.err.println '4'
        })

        when:
        def actual = mock.exec()

        then:
        actual == '''|cat << EOF
                     |1
                     |3
                     |EOF
                     |cat >&2 << EOF
                     |2
                     |4
                     |EOF'''.stripMargin()
    }

    def "redirect error stream"() {

        setup:
        ctx.isRedirectErrorStream() >> true
        def mock = new MockFunction(ctx:ctx, func:{
            println '1'
            System.err.println '2'
            println '3'
            System.err.println '4'
        })

        when:
        def actual = mock.exec()

        then:
        actual == '''|cat << EOF
                     |1
                     |2
                     |3
                     |4
                     |EOF'''.stripMargin()
    }

    def "print without line"() {

        setup:
        def mock = new MockFunction(ctx:ctx, func:{
            print 'Hello World'
        })

        when:
        def actual = mock.exec()

        then:
        actual == '''|cat << EOF
                     |Hello World
                     |EOF'''.stripMargin()
    }
}
