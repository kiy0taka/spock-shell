package org.kiy0taka.spock.shell

class ShellProc {

    File dir
    Map<String, String> env
    List<String> mockScripts
    String preScript
    String command
    int status
    String stdout
    String stderr
    List<String> lines
    boolean redirectErrorStream

    void exec() {
        def builder = new ProcessBuilder('sh').directory(dir)
        builder.redirectErrorStream(redirectErrorStream)
        builder.environment() << env
        def p = builder.start()
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
