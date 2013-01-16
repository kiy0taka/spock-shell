package org.kiy0taka.spock.shell

import groovy.ui.SystemOutputInterceptor

/**
 * @author Kiyotaka Oku
 */
class MockFunction {

    boolean redirectErrorStream
    Closure func

    String exec(List args) {
        def stdout = new Out()
        def stderr = redirectErrorStream ? stdout : new Out(error:true)
        def interceptors = [
            new SystemOutputInterceptor({ stdout.append(it); false }),
            new SystemOutputInterceptor({ stderr.append(it); false }, false)
        ]
        interceptors*.start()
        try {
            func(args)
        } finally {
            interceptors*.stop()
        }

        if (redirectErrorStream) {
            stdout.toString()
        } else {
            [stdout.toString(), stderr.toString()].grep().join('\n')
        }
    }

    static class Out {

        String out = ''
        boolean error

        void append(String str) {
            out += str
        }

        @Override
        String toString() {
            out ? """|cat ${error ? '>&2 ' : ''}<< EOF
                     |${out - ~/\n$/}
                     |EOF""".stripMargin() : out
        }
    }
}
