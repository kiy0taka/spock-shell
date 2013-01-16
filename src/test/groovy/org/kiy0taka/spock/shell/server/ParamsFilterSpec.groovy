package org.kiy0taka.spock.shell.server

import spock.lang.Specification

/**
 * @author Kiyotaka Oku
 */
class ParamsFilterSpec extends Specification {

    def "parse query"() {

        when:
        def actual = new ParamsFilter().parseQuery(query)

        then:
        actual == expected

        where:
        query                     | expected
        'args0=a'                 | ['a']
        'args0=a&args1=b&args2=c' | ['a', 'b', 'c']
        'args0=%E3%81%82'         | ['あ']
        ''                        | []
        'args1=b'                 | ['', 'b']
        'args0=a=b'               | ['a=b']
        'foo=bar'                 | []
    }
}
