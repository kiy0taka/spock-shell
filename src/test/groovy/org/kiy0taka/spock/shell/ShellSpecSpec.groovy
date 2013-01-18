package org.kiy0taka.spock.shell

class ShellSpecSpec extends ShellSpec {

    def "get status"() {
        when:
        run 'status.sh', arg

        then:
        status == expected

        where:
        arg   | expected
        'yes' | 0
        'no'  | 1
    }

    def "get output"() {
        when:
        run 'output.sh'

        then:
        stdout == '''|1
                     |3
                     |'''.stripMargin()

        and:
        stderr == '''|2
                     |4
                     |'''.stripMargin()
    }

    def "redirect error stream"() {
        given:
        redirectErrorStream true

        when:
        run 'output.sh'

        then:
        stdout == '''1
                    |2
                    |3
                    |4
                    |'''.stripMargin()

        and:
        stderr == ''
    }

    def "use env"() {
        given:
        export 'FOO', 'foo'
        export 'BAR', 'bar'

        when:
        exec 'env'

        then:
        lines.grep('FOO=foo')

        and:
        lines.grep('BAR=bar')
    }

    def "execute command"() {
        when:
        exec 'expr 1 + 1'

        then:
        lines[0] == '2'
    }

    def 'current directory should be temporary'() {
        when:
        exec 'pwd'

        then:
        lines[0] == tempFolder.root.canonicalPath
    }

    def 'use mock scripts'() {
        given:
        mockScript 'mock.sh'

        when:
        run 'run.sh'

        then:
        lines[0] == 'called curl http://www.google.com'
    }

    def 'copy resources to the workspace'() {

        given: 'copy src/test/resources/test2 to workspace'
        resources 'test2'

        when:
        exec 'ls -1'

        then:
        stdout == '''foo.txt
                     |sub-folder
                     |'''.stripMargin()

        when:
        exec 'ls -1 sub-folder'

        then:
        stdout == '''foo2.txt
                     |'''.stripMargin()
    }

    def 'not exist resources directory should throws exception'() {
        when: 'no such directory src/test/resources/hoge.'
        resources 'hoge'

        then:
        thrown(FileNotFoundException)
    }

    def 'assert stdout, use mock function'() {
        given:
        mockFunction('curl') { args ->
            println "called curl ${args}"
        }

        when:
        exec 'curl http://www.example.org'

        then:
        lines[0] == 'called curl [http://www.example.org]'
    }

    def 'assert stderr, use mock function'() {
        given:
        mockFunction('curl') { args ->
            System.err.println "called curl ${args}"
        }

        when:
        exec 'curl http://www.example.org'

        then:
        stderr == 'called curl [http://www.example.org]\n'
    }

    def 'assert stdout/stderr, use mock function'() {
        given:
        mockFunction('curl') { args ->
            println '1'
            System.err.println '2'
            println '3'
            System.err.println '4'
        }

        when:
        exec 'curl http://www.example.org'

        then:
        stdout == '1\n3\n'

        and:
        stderr == '2\n4\n'
    }

    def 'redirect error stream, use mock function'() {
        given:
        mockFunction('curl') { args ->
            println '1'
            System.err.println '2'
            println '3'
            System.err.println '4'
        }
        redirectErrorStream true

        when:
        exec 'curl http://www.example.org'

        then:
        stdout == '1\n2\n3\n4\n'

        and:
        stderr == ''
    }

    def 'mock function status'() {
        given:
        mockFunction('curl') {
            return arg
        }

        when:
        exec 'curl http://www.example.org'

        then:
        status == expected

        where:
        arg  | expected
        0    | 0
        1    | 1
        null | 0
        'a'  | 0
        '1'  | 0
    }

    def 'mock function throws Exception'() {
        given:
        mockFunction('curl') {
            throw new RuntimeException('error!')
        }

        when:
        exec 'curl http://www.example.org'

        then:
        status == 255

        and:
        stderr.split(/\n/)[0] == 'java.lang.RuntimeException: error!'
    }
}
