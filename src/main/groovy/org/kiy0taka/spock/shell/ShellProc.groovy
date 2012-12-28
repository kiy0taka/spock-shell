package org.kiy0taka.spock.shell

class ShellProc {

    File dir
    List<String> env = []
    List<String> mockScripts
    String preScript
    String command
    int status
    String stdout
    String stderr
    List<String> lines

    void exec() {
        def p = 'sh'.execute(env, dir)
        p.out.withWriter { w ->
            w << 'set -a\n'
            w.flush()
            mockScripts.each {
                w << "source $it\n"
                w.flush()
            }
            w << "$command\n"
            w.flush()
            w << 'exit $?'
            w.flush()
        }
        status = p.waitFor()
        stderr = p.err.text
        stdout = p.text
        lines = stdout.split('\n')
        println stdout
        System.err.println stderr
    }
}
