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

    @LocalData('test1')
    def 'use local data'() {
        when:
        exec 'cat hoge.txt'

        then:
        lines[0] == 'this is hoge.txt'
    }

    def 'copy resources to the workspace'() {
        given:
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
        when:
        resources 'hoge2'

        then:
        thrown(FileNotFoundException)

    }
}
