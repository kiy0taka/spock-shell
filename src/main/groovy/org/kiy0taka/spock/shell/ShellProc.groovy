package org.kiy0taka.spock.shell

class ShellProc {

    File dir
    Map<String, String> env
    List<String> mockScripts
    String command
    int status
    String stdout
    String stderr

    @Lazy
    List<String> lines = { stdout.split('\n') }()

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

        (status, stderr, stdout) = [p.waitFor(), p.err.text, p.text]

        println stdout
        System.err.println stderr
    }
}
